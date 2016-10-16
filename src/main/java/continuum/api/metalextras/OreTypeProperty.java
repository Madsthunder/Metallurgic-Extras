package continuum.api.metalextras;

import java.util.List;

import com.google.common.base.Optional;

import net.minecraft.block.properties.IProperty;
import net.minecraft.util.ResourceLocation;

public class OreTypeProperty implements IProperty<OreType>
{
	private final OreCategory category;
	
	public OreTypeProperty(OreCategory category)
	{
		this.category = category;
	}
	
	@Override
	public String getName()
	{
		return "type";
	}

	@Override
	public List<OreType> getAllowedValues()
	{
		return this.category.getOreTypes();
	}

	@Override
	public Class<OreType> getValueClass()
	{
		return OreType.class;
	}

	@Override
	public Optional<OreType> parseValue(String value)
	{
		for(OreType type : this.category.getOreTypes())
			if(type.toString().equals(value))
				return Optional.fromNullable(type);
		return Optional.absent();
	}

	@Override
	public String getName(OreType value)
	{
		return value.toString();
	}
	
}
