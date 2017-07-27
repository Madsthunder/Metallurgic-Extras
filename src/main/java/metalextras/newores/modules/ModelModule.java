package metalextras.newores.modules;

import com.google.gson.JsonObject;
import api.metalextras.ModelType;
import metalextras.newores.NewOreType;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class ModelModule extends OreModule<NewOreType, ModelModule>
{
    protected boolean three_dimensional = false;
    protected ResourceLocation texture = new ResourceLocation("missingno");
    protected ModelType model_type = ModelType.IRON;
    
    public ModelModule(String path, JsonObject json, boolean parse)
    {
    	super(path, NewOreType.class, ModelModule.class, json);
    	if(parse)
    	{
            this.three_dimensional = JsonUtils.getBoolean(json, "3d", false);
            this.texture = new ResourceLocation(JsonUtils.getString(json, "texture", "missingno"));
            this.model_type = ModelType.byName(JsonUtils.getString(json, "model_type", "IRON"));
    	}
    }
    
    public final boolean get3d()
    {
        return false;
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