package metalextras.config;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import api.metalextras.OreType;
import api.metalextras.OreTypeDictionary;
import api.metalextras.OreTypes;
import api.metalextras.OreUtils;
import continuum.essentials.config.IConfigSubHandler;
import continuum.essentials.config.IConfigValue;
import net.minecraft.util.ResourceLocation;

public class OreConfigHandler implements IConfigSubHandler
{
	public final String name;
	public final Predicate<Collection<OreTypeDictionary>> defaultWhitelist;
	public final IConfigValue<Boolean> spawnEnabled;
	public final IConfigValue<Integer> spawnTries;
	public final IConfigValue<Integer> minHeight;
	public final IConfigValue<Integer> maxHeight;
	public final IConfigValue<Float> minTemperature;
	public final IConfigValue<Float> maxTemperature;
	public final IConfigValue<Integer> veinSize;
	public List<OreType> whitelist;
	
	public OreConfigHandler(String name, boolean spawnEnabled, int spawnTries, int minHeight, int maxHeight, float minTemperature, float maxTemperature, int veinSize, Predicate<Collection<OreTypeDictionary>> whitelist)
	{
		this.name = name;
		this.spawnEnabled = new IConfigValue.Impl<Boolean>("spawn_enabled", "config.metalextras:value.spawn_enabled.name", spawnEnabled);
		this.spawnTries = new IConfigValue.Impl<Integer>("spawn_attempts_per_chunk", "config.metalextras:value.spawn_tries.name", spawnTries, 1, Integer.MAX_VALUE);
		this.minHeight = new IConfigValue.Impl<Integer>("minimum_generation_height", "config.metalextras:value.min_height.name", minHeight, 0, 256)
		{
			@Override
			public Integer getMaxValue()
			{
				return OreConfigHandler.this.maxHeight.getCurrentValue() - 1;
			}
		};
		this.maxHeight = new IConfigValue.Impl<Integer>("maximum_generation_height", "config.metalextras:value.max_height.name", maxHeight, 0, 256)
		{
			@Override
			public Integer getMinValue()
			{
				return OreConfigHandler.this.minHeight.getCurrentValue() + 1;
			}
		};
		this.minTemperature = new IConfigValue.Impl<Float>("minimum_generation_temperature", "config.metalextras:value.min_temperature.name", minTemperature, Float.MIN_VALUE, Float.MAX_VALUE)
		{
			@Override
			public Float getMaxValue()
			{
				return OreConfigHandler.this.maxTemperature.getCurrentValue();
			}
		};
		this.maxTemperature = new IConfigValue.Impl<Float>("maximum_generation_temperature", "config.metalextras:value.max_temperature.name", maxTemperature, Float.MIN_VALUE, Float.MAX_VALUE)
		{
			@Override
			public Float getMinValue()
			{
				return OreConfigHandler.this.minTemperature.getCurrentValue();
			}
		};
		this.veinSize = new IConfigValue.Impl<Integer>("maximum_vein_size", "config.metalextras:value.vein_size.name", veinSize, 1, Integer.MAX_VALUE);
		this.defaultWhitelist = whitelist;
		this.reset(false);
	}
	
	private boolean reset(boolean simulate)
	{
		boolean difference = !this.spawnEnabled.isDefault() || !this.spawnTries.isDefault() || this.minHeight.isDefault() || !this.maxHeight.isDefault() || !this.minTemperature.isDefault() || !this.maxTemperature.isDefault() || !this.veinSize.isDefault() || this.whitelist == null ? false : !Iterables.all(this.whitelist, new Predicate<OreType>()
		{
			public boolean apply(OreType type)
			{
				return OreConfigHandler.this.defaultWhitelist.apply(type.getOreTypeDictionaryList());
			}
		});
		if(!simulate)
		{
			this.spawnEnabled.resetToDefault();
			this.spawnTries.resetToDefault();
			this.minHeight.resetToDefault();
			this.maxHeight.resetToDefault();
			this.minTemperature.resetToDefault();
			this.maxTemperature.resetToDefault();
			this.veinSize.resetToDefault();
			this.whitelist = OreUtils.getTypeCollectionsRegistry().getValues().isEmpty() ? null : getWhitelist(this.defaultWhitelist);
		}
		return difference;
	}
	
	@Override
	public void read(JsonObject object)
	{
		this.spawnEnabled.read(object);
		this.spawnTries.read(object);
		this.minHeight.read(object);
		this.maxHeight.read(object);
		this.minTemperature.read(object);
		this.maxTemperature.read(object);
		this.veinSize.read(object);
		try
		{
			JsonElement element = object.get("type_whitelist");
			this.whitelist = Lists.newArrayList();
			if(element == null || !element.isJsonObject())
				this.whitelist = getWhitelist(this.defaultWhitelist);
			else
			{
				JsonObject object1 = element.getAsJsonObject();
				for(Entry<String, JsonElement> entry : object1.entrySet())
				{
					ResourceLocation location = new ResourceLocation(entry.getKey());
					if(entry.getKey().equals(location.toString()))
					{
						OreType type = OreUtils.findOreType(location);
						if(type != null)
						{
							try
							{
								if(entry.getValue().getAsBoolean())
									this.whitelist.add(type);
							}
							catch(Exception e)
							{
								
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			
		}
	}
	
	@Override
	public void write(JsonObject baseObj)
	{
		this.spawnEnabled.write(baseObj);
		this.spawnTries.write(baseObj);
		this.minHeight.write(baseObj);
		this.maxHeight.write(baseObj);
		this.minTemperature.write(baseObj);
		this.maxTemperature.write(baseObj);
		this.veinSize.write(baseObj);
		JsonElement whitelistElement = baseObj.get("type_whitelist");
		JsonObject whitelistObject = whitelistElement == null || !whitelistElement.isJsonObject() ? new JsonObject() : whitelistElement.getAsJsonObject();
		for(OreTypes types : OreUtils.getTypeCollectionsRegistry())
			for(OreType type : types)
				whitelistObject.addProperty(type.getRegistryName().toString(), this.whitelist == null ? this.defaultWhitelist.apply(type.getOreTypeDictionaryList()) : this.whitelist.contains(type));
		baseObj.add("type_whitelist", whitelistObject);
	}
	
	@Override
	public String getName()
	{
		return this.name;
	}
	
	@Override
	public File getFile(File origin)
	{
		return new File(origin, "ores.json");
	}
	
	private static List<OreType> getWhitelist(Predicate<Collection<OreTypeDictionary>> defaultWhitelist)
	{
		List<OreType> whitelist = Lists.newArrayList();
		for(OreTypes types : OreUtils.getTypeCollectionsRegistry())
			for(OreType type : types)
				if(defaultWhitelist.apply(type.getOreTypeDictionaryList()))
					whitelist.add(type);
		return whitelist;
	}
}