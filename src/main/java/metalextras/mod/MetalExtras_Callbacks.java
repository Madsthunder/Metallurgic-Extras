package metalextras.mod;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import api.metalextras.BlockOre;
import api.metalextras.OreMaterial;
import api.metalextras.OreType;
import api.metalextras.OreTypes;
import api.metalextras.OreUtils;
import metalextras.items.ItemBlockOre;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry.AddCallback;
import net.minecraftforge.fml.common.registry.IForgeRegistry.CreateCallback;

public class MetalExtras_Callbacks
{
	public static final Object ORE_MATERIALS = new OreMaterials();
	public static final Object ORE_TYPES = new OreTypeCollections();
	
	private static class OreMaterials implements AddCallback<OreMaterial>
	{
		@Override
		public void onAdd(OreMaterial material, int id, Map<ResourceLocation, ?> slaveset)
		{
			IForgeRegistry<Block> blocks = GameRegistry.findRegistry(Block.class);
			IForgeRegistry<Item> items = GameRegistry.findRegistry(Item.class);
			IForgeRegistry<OreTypes> typeCollections = GameRegistry.findRegistry(OreTypes.class);
			if(blocks != null && items != null && typeCollections != null)
				for(OreTypes types : typeCollections)
					tryRegister(material, types, blocks, items);
		}
	}
	
	private static class OreTypeCollections implements CreateCallback<OreTypes>, AddCallback<OreTypes>
	{
		@Override
		public void onAdd(OreTypes types, int id, Map<ResourceLocation, ?> slaveset)
		{
			types.lock();
			if(slaveset.containsKey(OreUtils.ORETYPE_TO_IBLOCKSTATE))
				for(OreType type : types)
					((BiMap<OreType, IBlockState>)slaveset.get(OreUtils.ORETYPE_TO_IBLOCKSTATE)).put(type, type.getState());
			IForgeRegistry<Block> blocks = GameRegistry.findRegistry(Block.class);
			IForgeRegistry<Item> items = GameRegistry.findRegistry(Item.class);
			IForgeRegistry<OreMaterial> materials = GameRegistry.findRegistry(OreMaterial.class);
			if(blocks != null && items != null && materials != null)
				for(OreMaterial material : materials)
					tryRegister(material, types, blocks, items);
		}
		
		@Override
		public void onCreate(Map<ResourceLocation, ?> slaveset, BiMap<ResourceLocation, ? extends IForgeRegistry<?>> registries)
		{
			Map<ResourceLocation, Object> slaves = (Map<ResourceLocation, Object>)slaveset;
			slaves.put(OreUtils.ORETYPE_TO_IBLOCKSTATE, HashBiMap.<OreType, IBlockState>create());
		}
	}
	
	private static void tryRegister(OreMaterial material, OreTypes types, @Nonnull IForgeRegistry<Block> blocks, @Nonnull IForgeRegistry<Item> items)
	{
		Block block = material.generateBlock(types);
		if(blocks.containsKey(block.getRegistryName()))
			block = blocks.getValue(block.getRegistryName());
		else
			blocks.register(block);
		Item item = null;
		if(block instanceof BlockOre)
			item = new ItemBlockOre(block, ((BlockOre)block).getOreTypeProperty()).setRegistryName(block.getRegistryName()).setUnlocalizedName(block.getRegistryName().toString());
		if(item != null && !items.containsKey(item.getRegistryName()))
			items.register(item);
	}
}
