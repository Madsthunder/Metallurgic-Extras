package api.metalextras;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenOre extends WorldGenerator
{
	private final OreProperties.Impl properties;
	
	public WorldGenOre(OreProperties.Impl properties)
	{
		this.properties = properties;
	}
	
	public boolean generate(World world, Random random, BlockPos pos)
	{
		return OreUtils.generateOres(world, pos, random, this.properties.getVeinSize(world, world.getBiomeGenForCoords(pos)), this.properties);
	}
}