package continuum.core.client;

import continuum.essentials.events.ResourceManagerReloadEvent;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.common.MinecraftForge;

public class ResourceManagerReloadListener implements IResourceManagerReloadListener
{
	@Override
	public void onResourceManagerReload(IResourceManager manager)
	{
		MinecraftForge.EVENT_BUS.post(new ResourceManagerReloadEvent(manager));
	}
}
