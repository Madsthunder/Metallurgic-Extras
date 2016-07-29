package continuum.metalextras.world.gen;

import java.util.HashMap;
import java.util.Random;

import continuum.api.metalextras.IOre;
import continuum.essentials.mod.CTMod;
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
	private static final HashMap<IOre, WorldGenMinableAdvanced> worldGen = new HashMap<IOre, WorldGenMinableAdvanced>();
	
	public OreGeneration(MetalExtras_OH objectHolder)
	{
		this.objectHolder = objectHolder;
		for(IOre[] ores : objectHolder.oresList.values())
			for(IOre ore : ores)
				worldGen.put(ore, new WorldGenMinableAdvanced(ore));
	}
	
	public static void spawnOresInChunk(World world, DimensionType dimension, Integer xPos, Integer zPos, Random random, IOre ore)
	{
		for(Integer i = 0; i < ore.getSpawnTriesPerChunk(world); i++)
		{
			Integer y = random.nextInt(ore.getMaxGenHeight(world) - ore.getMinGenHeight(world)) + ore.getMinGenHeight(world);
			Integer x = random.nextInt(16) + (xPos * 16);
			Integer z = random.nextInt(16) + (zPos * 16);
			worldGen.get(ore).generate(world, dimension, x, y, z, random);
		}
	}
	
	@Override
	public void generate(Random random, int x, int z, World world, IChunkGenerator generator, IChunkProvider provider)
	{
		DimensionType type = world.provider.getDimensionType();
		for(String name : this.objectHolder.oresList.keySet())
			if(type == DimensionType.NETHER || this.objectHolder.oresToRegen.contains(name))
				MetalExtras_EH.scheduleOreForGeneration(world, x, z, name);
	}
}
