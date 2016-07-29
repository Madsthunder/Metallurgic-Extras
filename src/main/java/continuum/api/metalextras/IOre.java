package continuum.api.metalextras;

import net.minecraft.world.DimensionType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;

public interface IOre
{
	public String getName();
	public Boolean getRandomizeGeneration();
	public Boolean canSpawnWithMaterial(Object obj);
	public Integer getSpawnTriesPerChunk(IBlockAccess access);
	public Integer getMaxVeinSize(IBlockAccess access);
	public Integer getMinGenHeight(IBlockAccess access);
	public Integer getMaxGenHeight(IBlockAccess access);
	public Boolean getSpawnInBiome(Biome biome, DimensionType dimension);
	public OrePredicate getPredicate();
}
