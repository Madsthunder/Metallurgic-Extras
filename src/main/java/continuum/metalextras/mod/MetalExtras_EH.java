package continuum.metalextras.mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import continuum.api.metalextras.BlockOre;
import continuum.api.metalextras.OreMaterial;
import continuum.api.metalextras.OreType;
import continuum.metalextras.world.gen.OreGeneration;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MetalExtras_EH
{
	static MetalExtras_OH objectHolder;
	/**Used to check if a chunk needs generating, will return true if the chunk is found*/
	public static final HashMap<OreMaterial, HashSet<Chunk>> chunkGenerated = Maps.newHashMap();
	private static final ArrayList<Pair<Chunk, OreMaterial>> chunks = Lists.newArrayList();
	public static Random random = new Random();
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onItemTooltip(ItemTooltipEvent event)
	{
		ItemStack stack = event.getItemStack();
		Block block = Block.getBlockFromItem(stack.getItem());
		if(block instanceof BlockOre)
		{
			OreType ore = ((BlockOre)block).getOreType(block.getStateFromMeta(stack.getMetadata()));
			event.getToolTip().add(I18n.translateToLocal(ore.getCategory().getName().getResourcePath() + "." + ore.getName() + ".name"));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onChunkDataLoad(ChunkDataEvent.Load event)
	{
		Chunk chunk = event.getChunk();
		if(chunk.isTerrainPopulated())
			loadOreGenData(chunk, event.getData());
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onChunkDataSave(ChunkDataEvent.Save event)
	{
		event.getData().setTag("oreGenData", getOreGenData(event.getChunk()));
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onChunkLoad(ChunkEvent.Load event)
	{
		Chunk chunk = event.getChunk();
		World world = chunk.getWorld();
		if(!(world.isRemote))
			for(OreMaterial data : objectHolder.ores)
				if(getChunkNeedsGenerating(data, chunk))
					scheduleOreForGeneration(chunk, data);
	}
	
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event)
	{
		Integer min = Math.min(4, chunks.size());
		for(Integer i = 0;i < min;i++)
		{
			Pair<Chunk, OreMaterial> pair = chunks.remove(0);
			Chunk chunk = pair.getLeft();
			OreMaterial data = pair.getRight();
			HashSet<Chunk> set = chunkGenerated.get(data);
			OreGeneration.spawnOresInChunk(chunk, random, data);
			if(set != null)
				set.remove(chunk);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onChunkUnload(ChunkEvent.Unload event)
	{
		for(OreMaterial data : chunkGenerated.keySet())
			chunkGenerated.get(data).remove(event.getChunk());
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onOreGen(GenerateMinable event)
	{
		EventType type = event.getType();
		String name;
		switch(type)
		{
			case COAL : name = "coal_ore";
			break;
			case IRON : name = "iron_ore";
			break;
			case LAPIS : name = "lapis_ore";
			break;
			case GOLD : name = "gold_ore";
			break;
			case REDSTONE : name = "redstone_ore";
			break;
			case EMERALD : name = "emerald_ore";
			break;
			case DIAMOND : name = "diamond_ore";
			default : name = null;
		}
		OreMaterial data = null;
		if(name != null)
		{
			ResourceLocation location = new ResourceLocation(name);
			for(OreMaterial data1 : MetalExtras_OH.ores)
				if(data1.getName().equals(location))
					data = data1;
		}
		if(data != null)
			scheduleOreForGeneration(event.getWorld().getChunkFromBlockCoords(event.getPos()), data);
		event.setResult(data == null ? event.getResult() : Result.DENY);
	}
	
	public static void loadOreGenData(Chunk chunk, NBTTagCompound compound)
	{
		if(!compound.hasKey("oreGenData"))
			for(OreMaterial data : objectHolder.ores)
				if(objectHolder.oresToReplace.contains(data.getName()))
					if(!compound.getCompoundTag("oreGenData").getBoolean(data.getName().toString()))
						chunkGenerated.get(data).add(chunk);
	}
	
	public static NBTTagCompound getOreGenData(Chunk chunk)
	{
		NBTTagCompound oreGenData = new NBTTagCompound();
		for(OreMaterial data : objectHolder.ores)
			if(objectHolder.oresToReplace.contains(data.getName()))
				oreGenData.setBoolean(data.getName().toString(), !getChunkNeedsGenerating(data, chunk));
		return oreGenData;
	}
	
	public static boolean getChunkNeedsGenerating(OreMaterial data, Chunk chunk)
	{
		return chunkGenerated.get(data).contains(chunk);
	}
	
	public static void scheduleOreForGeneration(Chunk chunk, OreMaterial data)
	{
		chunks.add(Pair.of(chunk, data));
	}
	
	public static void scheduleOreForGeneration(World world, Integer chunkX, Integer chunkZ, OreMaterial data)
	{
		chunks.add(Pair.of(world.getChunkFromChunkCoords(chunkX, chunkZ), data));
	}
}
