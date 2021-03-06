package metalextras.client.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import api.metalextras.BlockOre;
import api.metalextras.ModelType;
import api.metalextras.OreType;
import api.metalextras.OreTypes;
import api.metalextras.OreUtils;
import metalextras.newores.NewOreType;
import metalextras.newores.modules.TypeModelModule;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.registries.GameData;

public class ModelOre implements IModel
{
	public static void reload(IResourceManager manager)
	{
		BakedModelOre.models.clear();
	}

	@Override
	public Collection<ResourceLocation> getDependencies()
	{
		Set<ResourceLocation> dependencies = Sets.newHashSet();
		for(ModelType material : ModelType.values())
		{
			dependencies.add(material.model_location);
			IModel model = ModelLoaderRegistry.getModelOrMissing(material.model_location);
			if(model != ModelLoaderRegistry.getMissingModel())
				dependencies.addAll(model.getDependencies());
			for(OreTypes types : OreUtils.getTypeCollectionsRegistry())
				for(OreType type : types)
					dependencies.addAll(type.getModel(material).getDependencies());
		}
		return dependencies;
	}

	@Override
	public Collection<ResourceLocation> getTextures()
	{
		Set<ResourceLocation> textures = Sets.newHashSet();
		for(ModelType material : ModelType.values())
			for(OreTypes types : OreUtils.getTypeCollectionsRegistry())
				for(OreType type : types)
					textures.addAll(type.getModel(material).getTextures());
		for(NewOreType type : OreUtils.getTypesRegistry())
			textures.add(type.getChildModule(TypeModelModule.class).getTexture());
		return textures;
	}

	@Override
	public IBakedModel bake(IModelState modelState, VertexFormat vertexFormat, Function<ResourceLocation, TextureAtlasSprite> textureGetter)
	{
		return new BakedModelOre(modelState, vertexFormat, textureGetter);
	}

	@Override
	public IModelState getDefaultState()
	{
		return ModelRotation.X0_Y0;
	}

	private static class BakedModelOre implements IBakedModel
	{
		private static final Map<IBlockState, IBakedModel> models = Maps.newHashMap();
		private final IModelState modelState;
		private final VertexFormat vertexFormat;
		private final Function<ResourceLocation, TextureAtlasSprite> textureGetter;

		private BakedModelOre(IModelState modelState, VertexFormat vertexFormat, Function<ResourceLocation, TextureAtlasSprite> textureGetter)
		{
			this.modelState = modelState;
			this.vertexFormat = vertexFormat;
			this.textureGetter = textureGetter;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
		{
			if(state != null)
			{
				IBakedModel baked_model = models.get(state);
				if(baked_model != null)
					return baked_model.getQuads(state, side, 0L);
				if(state.getBlock() instanceof BlockOre)
				{
					BlockOre ore = (BlockOre)state.getBlock();
					OreType type = ore.getOreType(state);
					if(type != null)
					{
						ResourceLocation type_name = type.getTexture();
						ResourceLocation name = new ResourceLocation(String.format("%s.%s", ore.getOreType().getChildModule(TypeModelModule.class).getTexture(), String.format("%s_%s", type_name.getResourceDomain(), type_name.getResourcePath())));
						baked_model = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation("minecraft:block/cube_all")).uvlock(true).retexture(ImmutableMap.of("all", String.format("%s:ores/%s", name.getResourceDomain(), name.getResourcePath()))).bake(this.modelState, this.vertexFormat, this.textureGetter);
						BakedModelOre.models.put(state, baked_model);
						return baked_model.getQuads(state, side, 0L);
					}
				}
			}
			return Lists.newArrayList();
		}

		@Override
		public boolean isAmbientOcclusion()
		{
			return true;
		}

		@Override
		public boolean isGui3d()
		{
			return true;
		}

		@Override
		public boolean isBuiltInRenderer()
		{
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture()
		{
			return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms()
		{
			return ItemCameraTransforms.DEFAULT;
		}

		@Override
		public ItemOverrideList getOverrides()
		{
			return ItemOverridesOre.I;
		}

		private static IBakedModel joinModels(IBlockState state, long seed, IBakedModel model, Iterable<IBakedModel> models)
		{
			List<BakedQuad> generalQuads = Lists.newArrayList();
			Map<EnumFacing, List<BakedQuad>> faceQuads = Maps.newHashMap();
			for(EnumFacing facing : EnumFacing.values())
				faceQuads.put(facing, Lists.newArrayList());
			for(IBakedModel model1 : models)
			{
				generalQuads.addAll(model1.getQuads(state, null, seed));
				for(EnumFacing facing : EnumFacing.values())
					faceQuads.get(facing).addAll(model1.getQuads(state, facing, seed));
			}
			return new SimpleBakedModel(generalQuads, faceQuads, model.isAmbientOcclusion(), model.isGui3d(), model.getParticleTexture(), model.getItemCameraTransforms(), model.getOverrides());
		}
	}

	private static class ItemOverridesOre extends ItemOverrideList
	{
		private static final ItemOverrideList I = new ItemOverridesOre();
		private final Map<Item, IntHashMap<IBakedModel>> models = Maps.newHashMap();

		public ItemOverridesOre()
		{
			super(Lists.newArrayList());
			Optional.of(Minecraft.getMinecraft().getResourceManager()).filter((manager) -> manager instanceof IReloadableResourceManager).ifPresent((manager) -> ((IReloadableResourceManager)manager).registerReloadListener((manager1) -> this.models.clear()));
		}

		@Override
		public IBakedModel handleItemState(IBakedModel original, ItemStack stack, World world, EntityLivingBase entity)
		{
			{
				IBakedModel model = this.models.getOrDefault(stack.getItem(), new IntHashMap<IBakedModel>()).lookup(stack.getMetadata());
				if(model != null)
					return model;
			}
			IBakedModel missing;
			{
			}
			Block block = GameData.getBlockItemMap().inverse().get(stack.getItem());
			if(block instanceof BlockOre)
			{
				if(!this.models.containsKey(stack.getItem()))
					this.models.put(stack.getItem(), new IntHashMap<IBakedModel>());
				IBlockState state = block.getStateFromMeta(stack.getMetadata());
				List<BakedQuad> generalQuads = original.getQuads(state, null, 0L);
				Map<EnumFacing, List<BakedQuad>> faceQuads = Maps.newHashMap();
				for(EnumFacing facing : EnumFacing.values())
					faceQuads.put(facing, original.getQuads(state, facing, 0L));
				IModel typeModel = ((BlockOre)block).getOreType(state).getModel(((BlockOre)block).getOreType().getChildModule(TypeModelModule.class).getModelType());
				IBakedModel model = new SimpleBakedModel(generalQuads, faceQuads, original.isAmbientOcclusion(), original.isGui3d(), original.getParticleTexture(), typeModel.bake(typeModel.getDefaultState(), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter()).getItemCameraTransforms(), ItemOverrideList.NONE);
				this.models.get(stack.getItem()).addKey(stack.getMetadata(), model);
				return model;
			}
			IModel model = ModelLoaderRegistry.getMissingModel();
			return model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
		}
	}
}
