package continuum.api.metalextras;

import java.util.HashMap;

import com.google.common.base.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OrePredicate implements Predicate<IBlockState>
{
	public final HashMap<IBlockState, IBlockState> validStates;
	
	public OrePredicate(HashMap<IBlockState, IBlockState> validStates)
	{
		this.validStates = validStates;
	}
	
	@Override
	public boolean apply(IBlockState state)
	{
		return validStates.containsKey(state);
	}
	
	public IBlockState getOre(IBlockState state, World world, BlockPos pos)
	{
		return validStates.get(state);
	}
}
