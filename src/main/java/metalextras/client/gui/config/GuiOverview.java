package metalextras.client.gui.config;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiOverview extends GuiConfig
{
	public GuiOverview(GuiScreen parent)
	{
		super(parent, getConfigElements(), "metalextras", false, false, "Metallurgic Extras");
	}
	
	private static List<IConfigElement> getConfigElements()
	{
		return Lists.newArrayList(new DummyCategoryElement("ore_generation", "config.metalextras:category.ore_generation.name", CategoryEntryOreGen.class));
	}
	
	public static class Factory implements IModGuiFactory
	{
		@Override
		public void initialize(Minecraft minecraftInstance)
		{
			
		}
		
		@Override
		public Class<? extends GuiScreen> mainConfigGuiClass()
		{
			return GuiOverview.class;
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
	}
}
