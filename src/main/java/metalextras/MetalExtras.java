package metalextras;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import api.metalextras.BlockOre;
import api.metalextras.Characteristic;
import api.metalextras.OreType;
import api.metalextras.OreUtils;
import api.metalextras.SPacketBlockOreLandingParticles;
import api.metalextras.SPacketBlockOreLandingParticles.SendLandingParticlesEvent;
import metalextras.items.ItemEnderHoe;
import metalextras.items.ItemEnderTool;
import metalextras.newores.FilterManager;
import metalextras.newores.NewOreType;
import metalextras.newores.NewOreType.Smelting;
import metalextras.newores.VariableManager;
import metalextras.packets.OreLandingParticleMessageHandler;
import metalextras.world.gen.OreGeneration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeHills;
import net.minecraft.world.biome.BiomeMesa;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@EventBusSubscriber
@Mod(modid = MetalExtras.MODID, name = MetalExtras.NAME, version = MetalExtras.VERSION)
public class MetalExtras
{
	public static final String MODID = "metalextras";
	public static final String NAME = "Metallurgic Extras";
	public static final String VERSION = "3.0.0";
	public static final Logger LOGGER = LogManager.getLogger("Metallurgic Extras");
	
	public static class Proxy
	{
		@SidedProxy(modId = MODID)
		public static Proxy I = null;
		
		public void constr()
		{
		}
		
		public void pre()
		{
			VariableManager.registerConstant(new ResourceLocation("oresapi:id"), () -> OreUtils.getTypesRegistry().nextId());
			FilterManager.register(new ResourceLocation("oresapi:include"), (materials, params) ->
			{
				Predicate<Collection<Characteristic>> filter = Characteristic.all(Iterables.toArray(Iterables.transform(params, (param) -> Characteristic.byName(param)), Characteristic.class));
				Set<OreType> material_set = Sets.newHashSet(Iterables.filter(materials, (material) -> filter.test(material.getCharacteristics())));
				materials.clear();
				materials.addAll(material_set);
			});
			FilterManager.register(new ResourceLocation("oresapi:exclude"), (materials, params) ->
			{
				Predicate<Collection<Characteristic>> filter = Characteristic.notAny(Iterables.toArray(Iterables.transform(params, (param) -> Characteristic.byName(param)), Characteristic.class));
				Set<OreType> material_set = Sets.newHashSet(Iterables.filter(materials, (material) -> filter.test(material.getCharacteristics())));
				materials.clear();
				materials.addAll(material_set);
			});
			VariableManager.registerGenerationPropertiesParser(new ResourceLocation("minecraft:iron"), NewOreType.Generation.Properties.Iron.createParser());
			VariableManager.registerGenerationPropertiesParser(new ResourceLocation("minecraft:lapis"), NewOreType.Generation.Properties.Lapis.createParser());
			VariableManager.registerGenerationPropertiesParser(new ResourceLocation("minecraft:emerald"), NewOreType.Generation.Properties.Emerald.createParser());
			Map<World, Map<String, NewOreType.Generation.Properties>> properties_map = Maps.newHashMap();
			VariableManager.registerConstantGenerationProperties(new ResourceLocation("minecraft:coal_ore"), (generation) -> (world, pos) ->
			{
				NewOreType.Generation.Properties default_properties = new NewOreType.Generation.Properties.Iron(generation, true);
				return Optional.ofNullable(properties_map.computeIfAbsent(world, (world1) -> Maps.newHashMap()).computeIfAbsent("coal_ore", (name) ->
				{
					ChunkGeneratorSettings settings = OreGeneration.getChunkProviderSettings(world);
					return settings == OreGeneration.defaultSettings ? null : new NewOreType.Generation.Properties.Iron(generation, true)
					{
						@Override
						public void init()
						{
							this.tries = settings.coalCount;
							this.min_height = settings.coalMinHeight;
							this.max_height = settings.coalMaxHeight;
							this.size = settings.coalSize;
						}
					};
				})).orElse(default_properties);
			});
			VariableManager.registerConstantGenerationProperties(new ResourceLocation("minecraft:iron_ore"), (generation) ->
			{
				NewOreType.Generation.Properties default_properties = new NewOreType.Generation.Properties.Iron(generation, true);
				return (world, pos) -> Optional.ofNullable(properties_map.computeIfAbsent(world, (world1) -> Maps.newHashMap()).computeIfAbsent("iron_ore", (name) ->
				{
					ChunkGeneratorSettings settings = OreGeneration.getChunkProviderSettings(world);
					return settings == OreGeneration.defaultSettings ? null : new NewOreType.Generation.Properties.Iron(generation, true)
					{
						@Override
						public void init()
						{
							this.tries = settings.ironCount;
							this.min_height = settings.ironMinHeight;
							this.max_height = settings.ironMaxHeight;
							this.size = settings.ironSize;
						}
					};
				})).orElse(default_properties);
			});
			VariableManager.registerConstantGenerationProperties(new ResourceLocation("minecraft:lapis_ore"), (generation) ->
			{
				NewOreType.Generation.Properties default_properties = new NewOreType.Generation.Properties.Lapis(generation, true);
				return (world, pos) -> Optional.ofNullable(properties_map.computeIfAbsent(world, (world1) -> Maps.newHashMap()).computeIfAbsent("lapis_ore", (name) ->
				{
					ChunkGeneratorSettings settings = OreGeneration.getChunkProviderSettings(world);
					return settings == OreGeneration.defaultSettings ? null : new NewOreType.Generation.Properties.Lapis(generation, true)
					{
						@Override
						public void init()
						{
							this.tries = settings.lapisCount;
							this.center_height = settings.lapisCenterHeight;
							this.spread = settings.lapisSpread;
							this.size = settings.lapisSize;
						}
					};
				})).orElse(default_properties);
			});
			VariableManager.registerConstantGenerationProperties(new ResourceLocation("minecraft:gold_ore"), (generation) ->
			{
				NewOreType.Generation.Properties default_properties = new NewOreType.Generation.Properties.Iron(generation, true);
				return (world, pos) -> Optional.ofNullable(properties_map.computeIfAbsent(world, (world1) -> Maps.newHashMap()).computeIfAbsent("gold_ore", (name) ->
				{
					ChunkGeneratorSettings settings = OreGeneration.getChunkProviderSettings(world);
					return settings == OreGeneration.defaultSettings ? null : new NewOreType.Generation.Properties.Iron(generation, true)
					{
						@Override
						public void init()
						{
							this.tries = settings.goldCount;
							this.min_height = settings.goldMinHeight;
							this.max_height = settings.goldMaxHeight;
							this.size = settings.goldSize;
						}
					};
				})).orElse(default_properties);
			});
			VariableManager.registerConstantGenerationProperties(new ResourceLocation("minecraft:redstone_ore"), (generation) ->
			{
				NewOreType.Generation.Properties default_properties = new NewOreType.Generation.Properties.Iron(generation, true);
				return (world, pos) -> Optional.ofNullable(properties_map.computeIfAbsent(world, (world1) -> Maps.newHashMap()).computeIfAbsent("redstone_ore", (name) ->
				{
					ChunkGeneratorSettings settings = OreGeneration.getChunkProviderSettings(world);
					return settings == OreGeneration.defaultSettings ? null : new NewOreType.Generation.Properties.Iron(generation, true)
					{
						@Override
						public void init()
						{
							this.tries = settings.redstoneCount;
							this.min_height = settings.redstoneMinHeight;
							this.max_height = settings.redstoneMaxHeight;
							this.size = settings.redstoneSize;
						}
					};
				})).orElse(default_properties);
			});
			VariableManager.registerConstantGenerationProperties(new ResourceLocation("minecraft:emerald_ore"), (generation) ->
			{
				NewOreType.Generation.Properties default_properties = new NewOreType.Generation.Properties.Emerald(generation, true);
				return (world, pos) -> world.getBiome(pos.add(16, 0, 16)) instanceof BiomeHills ? Optional.ofNullable(properties_map.computeIfAbsent(world, (world1) -> Maps.newHashMap()).computeIfAbsent("emerald_ore", (name) ->
				{
					ChunkGeneratorSettings settings = OreGeneration.getChunkProviderSettings(world);
					return settings == OreGeneration.defaultSettings ? null : new NewOreType.Generation.Properties.Emerald(generation, true)
					{
						@Override
						public void init()
						{
							this.tries_base = 3;
							this.tries_randomizer = 6;
							this.min_height = settings.coalMinHeight;
							this.max_height = settings.coalMaxHeight;
							this.size = settings.coalSize;
						}
					};
				})).orElse(default_properties) : default_properties;
			});
			VariableManager.registerConstantGenerationProperties(new ResourceLocation("minecraft:diamond_ore"), (generation) ->
			{
				NewOreType.Generation.Properties default_properties = new NewOreType.Generation.Properties.Iron(generation, true);
				return (world, pos) -> Optional.ofNullable(properties_map.computeIfAbsent(world, (world1) -> Maps.newHashMap()).computeIfAbsent("diamond_ore", (name) ->
				{
					ChunkGeneratorSettings settings = OreGeneration.getChunkProviderSettings(world);
					return settings == OreGeneration.defaultSettings ? null : new NewOreType.Generation.Properties.Iron(generation, true)
					{
						@Override
						public void init()
						{
							this.tries = settings.diamondCount;
							this.min_height = settings.diamondMinHeight;
							this.max_height = settings.diamondMaxHeight;
							this.size = settings.diamondSize;
						}
					};
				})).orElse(default_properties);
			});
			VariableManager.registerConstantGenerationProperties(new ResourceLocation("minecraft:mesa_gold_ore"), (generation) ->
			{
				NewOreType.Generation.Properties default_properties = new NewOreType.Generation.Properties.Iron(generation, true);
				return (world, pos) -> world.getBiome(pos.add(16, 0, 16)) instanceof BiomeMesa ? Optional.ofNullable(properties_map.computeIfAbsent(world, (world1) -> Maps.newHashMap()).computeIfAbsent("mesa_gold_ore", (name) ->
				{
					ChunkGeneratorSettings settings = OreGeneration.getChunkProviderSettings(world);
					return settings == OreGeneration.defaultSettings ? null : new NewOreType.Generation.Properties.Iron(generation, true)
					{
						@Override
						public void init()
						{
							this.tries = 20;
							this.min_height = 32;
							this.max_height = 80;
							this.size = settings.goldSize;
						}
					};
				})).orElse(default_properties) : default_properties;
			});
			LANDING_PARTICLE_WRAPPER.registerMessage(OreLandingParticleMessageHandler.class, SPacketBlockOreLandingParticles.class, 0, Side.CLIENT);
		}
		
		public void init()
		{
			for(NewOreType type : OreUtils.getTypesRegistry())
			{
				for(String name : type.getOreDictionary())
					OreUtils.addMaterialToOreDictionary(type, name, true);
				Smelting smelting = type.getSmelting();
				NBTTagCompound nbt = smelting.getNbt();
				nbt.setString("id", smelting.getItem().getRegistryName().toString());
				nbt.setByte("Count", (byte)smelting.getCount());
				nbt.setInteger("Damage", smelting.getMetadata());
				ItemStack stack = new ItemStack(nbt);
				if(!stack.isEmpty())
					OreUtils.registerMaterialSmeltingRecipe(type, stack, smelting.getXp());
			}
			OreDictionary.registerOre("blockCopper", MetalExtras_Objects.COPPER_BLOCK);
			OreDictionary.registerOre("blockTin", MetalExtras_Objects.TIN_BLOCK);
			OreDictionary.registerOre("blockAluminum", MetalExtras_Objects.ALUMINUM_BLOCK);
			OreDictionary.registerOre("blockLead", MetalExtras_Objects.LEAD_BLOCK);
			OreDictionary.registerOre("blockSilver", MetalExtras_Objects.SILVER_BLOCK);
			OreDictionary.registerOre("blockEnder", MetalExtras_Objects.ENDER_BLOCK);
			OreDictionary.registerOre("blockSapphire", MetalExtras_Objects.SAPPHIRE_BLOCK);
			OreDictionary.registerOre("blockRuby", MetalExtras_Objects.RUBY_BLOCK);
			OreDictionary.registerOre("ingotCopper", MetalExtras_Objects.COPPER_INGOT);
			OreDictionary.registerOre("ingotTin", MetalExtras_Objects.TIN_INGOT);
			OreDictionary.registerOre("ingotAluminum", MetalExtras_Objects.ALUMINUM_INGOT);
			OreDictionary.registerOre("ingotLead", MetalExtras_Objects.LEAD_INGOT);
			OreDictionary.registerOre("ingotSilver", MetalExtras_Objects.SILVER_INGOT);
			OreDictionary.registerOre("gemEnder", MetalExtras_Objects.ENDER_GEM);
			OreDictionary.registerOre("gemSapphire", MetalExtras_Objects.SAPPHIRE_GEM);
			OreDictionary.registerOre("gemRuby", MetalExtras_Objects.RUBY_GEM);
			OreDictionary.registerOre("nuggetCopper", MetalExtras_Objects.COPPER_NUGGET);
			OreDictionary.registerOre("nuggetTin", MetalExtras_Objects.TIN_NUGGET);
			OreDictionary.registerOre("nuggetAluminum", MetalExtras_Objects.ALUMINUM_NUGGET);
			OreDictionary.registerOre("nuggetLead", MetalExtras_Objects.LEAD_NUGGET);
			OreDictionary.registerOre("nuggetSilver", MetalExtras_Objects.SILVER_NUGGET);
			GameRegistry.registerWorldGenerator(new OreGeneration(), 100);
		}
		
		public void post()
		{
			
		}
		
		@SideOnly(Side.CLIENT)
		public static class ClientProxy extends Proxy
		{
		}
		
		@SideOnly(Side.SERVER)
		public static class ServerProxy extends Proxy
		{
			
		}
	}
	
	public static final Characteristic OTD_NETHER = Characteristic.byDimension(DimensionType.NETHER);
	public static final Characteristic OTD_END = Characteristic.byDimension(DimensionType.THE_END);
	
	@GameRegistry.ItemStackHolder("metalextras:ender_gem")
	public static final ItemStack METALLURGIC_EXTRAS_ITEM = ItemStack.EMPTY;
	
	public static final CreativeTabs METALLURGIC_EXTRAS = new CreativeTabs("metalextras:ores")
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return METALLURGIC_EXTRAS_ITEM;
		}
	};
	
	public static final SimpleNetworkWrapper LANDING_PARTICLE_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel("metalextras:landing_particles");
	
	public MetalExtras()
	{
		MinecraftForge.ORE_GEN_BUS.register(OreGeneration.class);
	}
	
	@Mod.EventHandler
	public void constr(FMLConstructionEvent evnet)
	{
		Proxy.I.constr();
	}
	
	@Mod.EventHandler
	public void pre(FMLPreInitializationEvent event)
	{
		Proxy.I.pre();
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		Proxy.I.init();
	}
	
	@Mod.EventHandler
	public void post(FMLPostInitializationEvent event)
	{
		Proxy.I.post();
	}
	
	private static MinecraftServer server = null;
	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		server = event.getServer();
	}
	
	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent event)
	{
		server = null;
	}
	
	public static boolean isServerActive()
	{
		return server != null;
	}
	
	public static MinecraftServer getActiveServer()
	{
		return server;
	}
	
	@NetworkCheckHandler
	public boolean check(Map<String, String> remoteVersions, Side side)
	{
		return true;
	}
	
	@SubscribeEvent
	public static void onBlockBroken(BlockEvent.BreakEvent event)
	{
		IBlockState state = event.getState();
		if(state.getBlock() instanceof BlockOre)
		{
			BlockOre block = (BlockOre)state.getBlock();
			ItemStack stack = event.getPlayer().getHeldItemMainhand();
			if(stack == null)
				event.setExpToDrop(0);
			else
				for(String tool : stack.getItem().getToolClasses(stack))
					if(block.isToolEffective(tool, state))
						return;
			event.setExpToDrop(0);
		}
	}
	
	@SubscribeEvent
	public static void onLandingParticlesSpawn(SendLandingParticlesEvent event)
	{
		SPacketBlockOreLandingParticles message = event.getMessage();
		Vec3d pos = message.getPosition();
		LANDING_PARTICLE_WRAPPER.sendToAllAround(message, new TargetPoint(event.getDimension(), pos.x, pos.y, pos.z, 1024));
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onHarvestBlock(HarvestDropsEvent event)
	{
		EntityPlayer player = event.getHarvester();
		ItemStack stack = player == null ? ItemStack.EMPTY : player.getHeldItemMainhand();
		if(!stack.isEmpty())
		{
			List<ItemStack> drops = event.getDrops();
			if(EnchantmentHelper.getEnchantmentLevel(MetalExtras_Objects.HOT_TO_THE_TOUCH, stack) > 0)
			{
				boolean fire = false;
				for(int i = 0; i < drops.size(); i++)
				{
					ItemStack stack1 = drops.get(i);
					if(!stack1.isEmpty())
					{
						ItemStack stack2 = FurnaceRecipes.instance().getSmeltingResult(stack1).copy();
						if(!stack2.isEmpty())
						{
							stack2.setCount(stack2.getCount() * stack1.getCount());
							for(int j = 0; j < drops.size(); j++)
							{
								ItemStack stack3 = drops.get(j);
								int count = stack3.getCount();
								if(!stack3.isEmpty() && stack3.isItemEqual(stack2) && stack3.isStackable() && 64 > count)
								{
									stack3.setCount(Math.min(64 - count, stack2.getCount()));
									stack2.setCount(stack2.getCount() + stack3.getCount() - count);
								}
							}
							while(stack2.getCount() > 0)
							{
								ItemStack stack3 = stack2.copy();
								int count = Math.min(stack2.getCount(), 64);
								stack3.setCount(count);
								drops.add(stack3);
								stack2.setCount(stack2.getCount() - count);
							}
							stack1.setCount(0);
							fire = true;
						}
					}
				}
				if(fire)
				{
					World world = event.getWorld();
					BlockPos pos = event.getPos();
					if(!world.isRemote)
						((WorldServer)world).spawnParticle(EnumParticleTypes.FLAME, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 128, 0, 0, 0, .05, new int[0]);
					world.playSound(null, pos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.BLOCKS, 1F, 1F);
				}
			}
			if(stack.getItem() instanceof ItemEnderTool || stack.getItem() instanceof ItemEnderHoe)
			{
				boolean teleport = false;
				for(int i = 0; i < drops.size(); i++)
				{
					ItemStack stack1 = drops.get(i);
					if(!stack1.isEmpty())
						teleport |= player.inventory.addItemStackToInventory(stack1);
				}
				if(teleport)
					event.getWorld().playSound(null, event.getPos(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1F, 1.25F);
			}
		}
	}
}
