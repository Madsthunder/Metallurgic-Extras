package metalextras.world.gen;

import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;

import api.metalextras.OreUtils;
import metalextras.newores.NewOreType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
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
	
	public static void spawnOresInChunk(Chunk chunk, Random random, NewOreType material)
	{
	    NewOreType.Generation generation = material.generation;
	    if(generation.canGenerate())
	    {
	        World world = chunk.getWorld();
	        WorldGenerator generator = generation.getGenerator();
	        for(int i = 0; i < generation.getSpawnTries(); i++)
	            generator.generate(world, random, new BlockPos(random.nextInt(16) + (chunk.x * 16), random.nextInt(generation.getMaxHeight() - generation.getMinHeight()) + generation.getMinHeight(), random.nextInt(16) + (chunk.z * 16)));
	    }
	}
	
	@Override
	public void generate(Random random, int x, int z, World world, IChunkGenerator generator, IChunkProvider provider)
	{
		for(NewOreType material : OreUtils.getTypesRegistry())
			if(material.generation.event == null)
				spawnOresInChunk(world.getChunkFromChunkCoords(x, z), random, material);
	}
	
	@SubscribeEvent
	public static void onOreGenPre(OreGenEvent.Pre event)
	{
	    for(NewOreType type : OreUtils.getTypesRegistry())
	        if(type.generation.post_event)
	            postGenerateMinableEvent(event, type, type.generation.event);
		/**postGenerateMinableEvent(event, MetalExtras_Objects.COPPER_ORE, MetalExtras.COPPER_EVT);
		postGenerateMinableEvent(event, MetalExtras_Objects.TIN_ORE, MetalExtras.TIN_EVT);
		postGenerateMinableEvent(event, MetalExtras_Objects.ALUMINUM_ORE, MetalExtras.ALUMINUM_EVT);
		postGenerateMinableEvent(event, MetalExtras_Objects.LEAD_ORE, MetalExtras.LEAD_EVT);
		postGenerateMinableEvent(event, MetalExtras_Objects.SILVER_ORE, MetalExtras.SILVER_EVT);
		postGenerateMinableEvent(event, MetalExtras_Objects.ENDER_ORE, MetalExtras.ENDER_EVT);
		postGenerateMinableEvent(event, MetalExtras_Objects.SAPPHIRE_ORE, MetalExtras.SAPPHIRE_EVT);
		postGenerateMinableEvent(event, MetalExtras_Objects.RUBY_ORE, MetalExtras.RUBY_EVT);*/
	}
	
	private static void postGenerateMinableEvent(OreGenEvent event, NewOreType material, GenerateMinable.EventType type)
	{
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		WorldGenerator generator = material.generation.getGenerator();
		if(MinecraftForge.ORE_GEN_BUS.post(new GenerateMinable(world, event.getRand(), generator, pos, type)))
			OreGeneration.spawnOresInChunk(world.getChunkFromBlockCoords(pos), event.getRand(), material);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onOreGen(GenerateMinable event)
	{
		boolean deny = false;
		for(NewOreType material : OreUtils.getTypesRegistry())
		{
		    NewOreType.Generation generation = material.generation;
			if(generation.canGenerate() && generation.event == event.getType())
				OreGeneration.spawnOresInChunk(event.getWorld().getChunkFromBlockCoords(event.getPos()), event.getRand(), material);
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
