package continuum.metalextras.loaders;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import continuum.api.metalextras.OreCategory;
import continuum.api.metalextras.OreMaterial;
import continuum.api.metalextras.OreProperties;
import continuum.api.metalextras.OreType;
import continuum.essentials.mod.CTMod;
import continuum.essentials.mod.ConfigParser;
import continuum.essentials.mod.ConfigParser.ConfigCategoryParser;
import continuum.essentials.mod.ObjectLoader;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraftforge.common.config.Configuration;

public class ConfigLoader implements ObjectLoader<MetalExtras_OH, MetalExtras_EH>
{
	private static final File configFolder = new File("config\\Continuum\\Metallurgic Extras");
	private static final File oresFolder = new File(configFolder, "Ores");
	private final HashMap<String, ConfigParser> configs = Maps.newHashMap();
	
	@Override
	public void init(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		String modid;
		for(OreMaterial material : MetalExtras_OH.ores)
			if(!configs.containsKey(modid = material.getName().getResourceDomain()))
				configs.put(modid, new ConfigParser(new File(oresFolder, modid + ".cfg")));
	}
	
	@Override
	public void post(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		for(OreMaterial material : MetalExtras_OH.ores)
			this.loadOreProperties(material);
		for(ConfigParser parser : this.configs.values())
			parser.getConfig().save();
	}
	
	private void loadOreProperties(OreMaterial material)
	{
		material.setOreProperties(new OreProperties(material, this.configs.get(material.getName().getResourceDomain()).getCategoryParser(material.getName().getResourcePath())));
	}
	
	public static List<OreType> parseOreWhitelist(ConfigCategoryParser parser, List<OreType> defaults)
	{
		List<OreType> whitelist = Lists.newArrayList();
		for(OreType type : OreCategory.getAllOreTypes())
			if(parser.getBoolean("spawnInside." + type.getResourceName().toString(), defaults.contains(type)))
				whitelist.add(type);
		return whitelist;
	}
	
	public static List<Pair<String, Object>> parseExtras(ConfigCategoryParser parser, List<Pair<String, Object>> defaults)
	{
		List<Pair<String, Object>> extras = Lists.newArrayList();
		for(Pair<String, Object> pair : defaults)
			extras.add(Pair.of(pair.getLeft(), parser.get(pair.getLeft(), pair.getRight())));
		return extras;
	}
	
	@Override
	public String getName()
	{
		return "Configuration";
	}
}
