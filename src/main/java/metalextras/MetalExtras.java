package metalextras;

import java.util.List;

import api.metalextras.BlockOre;
import api.metalextras.Characteristic;
import api.metalextras.OreUtils;
import api.metalextras.SPacketBlockOreLandingParticles;
import api.metalextras.SPacketBlockOreLandingParticles.SendLandingParticlesEvent;
import continuum.essentials.config.ConfigHandler;
import metalextras.items.ItemEnderHoe;
import metalextras.items.ItemEnderTool;
import metalextras.packets.OreLandingParticleMessageHandler;
import metalextras.world.gen.OreGeneration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@EventBusSubscriber
@Mod(modid = MetalExtras.MODID, name = MetalExtras.NAME, version = MetalExtras.VERSION, guiFactory = "metalextras.client.gui.config.GuiOverview$Factory")
public class MetalExtras
{
	public static final String MODID = "metalextras";
	public static final String NAME = "Metallurgic Extras";
	public static final String VERSION = "2.3.0";
	public static final ConfigHandler CONFIGURATION_HANDLER = new ConfigHandler("config\\Metallurgic Extras");
	
	public static class Proxy
	{
		@SidedProxy(modId = MODID)
		public static Proxy I = null;
		
		public void constr()
		{
		}
		
		public void pre()
		{
			LANDING_PARTICLE_WRAPPER.registerMessage(OreLandingParticleMessageHandler.class, SPacketBlockOreLandingParticles.class, 0, Side.CLIENT);
			CONFIGURATION_HANDLER.refreshAll();
		}
		
		public void init()
		{
            OreUtils.addMaterialToOreDictionary(MetalExtras_Objects.COAL_ORE, "oreCoal", true);
            OreUtils.addMaterialToOreDictionary(MetalExtras_Objects.IRON_ORE, "oreIron", true);
            OreUtils.addMaterialToOreDictionary(MetalExtras_Objects.LAPIS_ORE, "oreLapis", true);
            OreUtils.addMaterialToOreDictionary(MetalExtras_Objects.GOLD_ORE, "oreGold", true);
            OreUtils.addMaterialToOreDictionary(MetalExtras_Objects.REDSTONE_ORE, "oreRedstone", true);
            OreUtils.addMaterialToOreDictionary(MetalExtras_Objects.EMERALD_ORE, "oreEmerald", true);
            OreUtils.addMaterialToOreDictionary(MetalExtras_Objects.DIAMOND_ORE, "oreDiamond", true);
            OreUtils.addMaterialToOreDictionary(MetalExtras_Objects.COPPER_ORE, "oreCopper", true);
            OreUtils.addMaterialToOreDictionary(MetalExtras_Objects.TIN_ORE, "oreTin", true);
            OreUtils.addMaterialToOreDictionary(MetalExtras_Objects.ALUMINUM_ORE, "oreAluminum", true);
            OreUtils.addMaterialToOreDictionary(MetalExtras_Objects.LEAD_ORE, "oreLead", true);
            OreUtils.addMaterialToOreDictionary(MetalExtras_Objects.SILVER_ORE, "oreSilver", true);
            OreUtils.addMaterialToOreDictionary(MetalExtras_Objects.ENDER_ORE, "oreEnder", true);
            OreUtils.addMaterialToOreDictionary(MetalExtras_Objects.SAPPHIRE_ORE, "oreSapphire", true);
            OreUtils.addMaterialToOreDictionary(MetalExtras_Objects.RUBY_ORE, "oreRuby", true);
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
			OreUtils.registerMaterialSmeltingRecipe(MetalExtras_Objects.COAL_ORE, new ItemStack(Items.COAL), .1F, true);
			OreUtils.registerMaterialSmeltingRecipe(MetalExtras_Objects.IRON_ORE, new ItemStack(Items.IRON_INGOT), .7F, true);
			OreUtils.registerMaterialSmeltingRecipe(MetalExtras_Objects.LAPIS_ORE, new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()), .2F, true);
			OreUtils.registerMaterialSmeltingRecipe(MetalExtras_Objects.GOLD_ORE, new ItemStack(Items.GOLD_INGOT), 1F, true);
			OreUtils.registerMaterialSmeltingRecipe(MetalExtras_Objects.REDSTONE_ORE, new ItemStack(Items.REDSTONE), .7F, true);
			OreUtils.registerMaterialSmeltingRecipe(MetalExtras_Objects.EMERALD_ORE, new ItemStack(Items.EMERALD), 1F, true);
			OreUtils.registerMaterialSmeltingRecipe(MetalExtras_Objects.DIAMOND_ORE, new ItemStack(Items.DIAMOND), 1F, true);
			OreUtils.registerMaterialSmeltingRecipe(MetalExtras_Objects.COPPER_ORE, new ItemStack(MetalExtras_Objects.COPPER_INGOT), .7F, true);
			OreUtils.registerMaterialSmeltingRecipe(MetalExtras_Objects.TIN_ORE, new ItemStack(MetalExtras_Objects.TIN_INGOT), .7F, true);
			OreUtils.registerMaterialSmeltingRecipe(MetalExtras_Objects.ALUMINUM_ORE, new ItemStack(MetalExtras_Objects.ALUMINUM_INGOT), .5F, true);
			OreUtils.registerMaterialSmeltingRecipe(MetalExtras_Objects.LEAD_ORE, new ItemStack(MetalExtras_Objects.LEAD_INGOT), .9F, true);
			OreUtils.registerMaterialSmeltingRecipe(MetalExtras_Objects.SILVER_ORE, new ItemStack(MetalExtras_Objects.SILVER_INGOT), 0.9F, true);
			OreUtils.registerMaterialSmeltingRecipe(MetalExtras_Objects.ENDER_ORE, new ItemStack(MetalExtras_Objects.ENDER_GEM), 1F, true);
			OreUtils.registerMaterialSmeltingRecipe(MetalExtras_Objects.SAPPHIRE_ORE, new ItemStack(MetalExtras_Objects.SAPPHIRE_GEM), 1.5F, true);
			OreUtils.registerMaterialSmeltingRecipe(MetalExtras_Objects.RUBY_ORE, new ItemStack(MetalExtras_Objects.RUBY_GEM), 1.5F, true);
			GameRegistry.registerWorldGenerator(new OreGeneration(), 100);
			CONFIGURATION_HANDLER.refreshAll();
		}
		
		public void post()
		{
			CONFIGURATION_HANDLER.refreshAll();
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
	
	public static final CreativeTabs METALLURGIC_EXTRAS = new CreativeTabs("metalextras:metallurgic_extras")
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return METALLURGIC_EXTRAS_ITEM;
		}
	};
	
	public static final GenerateMinable.EventType COPPER_EVT = OreUtils.getEventType("COPPER");
	public static final GenerateMinable.EventType TIN_EVT = OreUtils.getEventType("TIN");
	public static final GenerateMinable.EventType ALUMINUM_EVT = OreUtils.getEventType("ALUMINUM");
	public static final GenerateMinable.EventType LEAD_EVT = OreUtils.getEventType("LEAD");
	public static final GenerateMinable.EventType SILVER_EVT = OreUtils.getEventType("SILVER");
	public static final GenerateMinable.EventType ENDER_EVT = OreUtils.getEventType("ENDER");
	public static final GenerateMinable.EventType SAPPHIRE_EVT = OreUtils.getEventType("SAPPHIRE");
	public static final GenerateMinable.EventType RUBY_EVT = OreUtils.getEventType("RUBY");
	
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
	
    @Mod.EventHandler
    public void serverStarted(FMLServerStartingEvent event)
    {
    }
	
	
	@SubscribeEvent
	public static void onConfigChanged(OnConfigChangedEvent event)
	{
		if(event.getModID().equals("metalextras"))
			CONFIGURATION_HANDLER.writeAll();
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
