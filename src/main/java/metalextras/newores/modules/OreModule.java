package metalextras.newores.modules;

import java.util.Map;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import metalextras.newores.VariableManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.GenericEvent;

public abstract class OreModule<P extends OreModule<?, P>, S extends OreModule<P, S>>
{
	private final Map<Class<?>, OreModule<S, ?>> children = Maps.newHashMap();
	public final Class<P> parent_type;
	public final Class<S> module_type;
	private P parent;

	public OreModule(String path, Class<P> parent_type, Class<S> module_type, JsonElement json)
	{
		this.parent_type = parent_type;
		this.module_type = module_type;
		if(this.parent_type == this.module_type)
		{
			this.setParent((P)this);
			if(json.isJsonObject())
				GatherChildrenEvent.gatherChildren(path, this, json.getAsJsonObject());
		}
	}

	protected final void setParent(P parent)
	{
		this.parent = parent;
	}

	public final P getParentModule()
	{
		return this.parent;
	}

	public final <V extends OreModule<S, V>> V getChildModule(Class<V> type)
	{
		return (V)this.children.get(type);
	}

	public Map<Class<?>, OreModule<S, ?>> gatherDefaultChildren(String path, JsonObject json)
	{
		return Maps.newHashMap();
	}

	public static class GatherChildrenEvent<S extends OreModule<?, S>> extends GenericEvent<S>
	{
		private final String path;
		private final JsonObject json;
		private final Map<Class<?>, OreModule<S, ?>> children = Maps.newHashMap();

		public GatherChildrenEvent(String path, Class<S> type, JsonObject json)
		{
			super(type);
			this.path = path;
			this.json = json;
		}

		public <V extends OreModule<S, V>> void addChild(Class<V> child_type)
		{
			this.children.put(child_type, VariableManager.newModule(this.path, child_type, this.json));
		}

		private static <S extends OreModule<?, S>> void gatherChildren(String path, OreModule<?, S> module, JsonObject json)
		{
			module.children.clear();
			GatherChildrenEvent<S> event = new GatherChildrenEvent<>(path, module.module_type, json);
			event.children.putAll(module.gatherDefaultChildren(path, json));
			MinecraftForge.EVENT_BUS.post(event);
			module.children.putAll(event.children);
			module.children.values().forEach((child) ->
			{
				child.setParent((S)module);
				gatherChildren(String.format("%s/%s", path, VariableManager.getModuleName(child.module_type)), child, json);
			});
		}
	}
}
