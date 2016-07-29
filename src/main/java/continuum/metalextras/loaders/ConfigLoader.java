package continuum.metalextras.loaders;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.Lists;

import continuum.api.metalextras.OreProperties;
import continuum.essentials.mod.CTMod;
import continuum.essentials.mod.ObjectLoader;
import continuum.metalextras.blocks.BlockOreGround.EnumGroundType;
import continuum.metalextras.blocks.BlockOreRock.EnumRockType;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

public class ConfigLoader implements ObjectLoader<MetalExtras_OH, MetalExtras_EH>
{
	private static Configuration ores;
	
	@Override
	public void construction(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		mod.getObjectHolder().oreProperties = Lists.newArrayList();
		File folderDir = new File((File)(FMLInjectionData.data()[6]), "config\\Continuum\\Metallurgic Extras");
		folderDir.mkdirs();
		Boolean T = true;
		Boolean F = false;
		ores = new Configuration(new File(folderDir, "Ores.cfg"));
		ores.load();
		this.loadOreProperties(mod, "copper ore", T, /**Rocks*/T, T, T, T, T, T, T, T, T, /**Grounds*/T, T, T, T, T, T, T, 20, 9, 0, 64);
		this.loadOreProperties(mod, "tin ore", T, /**Rocks*/T, T, T, T, T, T, T, F, T, /**Grounds*/T, T, T, T, T, T, T, 20, 9, 0, 64);
		this.loadOreProperties(mod, "aluminum ore", T, /**Rocks*/T, T, T, T, T, T, T, T, T, /**Grounds*/F, T, F, F, F, F, F, 6, 5, 32, 128);
		this.loadOreProperties(mod, "lead ore", T, /**Rocks*/T, T, T, T, T, T, T, F, T, /**Grounds*/F, F, F, F, F, F, F, 8, 8, 32, 64);
		this.loadOreProperties(mod, "silver ore", T, /**Rocks*/T, T, T, T, F, F, T, F, T, /**Grounds*/F, F, F, F, F, F, F, 8, 8, 0, 32);
		this.loadOreProperties(mod, "mystery ore", T, /**Rocks*/F, F, F, F, F, F, F, T, F, /**Grounds*/F, F, F, F, F, F, F, 20, 9, 0, 64);
		this.loadOreProperties(mod, "sapphire ore", T, /**Rocks*/T, T, T, T, F, F, F, T, T, /**Grounds*/T, F, F, F, T, F, F, 20, 3, 0, 64, "temperatureLessThanOrEqualTo", 0.2D, "canSpawnInEnd", true);
		this.loadOreProperties(mod, "ruby ore", T, /**Rocks*/T, T, T, T, T, T, T, F, T, /**Grounds*/T, T, T, T, F, F, F, 20, 3, 0, 64, "temperatureGreaterThanOrEqualTo", 1.0D, "canSpawnInNether", true);
		this.loadOreProperties(mod, "coal ore", T, /**Rocks*/T, T, T, T, T, T, T, T, T, /**Grounds*/T, T, T, T, T, T, T, 20, 17, 0, 128);
		this.loadOreProperties(mod, "iron ore", T, /**Rocks*/T, T, T, T, T, T, T, T, T, /**Grounds*/T, T, T, T, T, T, T, 20, 9, 0, 64);
		this.loadOreProperties(mod, "lapis ore", T, /**Rocks*/T, T, T, T, T, T, T, T, T, /**Grounds*/T, T, T, T, T, T, T, 1, 7, 0, 32);
		this.loadOreProperties(mod, "gold ore", T, /**Rocks*/T, T, T, T, T, T, T, T, T, /**Grounds*/T, T, T, T, T, T, T, 2, 9, 0, 32);
		this.loadOreProperties(mod, "redstone ore", T, /**Rocks*/T, T, T, T, T, T, T, T, T, /**Grounds*/T, T, T, T, T, T, T, 8, 8, 0, 16);
		this.loadOreProperties(mod, "emerald ore", T, /**Rocks*/T, T, T, T, T, T, T, T, T, /**Grounds*/T, T, T, T, T, T, T, 3, 1, 0, 28);
		this.loadOreProperties(mod, "diamond ore", T, /**Rocks*/T, T, T, T, T, T, T, T, T, /**Grounds*/T, T, T, T, T, T, T, 1, 8, 0, 16);
		ores.save();
	}
	
	private void loadOreProperties(CTMod<MetalExtras_OH, MetalExtras_EH> mod, String categoryName, Object... defaults)
	{
		ConfigCategory category = ores.getCategory(categoryName);
		Integer i = 0;
		ArrayList<String> order = new ArrayList<String>();
		Boolean spawn = this.get(category, "shouldSpawn", true).getBoolean();
		Boolean randomize = this.get(category, "randomizeVeinSize", (Boolean)(defaults[i++])).getBoolean();
		Boolean[] materials = new Boolean[EnumRockType.values().length + EnumGroundType.values().length];
		for(EnumRockType type : EnumRockType.values())
			materials[type.ordinal()] = this.get(category, type.getName(), (Boolean)(defaults[i++])).getBoolean();
		for(EnumGroundType type : EnumGroundType.values())
			materials[type.ordinal() + EnumRockType.values().length] = this.get(category, type.getName(), (Boolean)(defaults[i++])).getBoolean();
		Integer tries = this.get(category, "spawnTriesPerChunk", (Integer)(defaults[i++])).getInt();
		Integer size = this.get(category, "maxVeinSize", (Integer)(defaults[i++])).getInt();
		Integer height0 = this.get(category, "minGenHeight", (Integer)(defaults[i++])).getInt();
		Integer height1 = this.get(category, "maxGenHeight", (Integer)(defaults[i++])).getInt();
		Double temperature = null;
		Boolean warmerThan = null;
		Boolean twt = false;
		HashMap<String, Object> extraProperties = new HashMap<String, Object>();
		for(;i != defaults.length;i+=2)
			extraProperties.put((String)defaults[i], this.get(category, (String)defaults[i], defaults[i + 1]));
		if(i != defaults.length && defaults[i] instanceof Double && defaults[i + 1] instanceof Boolean)
		{
			temperature = this.get(category, "temperature", (Double) (defaults[i++])).getDouble();
			warmerThan = this.get(category, "warmerThan", (Boolean) (defaults[i++])).getBoolean();
			twt = true;
		}
		order.add("shouldSpawn");
		order.add("minGenHeight");
		order.add("maxGenHeight");
		order.add("maxVeinSize");
		order.add("spawnTriesPerChunk");
		order.add("randomizeVeinSize");
		for(String string : extraProperties.keySet())
			order.add(string);
		for(EnumRockType type : EnumRockType.values())
			order.add(type.getName());
		for(EnumGroundType type : EnumGroundType.values())
			order.add(type.getName());
		ores.setCategoryPropertyOrder(categoryName, order);
		mod.getObjectHolder().oreProperties.add(new OreProperties(spawn, randomize, materials, tries, size, height0, height1, extraProperties));
	}

	
	private Object get(ConfigCategory category, String propertyName, Object _default)
	{
		if(_default instanceof Boolean)
			return get(category, propertyName, (Boolean)_default).getBoolean();
		if(_default instanceof Integer)
			return get(category, propertyName, (Integer)_default).getInt();
		if(_default instanceof Double)
			return get(category, propertyName, (Double)_default).getDouble();
		if(_default instanceof String)
			return get(category, propertyName, (String)_default).getString();
		return null;
	}
	private Property get(ConfigCategory category, String propertyName, Boolean _default)
	{
		Property property = null;
		if(category.containsKey(propertyName))
			property = category.get(propertyName);
		else
			property = new Property(propertyName, _default.toString(), Property.Type.BOOLEAN);
		category.put(propertyName, property);
		return property.setDefaultValue(_default);
	}
	
	private Property get(ConfigCategory category, String propertyName, Integer _default)
	{
		Property property = null;
		if(category.containsKey(propertyName))
			property = category.get(propertyName);
		else
			property = new Property(propertyName, _default.toString(), Property.Type.INTEGER);
		category.put(propertyName, property);
		return property.setDefaultValue(_default);
	}
	
	private Property get(ConfigCategory category, String propertyName, Double _default)
	{
		Property property = null;
		if(category.containsKey(propertyName))
			property = category.get(propertyName);
		else
			property = new Property(propertyName, _default.toString(), Property.Type.DOUBLE);
		category.put(propertyName, property);
		return property.setDefaultValue(_default);
	}
	
	private Property get(ConfigCategory category, String propertyName, String _default)
	{
		Property property = null;
		if(category.containsKey(propertyName))
			property = category.get(propertyName);
		else
			property = new Property(propertyName, _default.toString(), Property.Type.STRING);
		category.put(propertyName, property);
		return property.setDefaultValue(_default);
	}
	
	@Override
	public String getName()
	{
		return "Configuration";
	}
}
