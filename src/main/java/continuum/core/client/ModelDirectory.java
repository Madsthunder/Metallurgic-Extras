package continuum.core.client;

import java.lang.reflect.Method;

import com.google.common.collect.Lists;

import continuum.core.mod.CTCore_EH;
import continuum.core.mod.CTCore_OH;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class ModelDirectory implements ICustomModelLoader
{
	private static final Method loadModel;
	private IResourceManager manager;
	
	@Override
	public void onResourceManagerReload(IResourceManager manager)
	{
		this.manager = manager;
	}
	
	@Override
	public boolean accepts(ResourceLocation location)
	{
		return CTCore_OH.models.containsKey(location);
	}
	
	@Override
	public IModel loadModel(ResourceLocation location)
	{
		ModelBlock model = null;
		try
		{
			model = loadModel == null ? null : (ModelBlock)loadModel.invoke(CTCore_EH.getModelLoader(), location);
		}
		catch(Exception e)
		{
			
		}
		return CTCore_OH.models.get(location).apply(model);
	}
	static
	{
		Method method = null;
		try
		{
			method = ModelBakery.class.getDeclaredMethod("loadModel", ResourceLocation.class);
			method.setAccessible(true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			loadModel = method;
		}
	}
}
