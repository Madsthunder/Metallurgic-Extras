package api.metalextras;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.util.EnumHelper;

public enum OreTypeDictionary
{
	ROCKY,
	DIRTY,
	SANDY,
	LOOSE,
	COMPACT,
	DENSE,
	UNBREAKABLE,
	COLD,
	HOT,
	WET,
	DRY;
	
	public static OreTypeDictionary byName(String name)
	{
		name = name.toUpperCase();
		for(OreTypeDictionary dictionary : OreTypeDictionary.values())
			if(dictionary.name().equals(name))
				return dictionary;
		return EnumHelper.addEnum(OreTypeDictionary.class, name, new Class[0]);
	}
	
	public static OreTypeDictionary byDimension(DimensionType dimension)
	{
		return byName(dimension.name());
	}
	
	public static Predicate<Collection<OreTypeDictionary>> notAny(OreTypeDictionary... entries)
	{
		return new Predicate<Collection<OreTypeDictionary>>()
		{
			@Override
			public boolean apply(Collection<OreTypeDictionary> entries1)
			{
				for(OreTypeDictionary entry : entries)
					if(entries1.contains(entry))
						return false;
				return true;
			}
		};
	}
	
	public static Predicate<Collection<OreTypeDictionary>> all(OreTypeDictionary... entries)
	{
		return new Predicate<Collection<OreTypeDictionary>>()
		{
			@Override
			public boolean apply(Collection<OreTypeDictionary> entries1)
			{
				for(OreTypeDictionary entry : entries)
					if(!entries1.contains(entry))
						return false;
				return true;
			}
		};
	}
	
	public static Predicate<OreType> byBiomeDictionaryType(OreTypeDictionary type)
	{
		return byBiomeDictionaryType(Predicates.equalTo(type));
	}
	
	public static Predicate<OreType> byBiomeDictionaryType(Predicate<OreTypeDictionary> predicate)
	{
		return byBiomeDictionaryTypes(new Predicate<Iterable<OreTypeDictionary>>()
		{
			@Override
			public boolean apply(Iterable<OreTypeDictionary> types)
			{
				return Iterables.any(types, predicate);
			}
		});
	}
	
	public static Predicate<OreType> byBiomeDictionaryTypes(Iterable<OreTypeDictionary> types)
	{
		return byBiomeDictionaryTypes(new Predicate<Iterable<OreTypeDictionary>>()
		{
			@Override
			public boolean apply(Iterable<OreTypeDictionary> types1)
			{
				return Iterables.any(types1, new Predicate<OreTypeDictionary>()
				{
					@Override
					public boolean apply(OreTypeDictionary type)
					{
						return Iterables.contains(types, type);
					}
				});
			}
		});
	}
	
	public static Predicate<OreType> byBiomeDictionaryTypes(Predicate<Iterable<OreTypeDictionary>> predicate)
	{
		return new Predicate<OreType>()
		{
			@Override
			public boolean apply(OreType type)
			{
				return predicate.apply(type.getOreTypeDictionaryList());
			}
		};
	}
}
