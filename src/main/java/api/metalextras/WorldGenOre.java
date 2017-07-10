package api.metalextras;

import java.util.Random;

import metalextras.newores.NewOreType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenOre extends WorldGenerator
{
    private final NewOreType type;
    
	public WorldGenOre(NewOreType type)
	{
		this.type = type;
	}
	
	public boolean generate(World world, Random random, BlockPos pos)
	{
		return this.type.generation.canGenerate() && OreUtils.generateOres(world, pos, random, random.nextInt(this.type.generation.getVeinSize()) + 1, this.type);
	}
}