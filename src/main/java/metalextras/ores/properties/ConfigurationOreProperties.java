package metalextras.ores.properties;

import java.util.Collection;
import java.util.Map;
import java.util.Random;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import api.metalextras.Characteristic;
import api.metalextras.OreMaterial;
import api.metalextras.OreProperties;
import api.metalextras.OreType;
import api.metalextras.OreUtils;
import metalextras.MetalExtras;
import metalextras.config.OreConfigHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class ConfigurationOreProperties extends OreProperties
{
	public static final Map<String, ConfigurationOreProperties> cfgs = Maps.newHashMap();
	
	public final OreMaterial material;
	public final WorldGenerator generator;
	public final OreConfigHandler handler;
	
	public ConfigurationOreProperties(OreMaterial material, String name, boolean spawnEnabled, int spawnTries, int minHeight, int maxHeight, float minTemperature, float maxTemperature, int veinSize, Predicate<Collection<Characteristic>> defaultPredicates)
	{
		this.material = material;
		this.generator = new WorldGenerator()
		{
			@Override
			public boolean generate(World world, Random random, BlockPos pos)
			{
				return OreUtils.generateOres(world, pos, random, ConfigurationOreProperties.this.handler.veinSize.getCurrentValue(), ConfigurationOreProperties.this);
			}
		};
		this.handler = new OreConfigHandler(name, spawnEnabled, spawnTries, minHeight, maxHeight, minTemperature, maxTemperature, veinSize, defaultPredicates);
		MetalExtras.CONFIGURATION_HANDLER.addConfigSubHandler(this.handler);
		cfgs.put(name, this);
	}
	
	@Override
	public OreMaterial getOreMaterial()
	{
		return this.material;
	}
	
	@Override
	public boolean getSpawnEnabled()
	{
		return this.handler.spawnEnabled.getCurrentValue();
	}
	
	@Override
	public int getSpawnTriesPerChunk(World world, Random random)
	{
		return this.handler.spawnTries.getCurrentValue();
	}
	
	@Override
	public BlockPos getRandomSpawnPos(World world, Random random)
	{
		int minHeight = this.handler.minHeight.getCurrentValue();
		BlockPos pos = new BlockPos(random.nextInt(16), random.nextInt(this.handler.maxHeight.getCurrentValue() - minHeight) + minHeight, random.nextInt(16));
		return pos;
	}
	
	@Override
	public boolean canSpawnAtCoords(World world, BlockPos pos)
	{
		float temperature = world.getBiome(pos).getTemperature();
		return this.handler.minTemperature.getCurrentValue() <= temperature && this.handler.maxTemperature.getCurrentValue() >= temperature;
	}
	
	@Override
	public WorldGenerator getWorldGenerator(World world, BlockPos pos)
	{
		return this.generator;
	}
	
	@Override
	public boolean isValid(OreType type)
	{
		return this.handler.whitelist.contains(type);
	}
	
	public static Function<OreMaterial, OreProperties> func(String name, boolean spawnEnabled, int spawnTries, int minHeight, int maxHeight, float minTemperature, float maxTemperature, int veinSize, Predicate<Collection<Characteristic>> defaultPredicate)
	{
		return new Function<OreMaterial, OreProperties>()
		{
			@Override
			public OreProperties apply(OreMaterial material)
			{
				return new ConfigurationOreProperties(material, name, spawnEnabled, spawnTries, minHeight, maxHeight, minTemperature, maxTemperature, veinSize, defaultPredicate);
			}
		};
	}
}