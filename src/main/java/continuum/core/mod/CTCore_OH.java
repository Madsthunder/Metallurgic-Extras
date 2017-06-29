package continuum.core.mod;

import java.util.HashMap;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;

public class CTCore_OH
{
	
	public static final HashMap<ResourceLocation, Function<Object, IModel>> models = Maps.newHashMap();
}
