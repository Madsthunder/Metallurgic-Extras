package continuum.essentials.config;

import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.base.Functions;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.IArrayEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ConfigElement<O> implements IConfigElement
{
	private final IConfigValue<O> value;
	private final ConfigGuiType type;
	private final Function<? super ConfigElement<O>, String> comment;
	
	public ConfigElement(IConfigValue<O> value, ConfigGuiType type, String comment)
	{
		this(value, type, 
				new Function<Object, String>()
				{
					@Override
					public String apply(Object input)
					{
						return comment == null ? null : I18n.format(comment);
					}
				});
	}
	
	public ConfigElement(IConfigValue<O> value, ConfigGuiType type, Function<? super ConfigElement<O>, String> comment)
	{
		this.value = value;
		this.type = type;
		this.comment = comment;
	}

	@Override
	public boolean isProperty()
	{
		return true;
	}

	@Override
	public Class<? extends IConfigEntry> getConfigEntryClass()
	{
		return null;
	}

	@Override
	public Class<? extends IArrayEntry> getArrayEntryClass()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return this.value.getName();
	}

	@Override
	public String getQualifiedName()
	{
		return this.value.getName();
	}

	@Override
	public String getLanguageKey()
	{
		return this.value.getLangKey();
	}

	@Override
	public String getComment()
	{
		return comment.apply(this);
	}

	@Override
	public List<IConfigElement> getChildElements()
	{
		return null;
	}

	@Override
	public ConfigGuiType getType()
	{
		return this.type;
	}

	@Override
	public boolean isList()
	{
		return false;
	}

	@Override
	public boolean isListLengthFixed()
	{
		return true;
	}

	@Override
	public int getMaxListLength()
	{
		return -1;
	}

	@Override
	public boolean isDefault()
	{
		return this.value.getCurrentValue() == this.value.getDefaultValue();
	}

	@Override
	public Object getDefault()
	{
		return this.value.getDefaultValue();
	}

	@Override
	public Object[] getDefaults()
	{
		return null;
	}

	@Override
	public void setToDefault()
	{
		this.value.setCurrentValue(this.value.getDefaultValue());
	}

	@Override
	public boolean requiresWorldRestart()
	{
		return this.value.requiresWorldRestart();
	}

	@Override
	public boolean showInGui()
	{
		return true;
	}

	@Override
	public boolean requiresMcRestart()
	{
		return this.value.requiresMCRestart();
	}

	@Override
	public Object get()
	{
		return this.value.getCurrentValue();
	}

	@Override
	public Object[] getList()
	{
		return null;
	}

	@Override
	public void set(Object value)
	{
		this.value.setCurrentValue((O)value);
	}

	@Override
	public void set(Object[] aVal)
	{
		
	}

	@Override
	public String[] getValidValues()
	{
		return null;
	}

	@Override
	public Object getMinValue()
	{
		return this.value.getMinValue();
	}

	@Override
	public Object getMaxValue()
	{
		return this.value.getMaxValue();
	}

	@Override
	public Pattern getValidationPattern()
	{
		return null;
	}
	
	public static Function<IConfigElement, String> getLangKeyNameFunction(String commentLangKey)
	{
		return new Function<IConfigElement, String>()
				{
					@Override
					public String apply(IConfigElement element)
					{
						String name = I18n.format(element.getLanguageKey());
						return I18n.format(commentLangKey, element.getLanguageKey().equals(name) ? element.getName() : name);
					}
				};
	}
}
