package continuum.api.metalextras;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class OreMaterial
{
	private final ResourceLocation name;
	private final int harvestLevel;
	private final ResourceLocation itemDropped;
	private final int minItemDrops;
	private final int maxItemDrops;
	private final int metaDropped;
	private final int fortuneAdditive;
	private final int minXP;
	private final int maxXP;
	private final ResourceLocation ingot;
	private final int ingotMeta;
	private final boolean replace;
	private final ArrayList<Pair<String, Object>> extras = Lists.newArrayList();
	private final HashSet<BlockOre> blocks = Sets.newHashSet();
	private final OreProperties defaultProperties;
	private OreProperties properties;
	
	public OreMaterial(String ore, int harvestLevel, String ingot, int meta, boolean replace, int spawnTriesPC, int minVeinSize, int maxVeinSize, int minGenHeight, int maxGenHeight, Collection<Pair<String, Object>> extraData)
	{
		this(ore, harvestLevel, "metalextras:ore", 1, 1, -1, 0, 0, 0, ingot, meta, replace, spawnTriesPC, minVeinSize, maxVeinSize, minGenHeight, maxGenHeight, extraData);
	}
	
	public OreMaterial(String ore, int harvestLevel, String item, boolean replace, int spawnTriesPC, int minVeinSize, int maxVeinSize, int minGenHeight, int maxGenHeight, Collection<Pair<String, Object>> extraData)
	{
		this(ore, harvestLevel, item, 0, replace, spawnTriesPC, minVeinSize, maxVeinSize, minGenHeight, maxGenHeight, extraData);
	}
	
	public OreMaterial(String ore, int harvestLevel, String item, String ingot, int meta, boolean replace, int spawnTriesPC, int minVeinSize, int maxVeinSize, int minGenHeight, int maxGenHeight, Collection<Pair<String, Object>> extraData)
	{
		this(ore, harvestLevel, item, 1, 1, 0, 0, 0, 0, ingot, meta, replace, spawnTriesPC, minVeinSize, maxVeinSize, minGenHeight, maxGenHeight, extraData);
	}
	
	public OreMaterial(String ore, int harvestLevel, String item, int minItems, int maxItems, int metadata, int fortuneAdditive, int minXP, int maxXP, boolean replace, int spawnTriesPC, int minVeinSize, int maxVeinSize, int minGenHeight, int maxGenHeight, Collection<Pair<String, Object>> extraData)
	{
		this(ore, harvestLevel, item, minItems, maxItems, metadata, fortuneAdditive, minXP, maxXP, item, metadata, replace, spawnTriesPC, minVeinSize, maxVeinSize, minGenHeight, maxGenHeight, extraData);
	}
	
	public OreMaterial(String ore, int harvestLevel, String item, int minItems, int maxItems, int metadata, int fortuneAdditive, int minXP, int maxXP, String ingot, int meta, boolean replace, int spawnTriesPC, int minVeinSize, int maxVeinSize, int minGenHeight, int maxGenHeight, Collection<Pair<String, Object>> extras)
	{
		this.name = new ResourceLocation(ore);
		this.harvestLevel = harvestLevel;
		this.itemDropped = new ResourceLocation(item);
		this.minItemDrops = minItems;
		this.maxItemDrops = maxItems;
		this.metaDropped = metadata;
		this.fortuneAdditive = fortuneAdditive;
		this.minXP = minXP;
		this.maxXP = maxXP;
		this.ingot = new ResourceLocation(ingot);
		this.ingotMeta = meta;
		this.replace = replace;
		List<OreType> whitelist = Lists.newArrayList();
		Pair<String, String> p;
		for(OreType type : OreCategory.getAllOreTypes())
			if(extras.contains(p = Pair.of(type.getResourceName().getResourceDomain(), type.getResourceName().getResourcePath())))
				extras.remove(p);
			else
				whitelist.add(type);
		this.extras.addAll(extras);
		this.defaultProperties = new OreProperties(this, true, spawnTriesPC, minVeinSize, maxVeinSize, minGenHeight, maxGenHeight, whitelist, this.extras);
	}
	
	public ResourceLocation getName()
	{
		return this.name;
	}
	
	public Item getItem()
	{
		return ForgeRegistries.ITEMS.getValue(this.itemDropped);
	}
	
	public int getMinItems()
	{
		return this.minItemDrops;
	}
	
	public int getMaxItems()
	{
		return this.maxItemDrops;
	}
	
	public int getMetadata()
	{
		return this.metaDropped < 0 ? MetalExtras_OH.ores.indexOf(this) : this.metaDropped;
	}
	
	public int getFortuneAdditive()
	{
		return this.fortuneAdditive;
	}
	
	public int getMinXP()
	{
		return this.minXP;
	}
	
	public int getMaxXP()
	{
		return this.maxXP;
	}
	
	public int getHarvestLevel()
	{
		return this.harvestLevel;
	}
	
	public ItemStack getIngot()
	{
		return new ItemStack(ForgeRegistries.ITEMS.getValue(this.ingot), 1, this.ingotMeta);
	}
	
	public List<Object> getExtraData()
	{
		return Lists.newArrayList(this.extras);
	}
	
	public boolean shouldReplaceExisting()
	{
		return replace;
	}
	
	public boolean addBlockToList(BlockOre block)
	{
		for(BlockOre b : this.blocks)
			if(b.getOreCategory().equals(block.getOreCategory()))
				return false;
		this.blocks.add(block);
		return true;
	}
	
	public Set<BlockOre> getBlocks()
	{
		return Sets.newHashSet(this.blocks);
	}
	
	@Nullable
	public IBlockState applyBlockState(OreType type)
	{
		for(BlockOre ore : this.blocks)
			if(ore.containsOreType(type))
				return ore.withOreType(type);
		return null;
	}
	
	public boolean setOreProperties(OreProperties properties)
	{
		if(this.properties != null)
			return false;
		this.properties = properties;
		return true;
	}
	
	public OreProperties getOreProperties()
	{
		return this.properties;
	}
	
	public OreProperties getDefaultOrePropertie()
	{
		return this.defaultProperties;
	}
	
	public int hashCode()
	{
		return this.getName().hashCode();
	}
}
