package continuum.metalextras.world.gen;

import java.util.Random;

import continuum.api.metalextras.IOreData;
import continuum.api.metalextras.OrePredicate;
import continuum.api.metalextras.OreProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class WorldGenMinableAdvanced
{
    private final OreProperties properties;
    private final OrePredicate predicate;

    public WorldGenMinableAdvanced(IOreData data)
    {
    	this.properties = data.getOre().getOreProperties();
    	this.predicate = data.getOre().getPredicate();
    }

    public boolean generate(World world, DimensionType dimension, Integer x, Integer y, Integer z, Random random)
    {
    	Integer maxVeinSize = this.properties.getMaxVeinSize();
    	if(maxVeinSize > 0)
    	{
        	Integer blocksToGenerate = this.properties.getRandomizeGeneration() ? random.nextInt(maxVeinSize) + 1 : maxVeinSize;
            float f = random.nextFloat() * (float)Math.PI;
            double d0 = (double)((float)(x + 8) + MathHelper.sin(f) * (float)blocksToGenerate / 8.0F);
            double d1 = (double)((float)(x + 8) - MathHelper.sin(f) * (float)blocksToGenerate / 8.0F);
            double d2 = (double)((float)(z + 8) + MathHelper.cos(f) * (float)blocksToGenerate / 8.0F);
            double d3 = (double)((float)(z + 8) - MathHelper.cos(f) * (float)blocksToGenerate / 8.0F);
            double d4 = (double)(y + random.nextInt(3) - 2);
            double d5 = (double)(y + random.nextInt(3) - 2);

            for (int i = 0; i < blocksToGenerate; ++i)
            {
                float f1 = (float)i / (float)blocksToGenerate;
                double d6 = d0 + (d1 - d0) * (double)f1;
                double d7 = d4 + (d5 - d4) * (double)f1;
                double d8 = d2 + (d3 - d2) * (double)f1;
                double d9 = random.nextDouble() * (double)blocksToGenerate / 16.0D;
                double d10 = (double)(MathHelper.sin((float)Math.PI * f1) + 1.0F) * d9 + 1.0D;
                double d11 = (double)(MathHelper.sin((float)Math.PI * f1) + 1.0F) * d9 + 1.0D;
                int j = MathHelper.floor_double(d6 - d10 / 2.0D);
                int k = MathHelper.floor_double(d7 - d11 / 2.0D);
                int l = MathHelper.floor_double(d8 - d10 / 2.0D);
                int i1 = MathHelper.floor_double(d6 + d10 / 2.0D);
                int j1 = MathHelper.floor_double(d7 + d11 / 2.0D);
                int k1 = MathHelper.floor_double(d8 + d10 / 2.0D);

                for (int l1 = j; l1 <= i1; ++l1)
                {
                    double d12 = ((double)l1 + 0.5D - d6) / (d10 / 2.0D);

                    if (d12 * d12 < 1.0D)
                    {
                        for (int i2 = k; i2 <= j1; ++i2)
                        {
                            double d13 = ((double)i2 + 0.5D - d7) / (d11 / 2.0D);

                            if (d12 * d12 + d13 * d13 < 1.0D)
                            {
                                for (int j2 = l; j2 <= k1; ++j2)
                                {
                                    double d14 = ((double)j2 + 0.5D - d8) / (d10 / 2.0D);

                                    if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D)
                                    {
                                        BlockPos pos1 = new BlockPos(l1, i2, j2);

                                        IBlockState state = world.getBlockState(pos1);
                                        if(this.properties.getSpawnInBiome(world.getBiomeGenForCoords(pos1), dimension) && state.getBlock().isReplaceableOreGen(state, world, pos1, this.predicate))
                                            world.setBlockState(pos1, this.predicate.getOre(state, world, pos1), 2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
    	}

        return true;
    }
}