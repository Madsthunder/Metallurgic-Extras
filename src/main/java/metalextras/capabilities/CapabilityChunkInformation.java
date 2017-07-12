package metalextras.capabilities;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import api.metalextras.OreUtils;
import metalextras.MetalExtras;
import metalextras.ores.materials.OreMaterial;
import metalextras.world.gen.OreGeneration;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@EventBusSubscriber(modid = MetalExtras.MODID)
public class CapabilityChunkInformation implements ICapabilityProvider, Iterable<Pair<Chunk, boolean[]>>
{
    public static final ResourceLocation KEY = new ResourceLocation("metalextras:chunk_info");
    @CapabilityInject(CapabilityChunkInformation.class)
    public static Capability<CapabilityChunkInformation> CHUNK_INFORMATION = null;

    private final World world;
    private final Map<Chunk, boolean[]> map = Maps.newHashMap();
    private final Deque<Pair<Chunk, boolean[]>> generation_queue = Queues.newArrayDeque();
    
    public CapabilityChunkInformation(World world)
    {
        this.world = world;
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CHUNK_INFORMATION;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == CHUNK_INFORMATION ? (T)this : null;
    }
    
    @Override
    public Iterator<Pair<Chunk, boolean[]>> iterator()
    {
        return this.generation_queue.iterator();
    }
    
    @SubscribeEvent
    public static void onWorldCapabilitiesGather(AttachCapabilitiesEvent<World> event)
    {
        World world = event.getObject();
        if(world instanceof WorldServer)
            event.addCapability(KEY, new CapabilityChunkInformation(event.getObject()));
    }
    
    @SubscribeEvent
    public static void onChunkDataLoad(ChunkDataEvent.Load event)
    {
        CapabilityChunkInformation chunk_info = event.getWorld().getCapability(CHUNK_INFORMATION, null);
        if(chunk_info != null)
        {
            List<OreMaterial> materials = OreUtils.getMaterialsRegistry().getValues();
            boolean[] array = new boolean[materials.size()];
            Chunk chunk = event.getChunk();
            if(chunk.isTerrainPopulated())
            {
                NBTTagCompound metalextras = event.getData().getCompoundTag("metalextras");
                NBTTagCompound compound = Optional.of(metalextras == null ? null : metalextras.getCompoundTag("ores")).orElseGet(() -> new NBTTagCompound());
                for(int i = 0; i < materials.size(); i++)
                {
                    OreMaterial material = materials.get(i);
                    array[i] = material.isVanillaOre() || compound.getBoolean(material.getRegistryName().toString());
                }
            }
            else
                for(int i = 0; i < materials.size(); i++)
                    array[i] = materials.get(i).getOreProperties().getSpawnEnabled();
            chunk_info.map.put(chunk, array);
        }
    }
    
    @SubscribeEvent
    public static void onChunkDataSave(ChunkDataEvent.Save event)
    {
        CapabilityChunkInformation chunk_info = event.getWorld().getCapability(CHUNK_INFORMATION, null);
        if(chunk_info != null)
        {
            Chunk chunk = event.getChunk();
            boolean[] array = chunk_info.map.get(chunk);
            if(array != null)
            {
                List<OreMaterial> materials = OreUtils.getMaterialsRegistry().getValues();
                NBTTagCompound compound = new NBTTagCompound();
                compound.setInteger("version", 0);
                for(int i = 0; i < array.length; i++)
                    compound.setBoolean(materials.get(i).getRegistryName().toString(), array[i]);
                NBTTagCompound metalextras = Optional.of(event.getData().getCompoundTag("metalextras")).orElseGet(() -> new NBTTagCompound());
                metalextras.setTag("ores", compound);
                event.getData().setTag("metalextras", metalextras);
            }
        }
    }
    
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event)
    {
        CapabilityChunkInformation chunk_info = event.getWorld().getCapability(CHUNK_INFORMATION, null);
        if(chunk_info != null)
        {
            Chunk chunk = event.getChunk();
            boolean[] array = chunk_info.map.get(chunk);
            if(array != null)
                chunk_info.generation_queue.add(Pair.of(chunk, array));
        }
    }
    
    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event)
    {
        CapabilityChunkInformation chunk_info = event.getWorld().getCapability(CHUNK_INFORMATION, null);
        if(chunk_info != null)
        {
            Chunk chunk = event.getChunk();
            chunk_info.map.remove(chunk);
        }
    }
    
    public static final int CHUNKS_PER_TICK = -1;
    
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        IChunkProvider provider = event.world.getChunkProvider();
        CapabilityChunkInformation chunk_info = event.world.getCapability(CHUNK_INFORMATION, null);
        if(chunk_info != null && provider instanceof ChunkProviderServer)
        {
            List<OreMaterial> materials = OreUtils.getMaterialsRegistry().getValues();
            ChunkProviderServer server_provider = (ChunkProviderServer)provider;
            WorldServer world = (WorldServer)event.world;
            long seed = world.getSeed();
            int upto = CHUNKS_PER_TICK < 0 ? chunk_info.generation_queue.size() : CHUNKS_PER_TICK;
            int i = 0;
            while(i < upto)
            {
                i++;
                Entry<Chunk, boolean[]> entry = chunk_info.generation_queue.poll();
                Chunk chunk = entry.getKey();
                for(int x = -1; x <= 1; x++)
                    for(int z = -1; z <= 1; z++)
                        if(!server_provider.chunkExists(chunk.x + x, chunk.z + z))
                            continue;
                Random random = chunk.getRandomWithSeed(seed);
                boolean[] array = entry.getValue();
                for(int j = 0; j < array.length; j++)
                    if(!array[j])
                    {
                        OreGeneration.spawnOresInChunk(chunk, random, materials.get(j));
                        array[j] = true;
                    }
            }
        }
    }
}
