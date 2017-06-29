package continuum.essentials.config;

import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonHooks
{
	public static String getJson(String name, JsonElement element, int tabs)
	{
		if(element == null || element.isJsonNull())
		{
			return getTab(tabs) + (name == null ? "" : ("\"" + name + "\": ") + "null");
		}
		else if(element.isJsonArray())
		{
			return getJson(name, element.getAsJsonArray(), tabs);
		}
		else if(element.isJsonPrimitive())
		{
			return getJson(name, element.getAsJsonPrimitive(), tabs);
		}
		else if(element.isJsonObject())
			return getJson(name, element.getAsJsonObject(), tabs);
		return "";
	}
	
	public static String getJson(String name, JsonObject object, int tabs)
	{
		String tab = getTab(tabs);
		StringBuilder builder = new StringBuilder();
		if(name != null)
			builder.append(tab + "\"" + name + "\":" + System.lineSeparator());
		builder.append(tab + "{" + System.lineSeparator());
		int i = 0;
		Set<Entry<String, JsonElement>> entries = object.entrySet();
		for(Entry<String, JsonElement> entry : entries)
		{
			builder.append(getJson(entry.getKey(), entry.getValue(), tabs + 1) + (i + 1 < entries.size() ? "," : "") + System.lineSeparator());
			i++;
		}
		return builder.append(tab + "}").toString();
	}
	
	public static String getJson(String name, JsonArray array, int tabs)
	{
		StringBuilder builder = new StringBuilder();
		String tab = getTab(tabs);
		if(name != null)
			builder.append(tab + "\"" + name + "\":");
		boolean preTab = array.size() > 0 && (array.get(0).isJsonObject() || array.get(0).isJsonArray());
		if(preTab)
			builder.append(System.lineSeparator() + tab + "[" + System.lineSeparator());
		else
			builder.append(" [ ");
		for(int i = 0; i < array.size(); i++)
		{
			JsonElement element = array.get(i);
			builder.append(getJson(null, element, preTab ? tabs + 1 : 0));
			if((element.isJsonObject() || element.isJsonArray()) || (i + 1 < array.size() && (array.get(i + 1).isJsonObject() || array.get(i + 1).isJsonArray())))
			{
				builder.append("," + System.lineSeparator());
			}
			else if(i + 1 < array.size())
				builder.append(", ");
			else if(element.isJsonObject() || element.isJsonArray())
				builder.append(System.lineSeparator());
		}
		builder.append(preTab ? (tab + "]") : " ]");
		return builder.toString();
	}
	
	public static String getJson(String name, JsonPrimitive primitive, int tabs)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getTab(tabs) + (name == null ? "" : ("\"" + name + "\": ")));
		if(primitive.isBoolean())
			builder.append(primitive.getAsBoolean());
		else if(primitive.isNumber())
			builder.append(primitive.getAsNumber());
		else if(primitive.isString())
			builder.append("\"" + primitive.getAsString() + "\"");
		else
			builder.append("\"\"");
		return builder.toString();
	}
	
	public static String getTab(int tabs)
	{
		StringBuilder tab = new StringBuilder();
		for(int t = 0; t < tabs; t++)
			tab.append("\t");
		return tab.toString();
	}
}
