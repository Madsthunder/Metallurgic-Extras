package metalextras.world.gen;

import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;

import api.metalextras.OreMaterial;
import api.metalextras.OreProperties;
import api.metalextras.OreUtils;
import metalextras.MetalExtras;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderSettings;
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
	public static final ChunkProviderSettings defaultSettings = new ChunkProviderSettings.Factory().build();
	private static final Map<World, ChunkProviderSettings> settingsMap = Maps.newHashMap();
	
	public static void spawnOresInChunk(Chunk chunk, Random random, OreMaterial material)
	{
		OreProperties properties = material.getOreProperties();
		for(int i = 0; i < properties.getSpawnTriesPerChunk(chunk.getWorld(), random); i++)
		{
			BlockPos pos = properties.getRandomSpawnPos(chunk.getWorld(), random).add(chunk.xPosition * 16, 0, chunk.zPosition * 16);
			properties.getWorldGenerator(chunk.getWorld(), pos).generate(chunk.getWorld(), random, pos);
		}
	}
	
	@Override
	public void generate(Random random, int x, int z, World world, IChunkGenerator generator, IChunkProvider provider)
	{
		DimensionType type = world.provider.getDimensionType();
		for(OreMaterial material : OreUtils.getMaterialsRegistry())
			if(material.getOverrides().isEmpty())
				spawnOresInChunk(world.getChunkFromChunkCoords(x, z), random, material);
	}
	
	@SubscribeEvent
	public static void onOreGenPre(OreGenEvent.Pre event)
	{
		postGenerateMinableEvent(event, MetalExtras.COPPER_ORE, MetalExtras.COPPER_EVT);
		postGenerateMinableEvent(event, MetalExtras.TIN_ORE, MetalExtras.TIN_EVT);
		postGenerateMinableEvent(event, MetalExtras.ALUMINUM_ORE, MetalExtras.ALUMINUM_EVT);
		postGenerateMinableEvent(event, MetalExtras.LEAD_ORE, MetalExtras.LEAD_EVT);
		postGenerateMinableEvent(event, MetalExtras.SILVER_ORE, MetalExtras.SILVER_EVT);
		postGenerateMinableEvent(event, MetalExtras.ENDER_ORE, MetalExtras.ENDER_EVT);
		postGenerateMinableEvent(event, MetalExtras.SAPPHIRE_ORE, MetalExtras.SAPPHIRE_EVT);
		postGenerateMinableEvent(event, MetalExtras.RUBY_ORE, MetalExtras.RUBY_EVT);
	}
	
	private static void postGenerateMinableEvent(OreGenEvent event, OreMaterial material, GenerateMinable.EventType type)
	{
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		if(MinecraftForge.ORE_GEN_BUS.post(new GenerateMinable(world, event.getRand(), material.getOreProperties().getWorldGenerator(world, pos), pos, type)))
			OreGeneration.spawnOresInChunk(world.getChunkFromBlockCoords(pos), event.getRand(), material);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onOreGen(GenerateMinable event)
	{
		boolean deny = false;
		for(OreMaterial material : OreUtils.getMaterialsRegistry())
			if(material.getOreProperties().getSpawnEnabled() && material.getOverrides().contains(event.getType()))
				OreGeneration.spawnOresInChunk(event.getWorld().getChunkFromBlockCoords(event.getPos()), event.getRand(), material);
		deny = true;
		if(deny)
			event.setResult(Result.DENY);
	}
	
	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event)
	{
		settingsMap.put(event.getWorld(), ChunkProviderSettings.Factory.jsonToFactory(event.getWorld().getWorldInfo().getGeneratorOptions()).build());
	}
	
	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload event)
	{
		settingsMap.remove(event.getWorld());
	}
	
	public static ChunkProviderSettings getChunkProviderSettings(World world)
	{
		ChunkProviderSettings settings = settingsMap.get(world);
		return settings == null ? defaultSettings : settings;
	}
	
	public static ChunkProviderSettings getChunkProviderSettings(BiomeDecorator decorator)
	{
		return decorator.chunkProviderSettings == null ? defaultSettings : decorator.chunkProviderSettings;
	}
}