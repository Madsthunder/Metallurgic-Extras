package continuum.metalextras.loaders;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import continuum.api.metalextras.IOre;
import continuum.api.metalextras.IOreData;
import continuum.api.metalextras.IOreType;
import continuum.api.metalextras.OrePredicate;
import continuum.api.metalextras.OreProperties;
import continuum.essentials.mod.CTMod;
import continuum.essentials.mod.ObjectLoader;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.block.state.IBlockState;
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
		ores = new Configuration(new File(folderDir, "Ores.cfg"));
	}
	
	public void pre(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		this.loadAllOreProperties(mod.getObjectHolder());
	}
	
	public void init(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		this.loadAllOreProperties(mod.getObjectHolder());
	}
	
	@Override
	public void post(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		this.loadAllOreProperties(mod.getObjectHolder());
		ores.save();
	}
	
	private void loadAllOreProperties(MetalExtras_OH holder)
	{
		ores.load();
		holder.oreProperties.clear();
		for(IOreData data : MetalExtras_OH.ores)
			this.loadOreProperties(holder, data, data.getBlacklist(), data.getExtraData().toArray());
	}
	
	private void loadOreProperties(MetalExtras_OH holder, IOreData data, List<IOreType> blacklist, Object... defaults)
	{
		ConfigCategory category = ores.getCategory(data.getOreName().toString());
		Integer i = 0;
		ArrayList<String> order = new ArrayList<String>();
		Boolean spawn = this.get(category, "shouldSpawn", true).getBoolean();
		Boolean randomize = this.get(category, "randomizeVeinSize", data.getDefaultRandomizeVeinSize()).getBoolean();
		List<IOreType> materials = Lists.newArrayList();
		for(IOreType type : MetalExtras_OH.oreTypes)
			if(this.get(category, type.getName(), !blacklist.contains(type)).getBoolean())
				materials.add(type);
		Integer tries = this.get(category, "spawnTriesPerChunk", data.getDefaultSpawnTriesPerChunk()).getInt();
		Integer size = this.get(category, "maxVeinSize", data.getDefaultMaxVeinSize()).getInt();
		Integer height0 = this.get(category, "minGenHeight", data.getDefaultMinGenHeight()).getInt();
		Integer height1 = this.get(category, "maxGenHeight", data.getDefaultMaxGenHeight()).getInt();
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
		for(IOreType type : MetalExtras_OH.oreTypes)
			order.add(type.getName());
		ores.setCategoryPropertyOrder(data.getOreName().toString(), order);
		OreProperties properties = new OreProperties(data.getOreName().getResourcePath(), spawn, randomize, materials, tries, size, height0, height1, extraProperties);
		if(data.getOre() != null)
		{
			IOre ore = data.getOre();
			ore.setOreProperties(properties);
			HashMap<IBlockState, IBlockState> validStates = Maps.newHashMap();
			for(IOreType type : properties.getTypes()) validStates.put(type.getState(), ore.applyBlockState(type));
			holder.orePredicates.put(data.getOreName(), new OrePredicate(validStates));
		}
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
