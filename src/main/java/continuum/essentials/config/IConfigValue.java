package continuum.essentials.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.util.JsonUtils;

public abstract class IConfigValue<O>
{
	public abstract String getName();
	public abstract String getLangKey();
	public abstract O getDefaultValue();
	
	public O getMinValue()
	{
		return null;
	}
	
	public O getMaxValue()
	{
		return null;
	}
	
	public abstract O getCurrentValue();
	public abstract void setCurrentValue(O value);
	
	public boolean requiresWorldRestart()
	{
		return false;
	}
	
	public boolean requiresMCRestart()
	{
		return false;
	}
	
	public boolean isDefault()
	{
		return this.getDefaultValue() == this.getCurrentValue();
	}
	
	public void resetToDefault()
	{
		this.setCurrentValue(this.getDefaultValue());
	}
	
	public void read(JsonObject object)
	{
		Class<?> c = this.getDefaultValue().getClass();
		if(c == Boolean.class)
			((IConfigValue<Boolean>)this).setCurrentValue(JsonUtils.getBoolean(object, this.getName(), Boolean.valueOf(this.getDefaultValue().toString())));
		else if(c == Integer.class)
			((IConfigValue<Integer>)this).setCurrentValue(JsonUtils.getInt(object, this.getName(), Integer.valueOf(this.getDefaultValue().toString())));
		else if(c == Float.class)
			((IConfigValue<Float>)this).setCurrentValue(JsonUtils.getFloat(object, this.getName(), Float.valueOf(this.getDefaultValue().toString())));
		else if(c == String.class)
			((IConfigValue<String>)this).setCurrentValue(JsonUtils.getString(object, this.getName(), String.valueOf(this.getDefaultValue().toString())));
		else
			throw new IllegalStateException("Didn't find a valid primitive class to go along with JsonElement. Override this method and handle this seperately.");
	}
	
	public void write(JsonObject object)
	{
		Class<?> c = this.getDefaultValue().getClass();
		if(c == Boolean.class)
			object.addProperty(this.getName(), Boolean.valueOf(this.getCurrentValue().toString()));
		else if(c == Integer.class)
			object.addProperty(this.getName(), Math.max(Integer.valueOf(this.getMinValue().toString()), Math.min(Integer.valueOf(this.getMaxValue().toString()), Integer.valueOf(this.getCurrentValue().toString()))));
		else if(c == Double.class)
			object.addProperty(this.getName(), Math.max(Double.valueOf(this.getMinValue().toString()), Math.min(Double.valueOf(this.getMaxValue().toString()), Double.valueOf(this.getCurrentValue().toString()))));
		else if(c == Float.class)
			object.addProperty(this.getName(), Math.max(Float.valueOf(this.getMinValue().toString()), Math.min(Float.valueOf(this.getMaxValue().toString()), Float.valueOf(this.getCurrentValue().toString()))));
		else if(c == Long.class)
			object.addProperty(this.getName(), Math.max(Long.valueOf(this.getMinValue().toString()), Math.min(Long.valueOf(this.getMaxValue().toString()), Long.valueOf(this.getCurrentValue().toString()))));
		else if(c == String.class)
			object.addProperty(this.getName(), String.valueOf(this.getCurrentValue().toString()));
		else
			throw new IllegalStateException("Didn't find a valid primitive class to go along with JsonElement. Override this method and handle this seperately.");
	}
	
	public static class Impl<O> extends IConfigValue<O>
	{
		private final String name;
		private final String langKey;
		private final O defaultValue;
		private final O minValue;
		private final O maxValue;
		private O currentValue;
		
		public Impl(String name, String langKey, O defaultValue)
		{
			this(name, langKey, defaultValue, null, null);
		}
		
		public Impl(String name, String langKey, O defaultValue, O minValue, O maxValue)
		{
			this.name = name;
			this.langKey = langKey;
			this.currentValue = this.defaultValue = defaultValue;
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
		
		@Override
		public String getName()
		{
			return this.name;
		}
		
		@Override
		public String getLangKey()
		{
			return this.langKey;
		}
		@Override
		public O getDefaultValue()
		{
			return this.defaultValue;
		}
		
		@Override
		public O getMinValue()
		{
			return this.minValue;
		}
		
		public O getMaxValue()
		{
			return this.maxValue;
		}
		
		@Override
		public O getCurrentValue()
		{
			return this.currentValue;
		}
		
		@Override
		public void setCurrentValue(O value)
		{
			this.currentValue = value;
		}
	}
}
