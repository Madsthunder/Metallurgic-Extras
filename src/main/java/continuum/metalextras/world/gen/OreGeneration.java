package continuum.metalextras.world.gen;

import java.util.HashMap;
import java.util.Random;

import com.google.common.collect.Maps;

import continuum.api.metalextras.IOreData;
import continuum.api.metalextras.OreProperties;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

public class OreGeneration implements IWorldGenerator
{
	public final MetalExtras_OH objectHolder;
	private static final HashMap<IOreData, WorldGenMinableAdvanced> worldGen = Maps.newHashMap();
	
	public OreGeneration(MetalExtras_OH objectHolder)
	{
		this.objectHolder = objectHolder;
		for(IOreData data : objectHolder.ores)
			worldGen.put(data, new WorldGenMinableAdvanced(data));
	}
	
	public static void spawnOresInChunk(World world, DimensionType dimension, Integer xPos, Integer zPos, Random random, IOreData data)
	{
		OreProperties properties = data.getOre().getOreProperties();
		for(Integer i = 0; i < properties.getSpawnTriesPerChunk(); i++)
		{
			Integer y = random.nextInt(properties.getMaxGenHeight() - properties.getMinGenHeight()) + properties.getMinGenHeight();
			Integer x = random.nextInt(16) + (xPos * 16);
			Integer z = random.nextInt(16) + (zPos * 16);
			worldGen.get(data).generate(world, dimension, x, y, z, random);
		}
	}
	
	@Override
	public void generate(Random random, int x, int z, World world, IChunkGenerator generator, IChunkProvider provider)
	{
		DimensionType type = world.provider.getDimensionType();
		for(IOreData data : this.objectHolder.ores)
			if(type == DimensionType.NETHER || this.objectHolder.oresToReplace.contains(data.getOreName()))
				MetalExtras_EH.scheduleOreForGeneration(world, x, z, data);
	}
}
