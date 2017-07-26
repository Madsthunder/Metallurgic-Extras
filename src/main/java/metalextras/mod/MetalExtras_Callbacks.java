package metalextras.mod;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import api.metalextras.OreType;
import api.metalextras.OreTypes;
import api.metalextras.OreUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraftforge.registries.IForgeRegistry.AddCallback;
import net.minecraftforge.registries.IForgeRegistry.ClearCallback;
import net.minecraftforge.registries.IForgeRegistry.CreateCallback;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.RegistryManager;

public class MetalExtras_Callbacks
{
	public static final Object ORE_TYPES = new OreTypeCollections();
	
	private static class OreTypeCollections implements ClearCallback<OreTypes>, CreateCallback<OreTypes>, AddCallback<OreTypes>
	{
		@Override
		public void onAdd(IForgeRegistryInternal<OreTypes> registry, RegistryManager manager, int id, OreTypes types, OreTypes old)
		{
			types.lock();
			BiMap<OreType, IBlockState> typeToState = registry.getSlaveMap(OreUtils.ORETYPE_TO_IBLOCKSTATE, BiMap.class);
            for(OreType type : types)
                typeToState.put(type, type.getState());
		}
		
		@Override
		public void onCreate(IForgeRegistryInternal<OreTypes> registry, RegistryManager manager)
		{
		    registry.setSlaveMap(OreUtils.ORETYPE_TO_IBLOCKSTATE, HashBiMap.<OreType, IBlockState> create());
            registry.setSlaveMap(OreUtils.ORETYPE_TO_ID, new ObjectIntIdentityMap<OreType>());
		}

        @Override
        public void onClear(IForgeRegistryInternal<OreTypes> registry, RegistryManager manager)
        {
            registry.getSlaveMap(OreUtils.ORETYPE_TO_IBLOCKSTATE, BiMap.class).clear();
        }
	}
}
