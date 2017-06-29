package continuum.essentials.config;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ConfigHandler
{
	private final File folder;
	private final List<IConfigSubHandler> subHandlers = Lists.newArrayList();
	
	public ConfigHandler(String folder)
	{
		this(new File(folder));
	}
	
	public ConfigHandler(File folder)
	{
		this.folder = folder.getAbsoluteFile();
	}
	
	public final void addConfigSubHandler(IConfigSubHandler subHandler)
	{
		this.subHandlers.add(subHandler);
	}
	
	public void refreshAll()
	{
		List<File> files = Lists.newArrayList();
		for(IConfigSubHandler handler : subHandlers)
			files.add(handler.getFile(folder));
		for(File file : files)
			refresh(file);
	}
	
	public void refresh(File directory)
	{
		JsonObject object = getJsonObject(directory);
		read(directory, object);
		write(directory, object);
	}
	
	public JsonObject getJsonObject(File directory)
	{
		JsonObject object = new JsonObject();
		if(!subHandlers.isEmpty())
		{
			folder.mkdirs();
			try
			{
				directory.createNewFile();
			}
			catch(IOException exception)
			{
				exception.printStackTrace();
			}
			String json = "";
			try
			{
				json = FileUtils.readFileToString(directory);
			}
			catch(IOException e)
			{
				
			}
			try
			{
				object = new JsonParser().parse(json).getAsJsonObject();
			}
			catch(Exception e)
			{
				if(!json.isEmpty())
					e.printStackTrace();
			}
		}
		return object;
	}
	
	public void read(File directory, JsonObject object)
	{
		for(IConfigSubHandler handler : this.subHandlers)
		{
			if(handler.getFile(this.folder).equals(directory))
			{
				JsonObject object1 = new JsonObject();
				if(object.has(handler.getName()))
				{
					JsonElement element = object.get(handler.getName());
					if(element.isJsonObject())
						handler.read(object1 = element.getAsJsonObject());
				}
				handler.write(object1);
				object.add(handler.getName(), object1);
			}
		}
	}
	
	public void readAll()
	{
		List<File> files = Lists.newArrayList();
		for(IConfigSubHandler handler : subHandlers)
			files.add(handler.getFile(folder));
		for(File file : files)
			read(file, getJsonObject(file));
	}
	
	public void write(File directory, JsonObject object)
	{
		try
		{
			FileUtils.write(directory, JsonHooks.getJson(null, object, 0));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void writeAll()
	{
		List<File> files = Lists.newArrayList();
		for(IConfigSubHandler handler : this.subHandlers)
			files.add(handler.getFile(this.folder));
		for(File file : files)
			write(file, getJsonObject(file));
	}
}
