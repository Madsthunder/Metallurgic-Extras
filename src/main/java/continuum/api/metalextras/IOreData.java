package continuum.api.metalextras;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public interface IOreData
{
	public ResourceLocation getOreName();
	public Item getItem();
	public IOre getOre();
	public void setOre(IOre ore);
	public Integer getMinItems();
	public Integer getMaxItems();
	public Integer getMetadata();
	public Integer getFortuneAdditive();
	public Integer getMinXP();
	public Integer getMaxXP();
	public Integer getHarvestLevel();
	public ItemStack getIngot();
	public Boolean getDefaultRandomizeVeinSize();
	public Integer getDefaultSpawnTriesPerChunk();
	public Integer getDefaultMaxVeinSize();
	public Integer getDefaultMinGenHeight();
	public Integer getDefaultMaxGenHeight();
	public List<IOreType> getBlacklist();
	public List<Object> getExtraData();
	public Boolean shouldReplaceExisting();
	
	public static class Impl implements IOreData
	{
		private final ResourceLocation name;
		private final Integer harvestLevel;
		private final ResourceLocation itemDropped;
		private final Integer minItemDrops;
		private final Integer maxItemDrops;
		private final Integer metaDropped;
		private final Integer fortuneAdditive;
		private final Integer minXP;
		private final Integer maxXP;
		private final ResourceLocation ingot;
		private final Integer ingotMeta;
		private final Boolean replace;
		private final Boolean randomizeVeinSize;
		private final Integer spawnTriesPC;
		private final Integer maxVeinSize;
		private final Integer minGenHeight;
		private final Integer maxGenHeight;
		private final List<IOreType> blacklist;
		private final List<Object> extraData;
		private IOre ore;
		
		public Impl(String ore, Integer harvestLevel, String ingot, Integer meta, Boolean replace, Integer spawnTriesPC, Integer maxVeinSize, Integer minGenHeight, Integer maxGenHeight, Pair... extraData)
		{
			this(ore, harvestLevel, "metalextras:ore", 1, 1, null, 0, 0, 0, ingot, meta, replace, spawnTriesPC, maxVeinSize, minGenHeight, maxGenHeight, extraData);
		}
		
		public Impl(String ore, Integer harvestLevel, String item, Boolean replace, Integer spawnTriesPC, Integer maxVeinSize, Integer minGenHeight, Integer maxGenHeight, Pair... extraData)
		{
			this(ore, harvestLevel, item, 0, replace, spawnTriesPC, maxVeinSize, minGenHeight, maxGenHeight, extraData);
		}
		
		public Impl(String ore, Integer harvestLevel, String item, String ingot, Integer meta, Boolean replace, Integer spawnTriesPC, Integer maxVeinSize, Integer minGenHeight, Integer maxGenHeight, Pair... extraData)
		{
			this(ore, harvestLevel, item, 1, 1, 0, 0, 0, 0, ingot, meta, replace, spawnTriesPC, maxVeinSize, minGenHeight, maxGenHeight, extraData);
		}
		
		public Impl(String ore, Integer harvestLevel, String item, Integer minItems, Integer maxItems, Integer metadata, Integer fortuneAdditive, Integer minXP, Integer maxXP, Boolean replace, Integer spawnTriesPC, Integer maxVeinSize, Integer minGenHeight, Integer maxGenHeight, Pair... extraData)
		{
			this(ore, harvestLevel, item, minItems, maxItems, metadata, fortuneAdditive, minXP, maxXP, item, metadata, replace, spawnTriesPC, maxVeinSize, minGenHeight, maxGenHeight, extraData);
		}
		
		public Impl(String ore, Integer harvestLevel, String item, Integer minItems, Integer maxItems, Integer metadata, Integer fortuneAdditive, Integer minXP, Integer maxXP, String ingot, Integer meta, Boolean replace, Integer spawnTriesPC, Integer maxVeinSize, Integer minGenHeight, Integer maxGenHeight, Pair... extraData)
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
			List<IOreType> blacklist = Lists.newArrayList();
			List<Object> extras = Lists.newArrayList();
			for(Pair pair : extraData)
				if(pair.getLeft() instanceof String && pair.getRight() instanceof String)
				{
					for(IOreType type : MetalExtras_OH.oreTypes)
						if(type.getResourceName().getResourceDomain().equals(pair.getLeft()) && type.getResourceName().getResourcePath().equals(pair.getRight()))
							blacklist.add(type);
				}
				else
					extras.addAll(Lists.newArrayList(pair.getLeft(), pair.getRight()));
			this.randomizeVeinSize = true;
			this.spawnTriesPC = spawnTriesPC;
			this.maxVeinSize = maxVeinSize;
			this.minGenHeight = minGenHeight;
			this.maxGenHeight = maxGenHeight;
			this.blacklist = blacklist;
			this.extraData = extras;
		}
		
		@Override
		public ResourceLocation getOreName()
		{
			return this.name;
		}

		@Override
		public Item getItem()
		{
			return ForgeRegistries.ITEMS.getValue(this.itemDropped);
		}

		@Override
		public Integer getMinItems()
		{
			return this.minItemDrops;
		}

		@Override
		public Integer getMaxItems()
		{
			return this.maxItemDrops;
		}

		@Override
		public Integer getMetadata()
		{
			return this.metaDropped == null ? MetalExtras_OH.ores.indexOf(this) : this.metaDropped;
		}

		@Override
		public Integer getFortuneAdditive()
		{
			return this.fortuneAdditive;
		}

		@Override
		public Integer getMinXP()
		{
			return this.minXP;
		}

		@Override
		public Integer getMaxXP()
		{
			return this.maxXP;
		}

		@Override
		public Integer getHarvestLevel()
		{
			return this.harvestLevel;
		}

		@Override
		public IOre getOre()
		{
			return this.ore;
		}

		@Override
		public void setOre(IOre ore)
		{
			this.ore = ore;
		}

		@Override
		public ItemStack getIngot()
		{
			return new ItemStack(ForgeRegistries.ITEMS.getValue(this.ingot), 1, this.ingotMeta);
		}
		
		@Override
		public Boolean getDefaultRandomizeVeinSize()
		{
			return true;
		}

		@Override
		public Integer getDefaultSpawnTriesPerChunk()
		{
			return this.spawnTriesPC;
		}

		@Override
		public Integer getDefaultMaxVeinSize()
		{
			return this.maxVeinSize;
		}

		@Override
		public Integer getDefaultMinGenHeight()
		{
			return this.minGenHeight;
		}

		@Override
		public Integer getDefaultMaxGenHeight()
		{
			return this.maxGenHeight;
		}
		
		@Override
		public List<IOreType> getBlacklist()
		{
			return Lists.newArrayList(this.blacklist);
		}
		
		@Override
		public List<Object> getExtraData()
		{
			return Lists.newArrayList(this.extraData);
		}
		
		@Override
		public Boolean shouldReplaceExisting()
		{
			return replace;
		}
	}
}
