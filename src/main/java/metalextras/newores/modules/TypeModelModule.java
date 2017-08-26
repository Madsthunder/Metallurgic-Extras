package metalextras.newores.modules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import api.metalextras.ModelType;
import metalextras.newores.NewOreType;
import metalextras.newores.VariableManager;
import net.minecraft.util.ResourceLocation;

public class TypeModelModule extends OreModule<NewOreType, TypeModelModule>
{
	protected boolean three_dimensional = false;
	protected ResourceLocation texture = new ResourceLocation("missingno");
	protected ModelType model_type = ModelType.IRON;

	public TypeModelModule(String path, JsonElement json, boolean parse)
	{
		super(path, NewOreType.class, TypeModelModule.class, json);
		if(parse)
		{
			JsonObject model_object = json.getAsJsonObject();
			this.three_dimensional = VariableManager.getBooleanConst(TypeModelModule.class, path, json, "3d", this.three_dimensional);
			this.texture = new ResourceLocation(VariableManager.getStringConst(TypeModelModule.class, path, json, "texture", this.texture.toString()));
			this.model_type = VariableManager.constant(TypeModelModule.class, ModelType.class, path, json, "model_type", (e) -> ModelType.byName(e.getAsString()), this.model_type, VariableManager.JSON_STRING_NAME);
		}
	}

	public final boolean get3d()
	{
		return this.three_dimensional;
	}

	public final ResourceLocation getTexture()
	{
		return this.texture;
	}

	public final ModelType getModelType()
	{
		return this.model_type;
	}
}