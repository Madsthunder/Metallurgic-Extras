package continuum.core.mod;

import metalextras.MetalExtras;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = MetalExtras.MODID)
public class CTCore_EH
{
	@SideOnly(Side.CLIENT)
	private static ModelLoader modelLoader;
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onModelBake(ModelBakeEvent event)
	{
		modelLoader = event.getModelLoader();
	}
	
	@SideOnly(Side.CLIENT)
	public static ModelLoader getModelLoader()
	{
		return modelLoader;
	}
}
