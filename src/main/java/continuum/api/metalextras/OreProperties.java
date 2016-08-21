package continuum.api.metalextras;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import continuum.essentials.mod.ConfigParser.ConfigCategoryParser;
import continuum.metalextras.loaders.ConfigLoader;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class OreProperties implements Predicate<IBlockState>
{
	private final OreMaterial material;
	private final boolean shouldSpawn;
	private final int spawnTries;
	private final int minVeinSize;
	private final int maxVeinSize;
	private final int minHeight;
	private final int maxHeight;
	private final List<OreType> whitelist;
	private final HashMap<String, Object> extraObjects = Maps.newHashMap();
	private final HashMap<IBlockState, IBlockState> validStates = Maps.newHashMap();
	
	public OreProperties(OreMaterial material, boolean shouldSpawn, int spawnTriesPerChunk, int minVeinSize, int maxVeinSize, int minGenHeight, int maxGenHeight, List<OreType> whitelist, Iterable<Pair<String, Object>> extras)
	{
		this.material = material;
		this.shouldSpawn = shouldSpawn;
		this.spawnTries = spawnTriesPerChunk;
		this.minVeinSize = minVeinSize;
		this.maxVeinSize = maxVeinSize;
		this.minHeight = minGenHeight;
		this.maxHeight = maxGenHeight;
		for(Pair<String, Object> pair : extras)
			this.extraObjects.put(pair.getLeft(), pair.getRight());
		this.whitelist = whitelist;
		for(OreType type : this.whitelist)
			this.validStates.put(type.getState(), material.applyBlockState(type));
	}
	
	public OreProperties(OreMaterial material, ConfigCategoryParser parser)
	{
		this(material, material.getDefaultOrePropertie(), parser);
	}
	
	public OreProperties(OreMaterial material, OreProperties parent, ConfigCategoryParser parser)
	{
		this(material, parser.getBoolean("shouldSpawn", parent.getShouldSpawn()), parser.getInt("spawnTriesPerChunk", parent.getSpawnTriesPerChunk()), parser.getInt("minimumVeinSize", parent.getMinVeinSize()), parser.getInt("maximumVeinSize", parent.getMaxVeinSize()), parser.getInt("minimumGenerationHeight", parent.getMinGenHeight()), parser.getInt("maximumGenerationHeight", parent.getMaxGenHeight()), ConfigLoader.parseOreWhitelist(parser, parent.getWhitelist()), ConfigLoader.parseExtras(parser, parent.getExtras()));
	}
	
	public boolean getShouldSpawn()
	{
		return this.shouldSpawn;
	}
	
	public int getMinVeinSize()
	{
		return this.maxVeinSize;
	}
	
	public int getMaxVeinSize()
	{
		return this.maxVeinSize;
	}
	
	public int getSpawnTriesPerChunk()
	{
		return this.spawnTries;
	}
	
	public int getMinGenHeight()
	{
		return this.minHeight;
	}
	
	public int getMaxGenHeight()
	{
		return this.maxHeight;
	}
	
	public boolean getSpawnInBiome(Biome biome, DimensionType dimension)
	{
		HashMap<String, Object> extraObjects = this.extraObjects;
		Object o;
		if(dimension == DimensionType.NETHER && (o = extraObjects.get("canSpawnInNether")) instanceof Boolean)
			return (boolean)o;
		if(dimension == DimensionType.THE_END && (o = extraObjects.get("canSpawnInEnd")) instanceof Boolean)
			return (boolean)o;
		if((o = extraObjects.get("canSpawnIn" + biome.getBiomeName())) instanceof Boolean)
			return (boolean)o;
		if((o = extraObjects.get("temperatureLessThanOrEqualTo")) instanceof Double)
			return biome.getTemperature() >= (double)o;
		if((o = extraObjects.get("temperatureGreaterThanOrEqualTo")) instanceof Double)
			return biome.getTemperature() <= (double)o;
		return true;
	}
	
	@Override
	public boolean apply(IBlockState state)
	{
		return this.validStates.containsKey(state);
	}
	
	public IBlockState getOre(IBlockState state, World world, BlockPos pos)
	{
		return this.validStates.get(state);
	}
	
	public boolean whitelistContains(OreType type)
	{
		return this.whitelist.contains(type);
	}
	
	public List<OreType> getWhitelist()
	{
		return Lists.newArrayList(this.whitelist);
	}
	
	public List<Pair<String, Object>> getExtras()
	{
		List<Pair<String, Object>> extras = Lists.newArrayList();
		for(Entry<String, Object> entry : this.extraObjects.entrySet())
			extras.add(Pair.of(entry.getKey(), entry.getValue()));
		return extras;
	}
	
	@Override
	public int hashCode()
	{
		return this.material.hashCode();
	}
	
	public boolean equals(Object o)
	{
		return o instanceof OreProperties ? ((OreProperties)o).material == this.material : false;
	}
}
