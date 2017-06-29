package continuum.essentials.events;

import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ResourceManagerReloadEvent extends Event
{
	private final IResourceManager manager;
	
	public ResourceManagerReloadEvent(IResourceManager manager)
	{
		this.manager = manager;
	}
	
	public IResourceManager getResourceManager()
	{
		return this.manager;
	}
}
