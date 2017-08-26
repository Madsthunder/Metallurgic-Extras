package metalextras.newores;

import java.util.Map;
import java.util.Optional;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import metalextras.newores.modules.MaterialBlockModule;
import metalextras.newores.modules.MaterialModelsModule;
import metalextras.newores.modules.OreModule;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class NewOreMaterial extends OreModule<NewOreMaterial, NewOreMaterial> implements IForgeRegistryEntry<NewOreMaterial>
{
	private ResourceLocation registry_name;
	private MaterialBlockModule block_module;
	
	public NewOreMaterial(String path, JsonElement json)
	{
		super(path, NewOreMaterial.class, NewOreMaterial.class, json);
	}
	
	public MaterialBlockModule getBlockModule()
	{
		return this.block_module;
	}
	
	@Override
	public Map<Class<?>, OreModule<NewOreMaterial, ?>> gatherDefaultChildren(String path, JsonObject json)
	{
		Map<Class<?>, OreModule<NewOreMaterial, ?>> children = Maps.newHashMap();
		children.put(MaterialBlockModule.class, VariableManager.newModule(path, MaterialBlockModule.class, json));
		children.put(MaterialModelsModule.class, VariableManager.newModule(path, MaterialModelsModule.class, json));
		return children;
	}

	@Override
	public final Class<NewOreMaterial> getRegistryType()
	{
		return NewOreMaterial.class;
	}

	public final NewOreMaterial setRegistryName(String modid, String name)
	{
		return this.setRegistryName(new ResourceLocation(modid, name));
	}

	public final NewOreMaterial setRegistryName(String name)
	{
		return this.setRegistryName(new ResourceLocation(String.format("%s%s", name.lastIndexOf(":") == -1 ? String.format("%s:", Loader.instance().activeModContainer().getModId()) : "", name)));
	}

	@Override
	public final NewOreMaterial setRegistryName(ResourceLocation name)
	{
		if(this.registry_name != null)
			throw new IllegalStateException(String.format("Attempted to set registry name with existing registry name! New: %s Old: %s", name, this.registry_name));
		Optional<ModContainer> optional = Optional.ofNullable(Loader.instance().activeModContainer()).filter((mod) -> !(mod instanceof InjectedModContainer && ((InjectedModContainer)mod).wrappedContainer instanceof FMLContainer));
		String modid = optional.isPresent() ? optional.get().getModId() : "minecraft";
		if(!modid.equals(name.getResourceDomain()))
			FMLLog.bigWarning("Dangerous alternative prefix `%s` for name `%s`, expected `%s` invalid registry invocation/invalid name?", name.getResourceDomain(), name, modid);
		this.registry_name = name;
		return this;
	}

	@Override
	public final ResourceLocation getRegistryName()
	{
		return this.registry_name;
	}
}
