package continuum.api.metalextras;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;

public class OreProperties
{
	private final String name;
	private final Boolean shouldSpawn;
	private final Boolean randomizeGeneration;
	private final List<IOreType> materials;
	private final Integer spawnTries;
	private final Integer veinSize;
	private final Integer minHeight;
	private final Integer maxHeight;
	private final HashMap<String, Object> extraObjects;
	
	public OreProperties(String name, Boolean shouldSpawn, Boolean randomizeOreSize, List<IOreType> materials, Integer spawnTriesPerChunk, Integer maxVeinSize, Integer minGenHeight, Integer maxGenHeight, HashMap<String, Object> extraObjects)
	{
		this.name = name;
		this.shouldSpawn = shouldSpawn;
		this.randomizeGeneration = randomizeOreSize;
		this.materials = materials;
		this.spawnTries = spawnTriesPerChunk;
		this.veinSize = maxVeinSize;
		this.minHeight = minGenHeight;
		this.maxHeight = maxGenHeight;
		this.extraObjects = extraObjects;
	}
	
	public Boolean getRandomizeGeneration()
	{
		return this.randomizeGeneration;
	}
	
	public List<IOreType> getTypes()
	{
		return Lists.newArrayList(this.materials);
	}
	
	public Integer getMaxVeinSize()
	{
		return this.veinSize;
	}
	
	public Integer getSpawnTriesPerChunk()
	{
		return this.spawnTries;
	}
	
	public Integer getMinGenHeight()
	{
		return this.minHeight;
	}
	
	public Integer getMaxGenHeight()
	{
		return this.maxHeight;
	}
	public Boolean getSpawnInBiome(Biome biome, DimensionType dimension)
	{
		HashMap<String, Object> extraObjects = this.extraObjects;
		Object o;
		if(dimension == DimensionType.NETHER && (o = extraObjects.get("canSpawnInNether")) instanceof Boolean)
			return (Boolean)o;
		if(dimension == DimensionType.THE_END && (o = extraObjects.get("canSpawnInEnd")) instanceof Boolean)
			return (Boolean)o;
		if((o = extraObjects.get("canSpawnIn" + biome.getBiomeName())) instanceof Boolean)
			return (Boolean)o;
		if((o = extraObjects.get("temperatureLessThanOrEqualTo")) instanceof Double)
			return biome.getTemperature() >= (Double)o;
		if((o = extraObjects.get("temperatureGreaterThanOrEqualTo")) instanceof Double)
			return biome.getTemperature() <= (Double)o;
		return true;
	}
	
	@Override
	public int hashCode()
	{
		return this.name.hashCode();
	}
	
	public boolean equals(Object o)
	{
		return o instanceof OreProperties ? ((OreProperties)o).name.equals(this.name) : false;
	}
}
