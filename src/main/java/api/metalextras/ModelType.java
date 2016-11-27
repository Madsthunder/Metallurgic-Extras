package api.metalextras;

import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum ModelType
{
	IRON,
	EMERALD,
	LAPIS;
	
	public static ModelType getModelType(String name)
	{
		name = name.toUpperCase();
		for(ModelType type : ModelType.values())
			if(type.name().equals(name))
				return type;
		return EnumHelper.addEnum(ModelType.class, name, new Class[0]);
	}
}
