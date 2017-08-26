package metalextras.newores.modules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import metalextras.newores.NewOreType;
import metalextras.newores.VariableManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SmeltingModule extends OreModule<NewOreType, SmeltingModule>
{
	protected float xp = 0;
	protected Item item = Items.AIR;
	protected int metadata = 0;
	protected int count = 1;
	protected NBTTagCompound nbt = new NBTTagCompound();

	public SmeltingModule(String path, JsonElement json, boolean parse)
	{
		super(path, NewOreType.class, SmeltingModule.class, json);
		if(parse)
		{
			JsonObject smelting_object = json.getAsJsonObject();
			this.xp = VariableManager.getFloatConst(SmeltingModule.class, path, json, "xp", this.xp);
			this.item = VariableManager.getRegistryEntryConst(SmeltingModule.class, ForgeRegistries.ITEMS, path, json, "item", this.item);
			this.metadata = VariableManager.getIntegerConst(SmeltingModule.class, path, json, "data", this.metadata);
			this.count = VariableManager.getIntegerConst(SmeltingModule.class, path, json, "count", this.count);
			this.nbt = VariableManager.constantOrParse(SmeltingModule.class, Void.class, NBTTagCompound.class, path.concat("/nbt"), smelting_object.get("nbt"), (nbt_object) -> new ResourceLocation(JsonUtils.getStringOr("type", nbt_object, "minecraft:item_nbt")).toString(), null, this.nbt);
		}
	}

	public float getXp()
	{
		return this.xp;
	}

	public Item getItem()
	{
		return this.item;
	}

	public int getMetadata()
	{
		return this.metadata;
	}

	public int getCount()
	{
		return this.count;
	}

	public NBTTagCompound getNbt()
	{
		return this.nbt;
	}
}