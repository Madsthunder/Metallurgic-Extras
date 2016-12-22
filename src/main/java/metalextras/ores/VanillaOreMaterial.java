package metalextras.ores;

import java.util.Collection;
import java.util.Random;

import com.google.common.collect.Sets;

import api.metalextras.ModelType;
import api.metalextras.OreMaterial;
import api.metalextras.OreProperties;
import api.metalextras.OreType;
import api.metalextras.OreUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;

public abstract class VanillaOreMaterial extends OreMaterial
{
	private final IBlockState state;
	private final Properties properties = new Properties();
	private final int xpMin;
	private final int xpMax;
	private final GenerateMinable.EventType type;
	private final ModelType modelType;
	
	public VanillaOreMaterial(IBlockState state, int xpMin, int xpMax, ModelType modelType, GenerateMinable.EventType type)
	{
		this.state = state;
		this.xpMin = xpMin;
		this.xpMax = xpMax;
		this.modelType = modelType;
		this.type = type;
	}
	
	@Override
	public int getHarvestLevel()
	{
		return this.state.getBlock().getHarvestLevel(this.state);
	}
	
	@Override
	public Item getDrop()
	{
		Item drop = this.state.getBlock().getItemDropped(this.state, new Random(), 0);
		return Block.getBlockFromItem(drop) == this.state.getBlock() ? OreMaterial.ORE : drop;
	}
	
	@Override
	public int getDropCount(Random random)
	{
		return this.state.getBlock().quantityDropped(random);
	}
	
	@Override
	public int getDropCountWithFortune(int fortune, Random random)
	{
		return this.state.getBlock().quantityDropped(this.state, fortune, random);
	}
	
	@Override
	public int getDropMeta()
	{
		Item drop = this.state.getBlock().getItemDropped(this.state, new Random(), 0);
		return Block.getBlockFromItem(drop) == this.state.getBlock() ? -1 : this.state.getBlock().damageDropped(this.state);
	}
	
	@Override
	public int getDropXP(Random random)
	{
		return MathHelper.getInt(random, this.xpMin, this.xpMax);
	}
	
	/**
	 * @Override public ItemStack getIngot() { return FurnaceRecipes.instance().getSmeltingResult(new ItemStack(this.state.getBlock())); }
	 */
	
	@Override
	public OreProperties getOreProperties()
	{
		return this.properties;
	}
	
	@Override
	public String getLanguageKey()
	{
		return this.state.getBlock().getUnlocalizedName();
	}
	
	public abstract int getSpawnTries(World world, Random random);
	
	public abstract int[] getSpawnParams(World world);
	
	public abstract int getVeinSize(BiomeDecorator decorator);
	
	public GenerateType getGenerateType()
	{
		return GenerateType.ORE1;
	}
	
	@Override
	public Collection<GenerateMinable.EventType> getOverrides()
	{
		return Sets.newHashSet(this.type);
	}
	
	@Override
	public IModel getModel(OreType type)
	{
		return ModelLoaderRegistry.getModelOrMissing(new ResourceLocation("metalextras", "block/" + this.getRegistryName().getResourcePath()));
	}
	
	@Override
	public ModelType getModelType()
	{
		return this.modelType;
	}
	
	private class Properties extends OreProperties
	{
		private final WorldGenerator generator;
		
		private Properties()
		{
			this.generator = new WorldGenerator()
			{
				@Override
				public boolean generate(World world, Random random, BlockPos pos)
				{
					return OreUtils.generateOres(world, pos, random, VanillaOreMaterial.this.getVeinSize(world.getBiome(pos).theBiomeDecorator), Properties.this);
				}
			};
		}
		
		@Override
		public OreMaterial getOreMaterial()
		{
			return VanillaOreMaterial.this;
		}
		
		@Override
		public boolean getSpawnEnabled()
		{
			return true;
		}
		
		@Override
		public int getSpawnTriesPerChunk(World world, Random random)
		{
			return VanillaOreMaterial.this.getSpawnTries(world, random);
		}
		
		@Override
		public BlockPos getRandomSpawnPos(World world, Random random)
		{
			return new BlockPos(random.nextInt(16), VanillaOreMaterial.this.getGenerateType().getHeight(VanillaOreMaterial.this, world, random), random.nextInt(16));
		}
		
		@Override
		public WorldGenerator getWorldGenerator(World world, BlockPos pos)
		{
			return this.generator;
		}
		
		@Override
		public boolean isValid(OreType type)
		{
			return true;
		}
	}
	
	public static enum GenerateType
	{
		ORE1,
		ORE2,
		ORE3;
		
		public int getHeight(VanillaOreMaterial material, World world, Random random)
		{
			int[] spawnParams = material.getSpawnParams(world);
			if(this == ORE2)
				return random.nextInt(spawnParams[1]) + random.nextInt(spawnParams[1]) + spawnParams[0] - spawnParams[1];
			else if(this == ORE3)
				return random.nextInt(spawnParams[0]) + spawnParams[1];
			else
				return random.nextInt(spawnParams[1] - spawnParams[0]) + spawnParams[0];
		}
	}
	
	public static enum OreModelType
	{
		IRON,
		LAPIS,
		EMERALD;
		// TODO Add some model methods
	}
}
