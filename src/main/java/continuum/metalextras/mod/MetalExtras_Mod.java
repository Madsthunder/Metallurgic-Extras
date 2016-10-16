package continuum.metalextras.mod;

import continuum.essentials.mod.CTMod;
import continuum.metalextras.loaders.BlockLoader;
import continuum.metalextras.loaders.ClientLoader;
import continuum.metalextras.loaders.ConfigLoader;
import continuum.metalextras.loaders.ItemLoader;
import continuum.metalextras.loaders.RecipeLoader;
import continuum.metalextras.loaders.UtilityLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "metalextras", name = "Metallurgic Extras", version = "1.2.2")
public class MetalExtras_Mod extends CTMod<MetalExtras_OH, MetalExtras_EH>
{
	public MetalExtras_Mod()
	{
		super(MetalExtras_OH.getHolder(), new ConfigLoader(), new BlockLoader(), new ItemLoader(), new UtilityLoader(), new RecipeLoader(), new ClientLoader());
		MetalExtras_EH.objectHolder = this.getObjectHolder();
		MinecraftForge.ORE_GEN_BUS.register(this.getEventHandler());
	}
	
	@Mod.EventHandler
	public void construction(FMLConstructionEvent event)
	{
		super.construction(event);
	}
	
	@Mod.EventHandler
	public void pre(FMLPreInitializationEvent event)
	{
		super.pre(event);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
	}
	
	@Mod.EventHandler
	public void post(FMLPostInitializationEvent event)
	{
		super.post(event);
	}
}
