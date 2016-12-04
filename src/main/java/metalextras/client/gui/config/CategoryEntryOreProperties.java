package metalextras.client.gui.config;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import continuum.essentials.config.ConfigElement;
import metalextras.ores.properties.ConfigurationOreProperties;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

public class CategoryEntryOreProperties extends CategoryEntry
{
	public CategoryEntryOreProperties(GuiConfig config, GuiConfigEntries entries, IConfigElement element)
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
