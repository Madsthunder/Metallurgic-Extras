package continuum.metalextras.mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import continuum.core.mod.Core_OH;
import continuum.essentials.mod.CTMod;
import continuum.metalextras.api.IOre;
import continuum.metalextras.blocks.BlockOreGround;
import continuum.metalextras.blocks.BlockOreGround.EnumGroundType;
import continuum.metalextras.blocks.BlockOreRock;
import continuum.metalextras.blocks.BlockOreRock.EnumRockType;
import continuum.metalextras.world.gen.OreGeneration;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
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
	public static final HashMap<String, ArrayList<Chunk>> chunkGenerated = new HashMap<String, ArrayList<Chunk>>();
	private static final ArrayList<Pair<Chunk, String>> chunks = new ArrayList<Pair<Chunk, String>>();
	public static Random random = new Random();
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onItemTooltip(ItemTooltipEvent event)
	{
		ItemStack stack = event.getItemStack();
		Block block = Block.getBlockFromItem(stack.getItem());
		if(block instanceof BlockOreRock)
			event.getToolTip().add(I18n.translateToLocal("stone." + EnumRockType.values()[stack.getMetadata()].getName() + ".name"));
		else if(block instanceof BlockOreGround)
			event.getToolTip().add(I18n.translateToLocal("ground." + EnumGroundType.values()[stack.getMetadata()].getName() + ".name"));
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onChunkDataLoad(ChunkDataEvent.Load event)
	{
		Chunk chunk = event.getChunk();
		if(chunk.isTerrainPopulated())
		{
			loadOreGenData(chunk, event.getData());
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onChunkDataSave(ChunkDataEvent.Save event)
	{
		Chunk chunk = event.getChunk();
		event.getData().setTag("oreGenData", getOreGenData(chunk));
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onChunkLoad(ChunkEvent.Load event)
	{
		Chunk chunk = event.getChunk();
		World world = chunk.getWorld();
		if(!(world.isRemote))
			for(String name : objectHolder.oresList.keySet())
				if(getChunkNeedsGenerating(name, chunk))
					scheduleOreForGeneration(chunk, name);
	}
	
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event)
	{
		Integer min = Math.min(4, chunks.size());
		for(Integer i = 0;i < min;i++)
		{
			Pair<Chunk, String> pair = chunks.remove(0);
			Chunk chunk = pair.getLeft();
			String name = pair.getRight();
			ArrayList<Chunk> list = chunkGenerated.get(name);
			for(IOre ore : objectHolder.oresList.get(name))
			{
				OreGeneration.spawnOresInChunk(chunk.getWorld(), chunk.getWorld().provider.getDimensionType(), chunk.xPosition, chunk.zPosition, random, ore);
				if(list != null)
					list.remove(chunk);
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onChunkUnload(ChunkEvent.Unload event)
	{
		for(String name : chunkGenerated.keySet())
			chunkGenerated.get(name).remove(event.getChunk());
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onOreGen(GenerateMinable event)
	{
		Chunk chunk = event.getWorld().getChunkFromBlockCoords(event.getPos());
		String name = "";
		EventType type = event.getType();
		if(type == EventType.COAL)
			name = "coal_ore";
		if(type == EventType.IRON)
			name = "iron_ore";
		if(type == EventType.LAPIS)
			name = "lapis_ore";
		if(type == EventType.GOLD)
			name = "gold_ore";
		if(type == EventType.REDSTONE)
			name = "redstone_ore";
		if(type == EventType.EMERALD)
			name = "emerald_ore";
		if(type == EventType.DIAMOND)
			name = "diamond_ore";
		if(objectHolder.oresList.containsKey(name))
			scheduleOreForGeneration(chunk, name);
		event.setResult(name == "" ? event.getResult() :  Result.DENY);
	}
	
	public static void loadOreGenData(Chunk chunk, NBTTagCompound compound)
	{
		if(!compound.hasKey("oreGenData"))
			for(String name : objectHolder.oresList.keySet())
			{
				if(objectHolder.oresToRegen.contains(name))
				{
					Boolean generated = compound.getCompoundTag("oreGenData").getBoolean(name);
					if(!generated)
						chunkGenerated.get(name).add(chunk);
				}
			}
	}
	
	public static NBTTagCompound getOreGenData(Chunk chunk)
	{
		NBTTagCompound oreGenData = new NBTTagCompound();
		for(String name : objectHolder.oresList.keySet())
			if(objectHolder.oresToRegen.contains(name))
				oreGenData.setBoolean(name, !getChunkNeedsGenerating(name, chunk));
		return oreGenData;
	}
	
	/**@SubscribeEvent
	public void onSoundPlay(PlaySoundEvent event)
	{
		ISound sound = event.getResultSound();
		if(sound instanceof PositionedSoundRecord)
		{
			PositionedSoundRecord record = (PositionedSoundRecord)sound;
			if(SoundEvent.soundEventRegistry.getObject(record.getSoundLocation()) instanceof IAdaptableSoundEvent)
			{
				event.setResultSound(new PositionedSoundRecord());
			}
		}
	}*/
	
	public static Boolean getChunkNeedsGenerating(String oreID, Chunk chunk)
	{
		return chunkGenerated.get(oreID).contains(chunk);
	}
	
	public static void scheduleOreForGeneration(Chunk chunk, String ore)
	{
		chunks.add(Pair.of(chunk, ore));
	}
	
	public static void scheduleOreForGeneration(World world, Integer chunkX, Integer chunkZ, String ore)
	{
		chunks.add(Pair.of(world.getChunkFromChunkCoords(chunkX, chunkZ), ore));
	}
}
