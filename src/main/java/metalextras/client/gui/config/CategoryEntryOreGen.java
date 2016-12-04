package metalextras.client.gui.config;

import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import metalextras.ores.properties.ConfigurationOreProperties;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

public class CategoryEntryOreGen extends CategoryEntry
{
	public CategoryEntryOreGen(GuiConfig config, GuiConfigEntries entries, IConfigElement element)
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
		for(Entry<String, ConfigurationOreProperties> entry : ConfigurationOreProperties.cfgs.entrySet())
			elements.add(new DummyCategoryElement(entry.getKey(), entry.getValue().getOreMaterial().getLanguageKey() + ".name", CategoryEntryOreProperties.class));
		return elements;
	}
	
}
