package api.metalextras;

import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockStateContainer.Builder;

public class OreTypeProperty implements IProperty<OreType>
{
	private final OreTypes types;
	private final Map<BlockOre, BlockStateContainer> containers = Maps.newHashMap();
	
	public OreTypeProperty(OreTypes types)
	{
		this.types = types;
	}
	
	@Override
	public String getName()
	{
		return "type";
	}
	
	@Override
	public final List<OreType> getAllowedValues()
	{
		return this.types.getOreTypes();
	}
	
	@Override
	public Class<OreType> getValueClass()
	{
		return OreType.class;
	}
	
	@Override
	public Optional<OreType> parseValue(String value)
	{
		for(OreType type : this.types.getOreTypes())
			if(type.getRegistryName().toString().replaceAll(":", "_").equals(value))
				return Optional.fromNullable(type);
		return Optional.absent();
	}
	
	@Override
	public String getName(OreType type)
	{
		return type.getRegistryName().toString().replaceAll(":", "_");
	}
	
	public final OreTypes getTypes()
	{
		return this.types;
	}
	
	public final BlockStateContainer getBlockState(BlockOre ore)
	{
		return this.containers.get(ore);
	}
	
	public final void addListener(BlockOre ore)
	{
	    this.containers.put(ore, this.types.getOreTypes().size() > 1 ? new Builder(ore).add(this).build() : new Builder(ore).build());
	}
}
