package metalextras.world.gen;

import java.util.Map;
import java.util.Random;
import com.google.common.collect.Maps;
import api.metalextras.OreUtils;
import metalextras.newores.NewOreType;
import metalextras.newores.modules.GenerationModule;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class OreGeneration implements IWorldGenerator
{
	public static final ChunkGeneratorSettings defaultSettings = new ChunkGeneratorSettings.Factory().build();
	private static final Map<World, ChunkGeneratorSettings> settingsMap = Maps.newHashMap();

	public static void spawnOresInChunk(Chunk chunk, Random random, GenerationModule.Properties properties)
	{
		if(properties.canGenerate())
		{
			World world = chunk.getWorld();
			WorldGenerator generator = properties.getGenerator();
			int tries = properties.randomSpawnTries(random);
			for(int i = 0; i < tries; i++)
				generator.generate(world, random, new BlockPos(random.nextInt(16) + chunk.x * 16, properties.randomHeight(random), random.nextInt(16) + chunk.z * 16));
		}
	}

	@Override
	public void generate(Random random, int x, int z, World world, IChunkGenerator generator, IChunkProvider provider)
	{
		for(NewOreType material : OreUtils.getTypesRegistry())
			if(!material.getGenerationModule().hasEvent())
				spawnOresInChunk(world.getChunkFromChunkCoords(x, z), random, material.getGenerationModule().getProperties(world, new BlockPos(x * 16, 0, z * 16)));
	}

	@SubscribeEvent
	public static void onOreGenPre(OreGenEvent.Pre event)
	{
		for(NewOreType type : OreUtils.getTypesRegistry())
			if(type.getGenerationModule().shouldPostEvent())
				postGenerateMinableEvent(event, type, type.getGenerationModule().getEvent());
	}

	private static void postGenerateMinableEvent(OreGenEvent event, NewOreType material, GenerateMinable.EventType type)
	{
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		GenerationModule.Properties properties = material.getGenerationModule().getProperties(world, pos);
		WorldGenerator generator = properties.getGenerator();
		if(MinecraftForge.ORE_GEN_BUS.post(new GenerateMinable(world, event.getRand(), generator, pos, type)))
			OreGeneration.spawnOresInChunk(world.getChunkFromBlockCoords(pos), event.getRand(), properties);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onOreGen(GenerateMinable event)
	{
		boolean deny = false;
		for(NewOreType material : OreUtils.getTypesRegistry())
		{
			World world = event.getWorld();
			BlockPos pos = event.getPos();
			GenerationModule.Properties properties = material.getGenerationModule().getProperties(world, pos);
			if(properties.canGenerate() && material.getGenerationModule().getEvent() == event.getType())
				OreGeneration.spawnOresInChunk(world.getChunkFromBlockCoords(pos), event.getRand(), properties);
		}
		deny = true;
		if(deny)
			event.setResult(Result.DENY);
	}

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event)
	{
		settingsMap.put(event.getWorld(), ChunkGeneratorSettings.Factory.jsonToFactory(event.getWorld().getWorldInfo().getGeneratorOptions()).build());
	}

	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload event)
	{
		settingsMap.remove(event.getWorld());
	}

	public static ChunkGeneratorSettings getChunkProviderSettings(World world)
	{
		ChunkGeneratorSettings settings = settingsMap.get(world);
		return settings == null ? defaultSettings : settings;
	}

	public static ChunkGeneratorSettings getChunkProviderSettings(BiomeDecorator decorator)
	{
		return decorator.chunkProviderSettings == null ? defaultSettings : decorator.chunkProviderSettings;
	}
}
