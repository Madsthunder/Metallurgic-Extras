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
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry.AddCallback;
import net.minecraftforge.registries.IForgeRegistry.CreateCallback;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.RegistryManager;

public class MetalExtras_Callbacks
{
	public static final Object ORE_MATERIALS = new OreMaterials();
	public static final Object ORE_TYPES = new OreTypeCollections();
	
	private static class OreMaterials implements AddCallback<OreMaterial>
	{
		@Override
		public void onAdd(IForgeRegistryInternal<OreMaterial> registry, RegistryManager manager, int id, OreMaterial material, OreMaterial old)
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
		public void onAdd(IForgeRegistryInternal<OreTypes> registry, RegistryManager manager, int id, OreTypes types, OreTypes old)
		{
			types.lock();
			BiMap<OreType, IBlockState> typeToState = registry.getSlaveMap(OreUtils.ORETYPE_TO_IBLOCKSTATE, BiMap.class);
            for(OreType type : types)
                typeToState.put(type, type.getState());
            ObjectIntIdentityMap<OreType> typeToId = registry.getSlaveMap(OreUtils.ORETYPE_TO_ID, ObjectIntIdentityMap.class);
				for(int i = 0; i < types.getOreTypes().size(); i++)
				    typeToId.put(types.getOreTypes().get(i), (id * 16) + i);
			IForgeRegistry<Block> blocks = GameRegistry.findRegistry(Block.class);
			IForgeRegistry<Item> items = GameRegistry.findRegistry(Item.class);
			IForgeRegistry<OreMaterial> materials = GameRegistry.findRegistry(OreMaterial.class);
			if(blocks != null && items != null && materials != null)
				for(OreMaterial material : materials)
					tryRegister(material, types, blocks, items);
			types.update();
		}
		
		@Override
		public void onCreate(IForgeRegistryInternal<OreTypes> registry, RegistryManager manager)
		{
		    registry.setSlaveMap(OreUtils.ORETYPE_TO_IBLOCKSTATE, HashBiMap.<OreType, IBlockState> create());
            registry.setSlaveMap(OreUtils.ORETYPE_TO_ID, new ObjectIntIdentityMap<OreType>());
		}
	}
	
	private static void tryRegister(OreMaterial material, OreTypes types, @Nonnull IForgeRegistry<Block> blocks, @Nonnull IForgeRegistry<Item> items)
	{
		for(BlockOre block : material.getBlocksToRegister(types))
		{
			if(blocks.containsKey(block.getRegistryName()))
			{
				Block block1 = blocks.getValue(block.getRegistryName());
				if(block1 instanceof BlockOre)
					block = (BlockOre)block1;
				else
					throw new IllegalStateException("There Is Already A Block \"" + block.getRegistryName() + "\" Registered That Doesn't Extend BlockOre.");
			}
			else
				blocks.register(block);
			Item item = new ItemBlockOre(block, block.getOreTypeProperty()).setRegistryName(block.getRegistryName()).setUnlocalizedName(block.getRegistryName().toString());
			if(item != null && !items.containsKey(item.getRegistryName()))
				items.register(item);
		}
	}
}
