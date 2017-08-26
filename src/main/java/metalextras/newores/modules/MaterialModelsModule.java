package metalextras.newores.modules;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import api.metalextras.ModelType;
import metalextras.newores.NewOreMaterial;
import metalextras.newores.VariableManager;
import net.minecraft.util.ResourceLocation;

public class MaterialModelsModule extends OreModule<NewOreMaterial, MaterialModelsModule>
{
	protected ResourceLocation default_model = new ResourceLocation("missingno");
	protected TreeMap<ModelType, ResourceLocation> models = Maps.newTreeMap(ModelType::compare);

	public MaterialModelsModule(String path, JsonElement json, boolean parse)
	{
		super(path, NewOreMaterial.class, MaterialModelsModule.class, json);
		if(parse)
		{
			this.models.putAll(VariableManager.parse(this.module_type, Void.class, Map.class, path, json, (e) ->
			{
				if(e.isJsonObject())
				{
					for(Entry<String, JsonElement> models_entry : e.getAsJsonObject().entrySet())
						//TODO check
						this.models.put(ModelType.byName(models_entry.getKey()), new ResourceLocation(models_entry.getValue().getAsString()));
				}
				return null;
			}, null, this.models));
			JsonObject models_object = json.getAsJsonObject();
			if(this.models.isEmpty())
				this.models.put(ModelType.DEFAULT, new ResourceLocation("missingno"));
			this.default_model = this.models.firstEntry().getValue();
			if(this.models.firstKey() == ModelType.DEFAULT)
				this.models.pollFirstEntry();
		}
	}

	public final ResourceLocation getModel(ModelType type)
	{
		return this.models.getOrDefault(type, this.default_model);
	}
}