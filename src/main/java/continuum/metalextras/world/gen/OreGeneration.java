package continuum.metalextras.world.gen;

import java.util.HashMap;
import java.util.Random;

import com.google.common.collect.Maps;

import continuum.api.metalextras.OreMaterial;
import continuum.api.metalextras.OreProperties;
import continuum.essentials.hooks.ObjectHooks;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

public class OreGeneration implements IWorldGenerator
{
	public final MetalExtras_OH objectHolder;
	private static final HashMap<OreMaterial, WorldGenOre> worldGen = Maps.newHashMap();
	
	public OreGeneration(MetalExtras_OH objectHolder)
	{
		this.objectHolder = objectHolder;
		for(OreMaterial data : objectHolder.ores)
			worldGen.put(data, new WorldGenOre(data));
	}
	
	public static void spawnOresInChunk(Chunk chunk, Random random, OreMaterial material)
	{
		OreProperties properties = material.getOreProperties();
		for(int i : ObjectHooks.increment(properties.getSpawnTriesPerChunk()))
			worldGen.get(material).generate(chunk.getWorld(), random, new BlockPos(random.nextInt(16) + (chunk.xPosition * 16), random.nextInt(properties.getMaxGenHeight() - properties.getMinGenHeight()) + properties.getMinGenHeight(), random.nextInt(16) + (chunk.zPosition * 16)));
	}
	
	@Override
	public void generate(Random random, int x, int z, World world, IChunkGenerator generator, IChunkProvider provider)
	{
		DimensionType type = world.provider.getDimensionType();
		for(OreMaterial data : this.objectHolder.ores)
			if(type == DimensionType.NETHER || this.objectHolder.oresToReplace.contains(data.getName()))
				MetalExtras_EH.scheduleOreForGeneration(world, x, z, data);
	}
}
