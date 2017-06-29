package continuum.essentials.config;

import java.io.File;

import com.google.gson.JsonObject;

public interface IConfigSubHandler
{
	public File getFile(File origin);
	public String getName();
	public void read(JsonObject object);
	public void write(JsonObject object);
}
