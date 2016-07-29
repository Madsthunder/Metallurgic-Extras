package continuum.api.metalextras;

import java.util.HashMap;

public class OreProperties
{
	public Boolean shouldSpawn;
	public Boolean randomizeGeneration;
	public Boolean[] materials;
	public Integer spawnTries;
	public Integer veinSize;
	public Integer minHeight;
	public Integer maxHeight;
	public HashMap<String, Object> extraObjects;
	
	public OreProperties(Boolean shouldSpawn, Boolean randomizeOreSize, Boolean[] materialsToSpawnIn, Integer spawnTriesPerChunk, Integer maxVeinSize, Integer minGenHeight, Integer maxGenHeight, HashMap<String, Object> extraObjects)
	{
		this.shouldSpawn = shouldSpawn;
		this.randomizeGeneration = randomizeOreSize;
		this.materials = materialsToSpawnIn;
		this.spawnTries = spawnTriesPerChunk;
		this.veinSize = maxVeinSize;
		this.minHeight = minGenHeight;
		this.maxHeight = maxGenHeight;
		this.extraObjects = extraObjects;
	}
}
