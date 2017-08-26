package metalextras.newores.modules;

import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class WorldContext<M extends OreModule<?, M>, E>
{
	public final M module;
	public final IBlockState state;
	public final IBlockAccess access;
	public final BlockPos pos;
	public final Entity entity;
	public final E extra;

	public WorldContext(M module, IBlockState state, IBlockAccess access, BlockPos pos, Entity entity, E extra)
	{
		this.module = module;
		this.state = state;
		this.access = access;
		this.pos = pos;
		this.entity = entity;
		this.extra = extra;
	}
	
	public <V> Optional<V> processIfWorldPresent(BiFunction<World, WorldContext<M, E>, V> processor)
	{
		return Optional.ofNullable(this.access instanceof World ? processor.apply((World)this.access, this) : null);
	}
	
	public static <M extends OreModule<?, M>> WorldContext<M, Void> of(M module, IBlockState state)
	{
		return of(module, state, null, null, null, null);
	}

	public static <M extends OreModule<?, M>> WorldContext<M, Void> of(M module, IBlockAccess access)
	{
		return of(module, null, access, null, null, null);
	}

	public static <M extends OreModule<?, M>> WorldContext<M, Void> of(M module, Entity entity)
	{
		return of(module, null, entity.world, entity.getPosition(), entity, null);
	}
	
	public static <M extends OreModule<?, M>> WorldContext<M, Void> of(M module, IBlockAccess access, Entity entity)
	{
		return of(module, null, access, entity.getPosition(), entity, null);
	}

	public static <M extends OreModule<?, M>> WorldContext<M, Void> of(M module, IBlockState state, IBlockAccess access, BlockPos pos)
	{
		return of(module, state, access, pos, null, null);
	}

	public static <M extends OreModule<?, M>> WorldContext<M, Void> of(M module, IBlockState state, IBlockAccess access, BlockPos pos, Entity entity)
	{
		return of(module, state, access, pos, entity, null);
	}
	
	public static <M extends OreModule<?, M>, E> WorldContext<M, E> of(M module, IBlockState state, IBlockAccess access, BlockPos pos, Entity entity, E extra)
	{
		return new WorldContext<M, E>(module, state, access, pos, entity, extra);
	}
}
