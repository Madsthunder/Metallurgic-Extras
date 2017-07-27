package metalextras.newores.modules;

import java.util.Optional;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.bind.TypeAdapters;
import metalextras.MetalExtras;
import metalextras.newores.NewOreType;
import metalextras.newores.VariableManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SmeltingModule extends OreModule<NewOreType, SmeltingModule>
{
	protected float xp;
	protected Item item = Items.AIR;
	protected int metadata = 0;
	protected int count = 1;
	protected NBTTagCompound nbt;

	public SmeltingModule(String path, JsonElement json, boolean parse)
	{
		super(path, NewOreType.class, SmeltingModule.class, json);
		if(parse)
			if(json.isJsonPrimitive() && json.getAsJsonPrimitive().isString())
			{
				// TODO Variables
			}
			else if(json.isJsonObject())
			{
				JsonObject smelting_object = json.getAsJsonObject();
				JsonElement xp_element = smelting_object.get("xp");
				if(xp_element == null)
					this.count = 1;
				else if(xp_element.isJsonPrimitive())
				{
					JsonPrimitive count_primitive = xp_element.getAsJsonPrimitive();
					String count_string;
					if(count_primitive.isString() && (count_string = count_primitive.getAsString()).startsWith("#"))
						this.xp = VariableManager.warnIfAbsent(VariableManager.getNumberConstant(new ResourceLocation(count_string.substring(1))), 0, "The string at %s/xp references a non-existent constant (%s). Defaulting to 0.", path, count_string).floatValue();
					else if(count_primitive.isNumber())
						this.xp = count_primitive.getAsFloat();
					else
						MetalExtras.LOGGER.warn(String.format("The element at %s/xp is expected to be a #constant, JsonPrimitive Number, or a JsonObject. Defaulting to 0.", path));
				}
				else
				{
					// TODO throw exception
				}
				JsonElement item_element = smelting_object.get("item");
				if(item_element == null)
					this.item = Items.AIR;
				else if(item_element.isJsonPrimitive() && item_element.getAsJsonPrimitive().isString())
				{
					String result_item_string = item_element.getAsString();
					if(result_item_string.startsWith("#"))
						this.item = VariableManager.warnIfAbsent(VariableManager.getConstant(new ResourceLocation(result_item_string.substring(1)), Item.class), Items.AIR, "The string at %s/item references a non-existent constant (%s)", path, result_item_string);
					else
						this.item = VariableManager.warnIfAbsent(Optional.ofNullable(ForgeRegistries.ITEMS.getValue(new ResourceLocation(result_item_string))), Items.AIR, "The string at %s/item references an unregistered item (%s). Defaulting to air.", path, result_item_string);
				}
				else
				{
					// TODO throw exception
				}
				JsonElement count_element = smelting_object.get("count");
				if(count_element == null)
					this.count = 1;
				else if(count_element.isJsonPrimitive())
				{
					JsonPrimitive count_primitive = count_element.getAsJsonPrimitive();
					String count_string;
					if(count_primitive.isString() && (count_string = count_primitive.getAsString()).startsWith("#"))
						this.count = VariableManager.warnIfAbsent(VariableManager.getNumberConstant(new ResourceLocation(count_string.substring(1))), 1, "The string at %s/count references a non-existent constant (%s)", path, count_string).intValue();
					else if(count_primitive.isNumber())
						this.count = count_primitive.getAsInt();
					else
						MetalExtras.LOGGER.warn(String.format("The element at %s/count is expected to be a #constant, JsonPrimitive Number, or a JsonObject. Defaulting to 1.", path));
				}
				else
				{
					// TODO throw exception
				}
				JsonElement nbt_element = smelting_object.get("nbt");
				if(nbt_element == null)
					this.nbt = new NBTTagCompound();
				else if(nbt_element.isJsonPrimitive() && nbt_element.getAsJsonPrimitive().isString())
				{
					String nbt_string = nbt_element.getAsString();
					if(nbt_string.startsWith("#"))
						this.nbt = VariableManager.warnIfAbsent(VariableManager.getConstant(new ResourceLocation(nbt_string.substring(1)), NBTTagCompound.class), new NBTTagCompound(), "The string at %s/count references a non-existent constant (%s)", path, nbt_string);
					else
						try
						{
							this.nbt = JsonToNBT.getTagFromJson(nbt_string);
						}
						catch(NBTException exception)
						{
							MetalExtras.LOGGER.warn(String.format("There was an exception while trying to convert the string at %s/nbt into an NBTTagCompound. Defaulting to an empty NBTTagCompound.", path));
							this.nbt = new NBTTagCompound();
						}
				}
				else if(nbt_element.isJsonObject())
					try
					{
						this.nbt = JsonToNBT.getTagFromJson(TypeAdapters.JSON_ELEMENT.toJson(nbt_element));
					}
					catch(NBTException exception)
					{
						MetalExtras.LOGGER.warn(String.format("There was an exception while trying to convert the string at %s/nbt into an NBTTagCompound. Defaulting to an empty NBTTagCompound.", path));
						this.nbt = new NBTTagCompound();
					}
				else
					MetalExtras.LOGGER.warn(String.format("The element at %s/nbt is expected to be a #constant, JsonPrimitive String, or a JsonObject. Defaulting to an empty NBTTagCompound.", path));
			}
			else
			{
				// TODO throw exception
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