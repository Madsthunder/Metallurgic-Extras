package metalextras.mod;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import continuum.essentials.config.ConfigElement;
import continuum.essentials.config.IConfigHandler;
import continuum.essentials.hooks.JsonHooks;
import metalextras.ores.properties.ConfigurationOreProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

public class MetalExtras_Config implements IModGuiFactory
{
	public static final File folder = new File("config\\Metallurgic Extras").getAbsoluteFile();
	public static final List<IConfigHandler> handlers = Lists.newArrayList();
	
	public static void refreshAll()
	{
		List<File> files = Lists.newArrayList();
		for(IConfigHandler handler : handlers)
			files.add(handler.getFile(folder));
		for(File file : files)
			refresh(file);
	}
	
	public static void refresh(File directory)
	{
		JsonObject object = getJsonObject(directory);
		read(directory, object);
		write(directory, object);
	}
	
	public static JsonObject getJsonObject(File directory)
	{
		JsonObject object = new JsonObject();
		if(!handlers.isEmpty())
		{
			folder.mkdirs();
			try
			{
				directory.createNewFile();
			}
			catch(IOException exception)
			{
				exception.printStackTrace();
			}
			String json = "";
			try
			{
				json = FileUtils.readFileToString(directory);
			}
			catch(IOException e)
			{
				
			}
			try
			{
				object = new JsonParser().parse(json).getAsJsonObject();
			}
			catch(Exception e)
			{
				if(!json.isEmpty())
					e.printStackTrace();
			}
		}
		return object;
	}
	
	public static void read(File directory, JsonObject object)
	{
		for (IConfigHandler handler : handlers)
		{
			if(handler.getFile(folder).equals(directory))
			{
				JsonObject object1 = new JsonObject();
				if(object.has(handler.getName()))
				{
					JsonElement element = object.get(handler.getName());
					if(element.isJsonObject())
						handler.read(object1 = element.getAsJsonObject());
				}
				handler.write(object1);
				object.add(handler.getName(), object1);
			}
		}
	}
	
	public static void readAll()
	{
		List<File> files = Lists.newArrayList();
		for(IConfigHandler handler : handlers)
			files.add(handler.getFile(folder));
		for(File file : files)
			read(file, getJsonObject(file));
	}
	
	public static void write(File directory, JsonObject object)
	{
		try
		{
			FileUtils.write(directory, JsonHooks.getJson(null, object, 0));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void writeAll()
	{
		List<File> files = Lists.newArrayList();
		for(IConfigHandler handler : handlers)
			files.add(handler.getFile(folder));
		for(File file : files)
			write(file, getJsonObject(file));
	}
	
	@Override
	public void initialize(Minecraft minecraftInstance)
	{
		
	}
	
	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass()
	{
		return MetalExtras_ConfigGui.class;
	}
	
	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
	{
		return null;
	}
	
	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
	{
		return null;
	}
	
	public static class MetalExtras_ConfigGui extends GuiConfig
	{
		public MetalExtras_ConfigGui(GuiScreen parent)
		{
			super(parent, getConfigElements(), "metalextras", false, false, "Metallurgic Extras");
		}
		
		private static List<IConfigElement> getConfigElements()
		{
			return Lists.newArrayList(new DummyCategoryElement("ore_generation", "config.metalextras:category.ore_generation.name", OreGenEntry.class));
		}
		
		public static class OreGenEntry extends CategoryEntry
		{
			public OreGenEntry(GuiConfig config, GuiConfigEntries entries, IConfigElement element)
			{
				super(config, entries, element);
				this.toolTip.clear();
				this.toolTip.add(TextFormatting.GREEN + this.name);
				this.toolTip.add(TextFormatting.YELLOW + I18n.format("config.metalextras:category.ore_generation.tooltip"));
			}
			
			@Override
			protected GuiScreen buildChildScreen()
			{
				return new GuiConfig(this.owningScreen, getConfigElements(), this.owningScreen.modID, "ore_generation", this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart, this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, I18n.format(this.configElement.getLanguageKey()));
			}
			
			private static List<IConfigElement> getConfigElements()
			{
				List<IConfigElement> elements = Lists.newArrayList();
				for (Entry<String, ConfigurationOreProperties> entry : ConfigurationOreProperties.cfgs.entrySet())
					elements.add(new DummyCategoryElement(entry.getKey(), entry.getValue().getOreMaterial().getLanguageKey() + ".name", OrePropertiesEntry.class));
				
				return elements;
			}
			
			public static class OrePropertiesEntry extends CategoryEntry
			{
				public OrePropertiesEntry(GuiConfig config, GuiConfigEntries entries, IConfigElement element)
				{
					super(config, entries, element);
					this.toolTip.clear();
					this.toolTip.add(TextFormatting.GREEN + this.name);
					this.toolTip.add(TextFormatting.YELLOW + I18n.format("config.metalextras:category.generator_options_entry.tooltip", this.name));
				}
				
				@Override
				protected GuiScreen buildChildScreen()
				{
					return new GuiConfig(this.owningScreen, getConfigElements(this.configElement), this.owningScreen.modID, this.configElement.getName() + "_properties", this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart, this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, I18n.format(this.configElement.getLanguageKey()));
				}
				
				private static List<IConfigElement> getConfigElements(IConfigElement element)
				{
					String name = I18n.format(element.getLanguageKey());
					ConfigurationOreProperties properties = ConfigurationOreProperties.cfgs.get(element.getName());
					List<IConfigElement> elements = Lists.newArrayList();
					elements.add(new ConfigElement(properties.handler.spawnEnabled, ConfigGuiType.BOOLEAN, func("config.metalextras:value.spawn_enabled.tooltip", properties)));
					elements.add(new ConfigElement(properties.handler.spawnTries, ConfigGuiType.INTEGER, func("config.metalextras:value.spawn_tries.tooltip", properties)));
					elements.add(new ConfigElement(properties.handler.minHeight, ConfigGuiType.INTEGER, func("config.metalextras:value.min_height.tooltip", properties)));
					elements.add(new ConfigElement(properties.handler.maxHeight, ConfigGuiType.INTEGER, func("config.metalextras:value.max_height.tooltip", properties)));
					elements.add(new ConfigElement(properties.handler.veinSize, ConfigGuiType.INTEGER, func("config.metalextras:value.vein_size.tooltip", properties)));
					return elements;
				}
				
				private static Function<Object, String> func(String langKey, ConfigurationOreProperties properties)
				{
					return new Function<Object, String>()
					{
						@Override
						public String apply(Object obj)
						{
							String name = I18n.format(properties.getOreMaterial().getLanguageKey() + ".name");
							return I18n.format(langKey, properties.getOreMaterial().getLanguageKey().equals(name) ? properties.handler.name : name);
						}
					};
				}
			}
		}
	}
}
