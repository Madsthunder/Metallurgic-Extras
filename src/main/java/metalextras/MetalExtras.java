package metalextras;

import static api.metalextras.OreTypeDictionary.DIRTY;
import static api.metalextras.OreTypeDictionary.DRY;
import static api.metalextras.OreTypeDictionary.HOT;
import static api.metalextras.OreTypeDictionary.LOOSE;
import static api.metalextras.OreTypeDictionary.ROCKY;
import static api.metalextras.OreTypeDictionary.SANDY;
import static api.metalextras.OreTypeDictionary.WET;

import java.util.Collection;
import java.util.Random;

import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import api.metalextras.BlockOre;
import api.metalextras.ModelType;
import api.metalextras.OreMaterial;
import api.metalextras.OreType;
import api.metalextras.OreTypeDictionary;
import api.metalextras.OreTypes;
import api.metalextras.OreUtils;
import continuum.core.mod.CTCore_OH;
import continuum.essentials.client.state.StateMapperStatic;
import continuum.essentials.config.ConfigHandler;
import continuum.essentials.mod.ObjectHolder;
import metalextras.client.model.ModelOre;
import metalextras.items.ItemOre;
import metalextras.mod.MetalExtras_Callbacks;
import metalextras.ores.VanillaOreMaterial;
import metalextras.ores.properties.ConfigurationOreProperties;
import metalextras.world.gen.OreGeneration;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.RegistryBuilder;
import net.minecraftforge.oredict.OreDictionary;

@EventBusSubscriber
@Mod(modid = MetalExtras.MODID, name = MetalExtras.NAME, version = MetalExtras.VERSION, guiFactory = "metalextras.client.gui.config.GuiOverview$Factory")
public class MetalExtras
{
	public static final String MODID = "metalextras";
	public static final String NAME = "Metallurgic Extras";
	public static final String VERSION = "2.0.0";
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
			OreUtils.addMaterialToOreDictionary(COAL_ORE, "oreCoal", true);
			OreUtils.addMaterialToOreDictionary(IRON_ORE, "oreIron", true);
			OreUtils.addMaterialToOreDictionary(LAPIS_ORE, "oreLapis", true);
			OreUtils.addMaterialToOreDictionary(GOLD_ORE, "oreGold", true);
			OreUtils.addMaterialToOreDictionary(REDSTONE_ORE, "oreRedstone", true);
			OreUtils.addMaterialToOreDictionary(EMERALD_ORE, "oreEmerald", true);
			OreUtils.addMaterialToOreDictionary(DIAMOND_ORE, "oreDiamond", true);
			OreUtils.addMaterialToOreDictionary(COPPER_ORE, "oreCopper", true);
			OreUtils.addMaterialToOreDictionary(TIN_ORE, "oreTin", true);
			OreUtils.addMaterialToOreDictionary(ALUMINUM_ORE, "oreAluminum", true);
			OreUtils.addMaterialToOreDictionary(LEAD_ORE, "oreLead", true);
			OreUtils.addMaterialToOreDictionary(SILVER_ORE, "oreSilver", true);
			OreUtils.addMaterialToOreDictionary(ENDER_ORE, "oreEnder", true);
			OreUtils.addMaterialToOreDictionary(SAPPHIRE_ORE, "oreSapphire", true);
			OreUtils.addMaterialToOreDictionary(RUBY_ORE, "oreRuby", true);
			OreDictionary.registerOre("blockCopper", COPPER_BLOCK);
			OreDictionary.registerOre("blockTin", TIN_BLOCK);
			OreDictionary.registerOre("blockAluminum", ALUMINUM_BLOCK);
			OreDictionary.registerOre("blockLead", LEAD_BLOCK);
			OreDictionary.registerOre("blockSilver", SILVER_BLOCK);
			OreDictionary.registerOre("blockEnder", ENDER_BLOCK);
			OreDictionary.registerOre("blockSapphire", SAPPHIRE_BLOCK);
			OreDictionary.registerOre("blockRuby", RUBY_BLOCK);
			OreDictionary.registerOre("ingotCopper", COPPER_INGOT);
			OreDictionary.registerOre("ingotTin", TIN_INGOT);
			OreDictionary.registerOre("ingotAluminum", ALUMINUM_INGOT);
			OreDictionary.registerOre("ingotLead", LEAD_INGOT);
			OreDictionary.registerOre("ingotSilver", SILVER_INGOT);
			OreDictionary.registerOre("gemEnder", ENDER_GEM);
			OreDictionary.registerOre("gemSapphire", SAPPHIRE_GEM);
			OreDictionary.registerOre("gemRuby", RUBY_GEM);
			CONFIGURATION_HANDLER.refreshAll();
		}
		
		public void init()
		{
			OreUtils.registerMaterialSmeltingRecipe(COAL_ORE, new ItemStack(Items.COAL), .1F, true);
			OreUtils.registerMaterialSmeltingRecipe(IRON_ORE, new ItemStack(Items.IRON_INGOT), .7F, true);
			OreUtils.registerMaterialSmeltingRecipe(LAPIS_ORE, new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()), .2F, true);
			OreUtils.registerMaterialSmeltingRecipe(GOLD_ORE, new ItemStack(Items.GOLD_INGOT), 1F, true);
			OreUtils.registerMaterialSmeltingRecipe(REDSTONE_ORE, new ItemStack(Items.REDSTONE), .7F, true);
			OreUtils.registerMaterialSmeltingRecipe(EMERALD_ORE, new ItemStack(Items.EMERALD), 1F, true);
			OreUtils.registerMaterialSmeltingRecipe(DIAMOND_ORE, new ItemStack(Items.DIAMOND), 1F, true);
			OreUtils.registerMaterialSmeltingRecipe(COPPER_ORE, new ItemStack(COPPER_INGOT), .7F, true);
			OreUtils.registerMaterialSmeltingRecipe(TIN_ORE, new ItemStack(TIN_INGOT), .7F, true);
			OreUtils.registerMaterialSmeltingRecipe(ALUMINUM_ORE, new ItemStack(ALUMINUM_INGOT), .5F, true);
			OreUtils.registerMaterialSmeltingRecipe(LEAD_ORE, new ItemStack(LEAD_INGOT), .9F, true);
			OreUtils.registerMaterialSmeltingRecipe(SILVER_ORE, new ItemStack(SILVER_INGOT), 0.9F, true);
			OreUtils.registerMaterialSmeltingRecipe(ENDER_ORE, new ItemStack(ENDER_GEM), 1F, true);
			OreUtils.registerMaterialSmeltingRecipe(SAPPHIRE_ORE, new ItemStack(SAPPHIRE_GEM), 1.5F, true);
			OreUtils.registerMaterialSmeltingRecipe(RUBY_ORE, new ItemStack(RUBY_GEM), 1.5F, true);
			GameRegistry.addShapedRecipe(new ItemStack(COPPER_BLOCK), "III", "III", "III", 'I', COPPER_INGOT);
			GameRegistry.addShapelessRecipe(new ItemStack(COPPER_INGOT, 9), COPPER_BLOCK);
			GameRegistry.addShapedRecipe(new ItemStack(TIN_BLOCK), "III", "III", "III", 'I', TIN_INGOT);
			GameRegistry.addShapelessRecipe(new ItemStack(TIN_INGOT, 9), TIN_BLOCK);
			GameRegistry.addShapedRecipe(new ItemStack(ALUMINUM_BLOCK), "III", "III", "III", 'I', ALUMINUM_INGOT);
			GameRegistry.addShapelessRecipe(new ItemStack(ALUMINUM_INGOT, 9), ALUMINUM_BLOCK);
			GameRegistry.addShapedRecipe(new ItemStack(LEAD_BLOCK), "III", "III", "III", 'I', LEAD_INGOT);
			GameRegistry.addShapelessRecipe(new ItemStack(LEAD_INGOT, 9), LEAD_BLOCK);
			GameRegistry.addShapedRecipe(new ItemStack(SILVER_BLOCK), "III", "III", "III", 'I', SILVER_INGOT);
			GameRegistry.addShapelessRecipe(new ItemStack(SILVER_INGOT, 9), SILVER_BLOCK);
			GameRegistry.addShapedRecipe(new ItemStack(ENDER_BLOCK), "III", "III", "III", 'I', ENDER_GEM);
			GameRegistry.addShapelessRecipe(new ItemStack(ENDER_GEM, 9), ENDER_BLOCK);
			GameRegistry.addShapedRecipe(new ItemStack(SAPPHIRE_BLOCK), "III", "III", "III", 'I', SAPPHIRE_GEM);
			GameRegistry.addShapelessRecipe(new ItemStack(SAPPHIRE_GEM, 9), SAPPHIRE_BLOCK);
			GameRegistry.addShapedRecipe(new ItemStack(RUBY_BLOCK), "III", "III", "III", 'I', RUBY_GEM);
			GameRegistry.addShapelessRecipe(new ItemStack(RUBY_GEM, 9), RUBY_BLOCK);
			GameRegistry.registerWorldGenerator(new OreGeneration(), 100);
			CONFIGURATION_HANDLER.refreshAll();
		}
		
		public void post()
		{
			CONFIGURATION_HANDLER.refreshAll();
		}
		
		public static class ClientProxy extends Proxy
		{
			public void pre()
			{
				super.pre();
				CTCore_OH.models.put(new ResourceLocation("metalextras:models/block/ore"), Functions.constant(new ModelOre()));
				COPPER_ORE.setModel(new ResourceLocation("metalextras:block/copper_ore"));
				TIN_ORE.setModel(new ResourceLocation("metalextras:block/tin_ore"));
				ALUMINUM_ORE.setModel(new ResourceLocation("metalextras:block/aluminum_ore"));
				LEAD_ORE.setModel(new ResourceLocation("metalextras:block/lead_ore"));
				SILVER_ORE.setModel(new ResourceLocation("metalextras:block/silver_ore"));
				ENDER_ORE.setModel(new ResourceLocation("metalextras:block/ender_ore"));
				SAPPHIRE_ORE.setModel(new ResourceLocation("metalextras:block/sapphire_ore"));
				RUBY_ORE.setModel(new ResourceLocation("metalextras:block/ruby_ore"));
				SAPPHIRE_ORE.setModelType(ModelType.EMERALD);
				RUBY_ORE.setModelType(ModelType.EMERALD);
				ModelLoader.setCustomModelResourceLocation(COPPER_INGOT, 0, new ModelResourceLocation("metalextras:copper_ingot", "inventory"));
				ModelLoader.setCustomModelResourceLocation(TIN_INGOT, 0, new ModelResourceLocation("metalextras:tin_ingot", "inventory"));
				ModelLoader.setCustomModelResourceLocation(ALUMINUM_INGOT, 0, new ModelResourceLocation("metalextras:aluminum_ingot", "inventory"));
				ModelLoader.setCustomModelResourceLocation(LEAD_INGOT, 0, new ModelResourceLocation("metalextras:lead_ingot", "inventory"));
				ModelLoader.setCustomModelResourceLocation(SILVER_INGOT, 0, new ModelResourceLocation("metalextras:silver_ingot", "inventory"));
				ModelLoader.setCustomModelResourceLocation(ENDER_GEM, 0, new ModelResourceLocation("metalextras:ender_gem", "inventory"));
				ModelLoader.setCustomModelResourceLocation(SAPPHIRE_GEM, 0, new ModelResourceLocation("metalextras:sapphire_gem", "inventory"));
				ModelLoader.setCustomModelResourceLocation(RUBY_GEM, 0, new ModelResourceLocation("metalextras:ruby_gem", "inventory"));
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(COPPER_BLOCK), 0, new ModelResourceLocation("metalextras:copper_block", "normal"));
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TIN_BLOCK), 0, new ModelResourceLocation("metalextras:tin_block", "normal"));
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ALUMINUM_BLOCK), 0, new ModelResourceLocation("metalextras:aluminum_block", "normal"));
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(LEAD_BLOCK), 0, new ModelResourceLocation("metalextras:lead_block", "normal"));
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SILVER_BLOCK), 0, new ModelResourceLocation("metalextras:silver_block", "normal"));
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ENDER_BLOCK), 0, new ModelResourceLocation("metalextras:ender_block", "normal"));
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SAPPHIRE_BLOCK), 0, new ModelResourceLocation("metalextras:sapphire_block", "normal"));
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RUBY_BLOCK), 0, new ModelResourceLocation("metalextras:ruby_block", "normal"));
				for(OreMaterial material : OreUtils.getMaterialsRegistry())
				{
					for(BlockOre block : material.getBlocks())
					{
						StateMapperStatic mapper = StateMapperStatic.create(new ModelResourceLocation(new ResourceLocation("metalextras", "ore"), "normal"));
						ModelLoader.setCustomStateMapper(block, mapper);
						for(int i = 0; i < block.getOreTypeProperty().getAllowedValues().size(); i++)
						{
							Item item = Item.getItemFromBlock(block);
							ModelLoader.setCustomModelResourceLocation(item, i, mapper.getModelLocation(new ItemStack(item, 1, i)));
						}
					}
					ModelLoader.setCustomModelResourceLocation(OreMaterial.ORE, OreUtils.getMaterialsRegistry().getValues().indexOf(material), new ModelResourceLocation(new ResourceLocation("metalextras", material.getRegistryName().getResourcePath() + "_item"), "inventory"));
				}
				
			}
		}
		
		public static class ServerProxy extends Proxy
		{
			
		}
	}
	
	public static final OreTypeDictionary OTD_NETHER = OreTypeDictionary.byDimension(DimensionType.NETHER);
	public static final OreTypeDictionary OTD_END = OreTypeDictionary.byDimension(DimensionType.THE_END);
	
	public static final CreativeTabs METALLURGIC_EXTRAS = new CreativeTabs("metalextras:metallurgic_extras")
	{
		@Override
		public Item getTabIconItem()
		{
			return ENDER_GEM;
		}
	};
	
	@GameRegistry.ObjectHolder("metalextras:coal_ore")
	public static final VanillaOreMaterial COAL_ORE = null;
	@GameRegistry.ObjectHolder("metalextras:iron_ore")
	public static final VanillaOreMaterial IRON_ORE = null;
	@GameRegistry.ObjectHolder("metalextras:lapis_ore")
	public static final VanillaOreMaterial LAPIS_ORE = null;
	@GameRegistry.ObjectHolder("metalextras:gold_ore")
	public static final VanillaOreMaterial GOLD_ORE = null;
	@GameRegistry.ObjectHolder("metalextras:redstone_ore")
	public static final VanillaOreMaterial REDSTONE_ORE = null;
	@GameRegistry.ObjectHolder("metalextras:emerald_ore")
	public static final VanillaOreMaterial EMERALD_ORE = null;
	@GameRegistry.ObjectHolder("metalextras:diamond_ore")
	public static final VanillaOreMaterial DIAMOND_ORE = null;
	@GameRegistry.ObjectHolder("metalextras:copper_ore")
	public static final OreMaterial.Impl COPPER_ORE = null;
	@GameRegistry.ObjectHolder("metalextras:tin_ore")
	public static final OreMaterial.Impl TIN_ORE = null;
	@GameRegistry.ObjectHolder("metalextras:aluminum_ore")
	public static final OreMaterial.Impl ALUMINUM_ORE = null;
	@GameRegistry.ObjectHolder("metalextras:lead_ore")
	public static final OreMaterial.Impl LEAD_ORE = null;
	@GameRegistry.ObjectHolder("metalextras:silver_ore")
	public static final OreMaterial.Impl SILVER_ORE = null;
	@GameRegistry.ObjectHolder("metalextras:ender_ore")
	public static final OreMaterial.Impl ENDER_ORE = null;
	@GameRegistry.ObjectHolder("metalextras:sapphire_ore")
	public static final OreMaterial.Impl SAPPHIRE_ORE = null;
	@GameRegistry.ObjectHolder("metalextras:ruby_ore")
	public static final OreMaterial.Impl RUBY_ORE = null;
	
	@GameRegistry.ObjectHolder(value = "minecraft:rocks")
	public static final OreTypes ROCKS = null;
	@GameRegistry.ObjectHolder(value = "minecraft:dirts")
	public static final OreTypes DIRTS = null;
	
	public static final GenerateMinable.EventType COPPER_EVT = OreUtils.getEventType("COPPER");
	public static final GenerateMinable.EventType TIN_EVT = OreUtils.getEventType("TIN");
	public static final GenerateMinable.EventType ALUMINUM_EVT = OreUtils.getEventType("ALUMINUM");
	public static final GenerateMinable.EventType LEAD_EVT = OreUtils.getEventType("LEAD");
	public static final GenerateMinable.EventType SILVER_EVT = OreUtils.getEventType("SILVER");
	public static final GenerateMinable.EventType ENDER_EVT = OreUtils.getEventType("ENDER");
	public static final GenerateMinable.EventType SAPPHIRE_EVT = OreUtils.getEventType("SAPPHIRE");
	public static final GenerateMinable.EventType RUBY_EVT = OreUtils.getEventType("RUBY");
	
	@GameRegistry.ObjectHolder("metalextras:copper_block")
	public static final Block COPPER_BLOCK = null;
	@GameRegistry.ObjectHolder("metalextras:tin_block")
	public static final Block TIN_BLOCK = null;
	@GameRegistry.ObjectHolder("metalextras:aluminum_block")
	public static final Block ALUMINUM_BLOCK = null;
	@GameRegistry.ObjectHolder("metalextras:lead_block")
	public static final Block LEAD_BLOCK = null;
	@GameRegistry.ObjectHolder("metalextras:silver_block")
	public static final Block SILVER_BLOCK = null;
	@GameRegistry.ObjectHolder("metalextras:ender_block")
	public static final Block ENDER_BLOCK = null;
	@GameRegistry.ObjectHolder("metalextras:sapphire_block")
	public static final Block SAPPHIRE_BLOCK = null;
	@GameRegistry.ObjectHolder("metalextras:ruby_block")
	public static final Block RUBY_BLOCK = null;
	
	@GameRegistry.ObjectHolder("metalextras:copper_ingot")
	public static final Item COPPER_INGOT = null;
	@GameRegistry.ObjectHolder("metalextras:tin_ingot")
	public static final Item TIN_INGOT = null;
	@GameRegistry.ObjectHolder("metalextras:aluminum_ingot")
	public static final Item ALUMINUM_INGOT = null;
	@GameRegistry.ObjectHolder("metalextras:lead_ingot")
	public static final Item LEAD_INGOT = null;
	@GameRegistry.ObjectHolder("metalextras:silver_ingot")
	public static final Item SILVER_INGOT = null;
	@GameRegistry.ObjectHolder("metalextras:ender_gem")
	public static final Item ENDER_GEM = null;
	@GameRegistry.ObjectHolder("metalextras:sapphire_gem")
	public static final Item SAPPHIRE_GEM = null;
	@GameRegistry.ObjectHolder("metalextras:ruby_gem")
	public static final Item RUBY_GEM = null;
	
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
	
	@SubscribeEvent
	public static void onRegistriesCreate(RegistryEvent.NewRegistry event)
	{
		new RegistryBuilder().setType(OreTypes.class).setIDRange(0, Integer.MAX_VALUE >> 5).setName(new ResourceLocation("metalextras", "ore_type_collections")).addCallback(MetalExtras_Callbacks.ORE_TYPES).create();
		new RegistryBuilder().setType(OreMaterial.class).setIDRange(0, Integer.MAX_VALUE >> 5).setName(new ResourceLocation("metalextras", "ore_materials")).addCallback(MetalExtras_Callbacks.ORE_MATERIALS).create();
	}
	
	@SubscribeEvent
	public static void onBlocksRegister(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(ObjectHolder.newBlock(new Block(Material.IRON), "copper_block", METALLURGIC_EXTRAS));
		event.getRegistry().register(ObjectHolder.newBlock(new Block(Material.IRON), "tin_block", METALLURGIC_EXTRAS));
		event.getRegistry().register(ObjectHolder.newBlock(new Block(Material.IRON), "aluminum_block", METALLURGIC_EXTRAS));
		event.getRegistry().register(ObjectHolder.newBlock(new Block(Material.IRON), "lead_block", METALLURGIC_EXTRAS));
		event.getRegistry().register(ObjectHolder.newBlock(new Block(Material.IRON), "silver_block", METALLURGIC_EXTRAS));
		event.getRegistry().register(ObjectHolder.newBlock(new Block(Material.IRON), "ender_block", METALLURGIC_EXTRAS));
		event.getRegistry().register(ObjectHolder.newBlock(new Block(Material.IRON), "sapphire_block", METALLURGIC_EXTRAS));
		event.getRegistry().register(ObjectHolder.newBlock(new Block(Material.IRON), "ruby_block", METALLURGIC_EXTRAS));
	}
	
	@SubscribeEvent
	public static void onItemsRegister(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(new ItemOre().setCreativeTab(METALLURGIC_EXTRAS).setRegistryName("ore"));
		event.getRegistry().register(ObjectHolder.newItem(new Item(), "copper_ingot", METALLURGIC_EXTRAS));
		event.getRegistry().register(ObjectHolder.newItem(new Item(), "tin_ingot", METALLURGIC_EXTRAS));
		event.getRegistry().register(ObjectHolder.newItem(new Item(), "aluminum_ingot", METALLURGIC_EXTRAS));
		event.getRegistry().register(ObjectHolder.newItem(new Item(), "lead_ingot", METALLURGIC_EXTRAS));
		event.getRegistry().register(ObjectHolder.newItem(new Item(), "silver_ingot", METALLURGIC_EXTRAS));
		event.getRegistry().register(ObjectHolder.newItem(new Item(), "ender_gem", METALLURGIC_EXTRAS));
		event.getRegistry().register(ObjectHolder.newItem(new Item(), "sapphire_gem", METALLURGIC_EXTRAS));
		event.getRegistry().register(ObjectHolder.newItem(new Item(), "ruby_gem", METALLURGIC_EXTRAS));
		event.getRegistry().register(ObjectHolder.newItemBlock(COPPER_BLOCK));
		event.getRegistry().register(ObjectHolder.newItemBlock(TIN_BLOCK));
		event.getRegistry().register(ObjectHolder.newItemBlock(ALUMINUM_BLOCK));
		event.getRegistry().register(ObjectHolder.newItemBlock(LEAD_BLOCK));
		event.getRegistry().register(ObjectHolder.newItemBlock(SILVER_BLOCK));
		event.getRegistry().register(ObjectHolder.newItemBlock(ENDER_BLOCK));
		event.getRegistry().register(ObjectHolder.newItemBlock(SAPPHIRE_BLOCK));
		event.getRegistry().register(ObjectHolder.newItemBlock(RUBY_BLOCK));
	}
	
	@SubscribeEvent
	public static void onOreTypesRegister(RegistryEvent.Register<OreTypes> event)
	{
		OreTypes rocks = new OreTypes().setRegistryName("rocks");
		rocks.addOreType(new OreType.Impl(rocks, Blocks.STONE.getStateFromMeta(0), OreTypeDictionary.ROCKY, OreTypeDictionary.DENSE)
		{
			@Override
			public IModel getModel(OreMaterial material)
			{
				if(material.getModelType() == ModelType.EMERALD)
					return OreType.Impl.getModelFromTexture(new ResourceLocation("metalextras:blocks/emerald_stone"));
				return super.getModel(material);
			}
		}.setHardness(1.5F).setResistance(10F).setModelTexture(new ResourceLocation("minecraft:blocks/stone")).setRegistryName("minecraft:stone"));
		rocks.addOreType(new OreType.Impl(rocks, Blocks.STONE.getStateFromMeta(1), OreTypeDictionary.ROCKY, OreTypeDictionary.DENSE).setHardness(1.5F).setResistance(10F).setModelTexture(new ResourceLocation("minecraft:blocks/stone_granite")).setRegistryName("minecraft:granite"));
		rocks.addOreType(new OreType.Impl(rocks, Blocks.STONE.getStateFromMeta(3), OreTypeDictionary.ROCKY, OreTypeDictionary.DENSE).setHardness(1.5F).setResistance(10F).setModelTexture(new ResourceLocation("minecraft:blocks/stone_diorite")).setRegistryName("minecraft:diorite"));
		rocks.addOreType(new OreType.Impl(rocks, Blocks.STONE.getStateFromMeta(5), OreTypeDictionary.ROCKY, OreTypeDictionary.DENSE).setHardness(1.5F).setResistance(10F).setModelTexture(new ResourceLocation("minecraft:blocks/stone_andesite")).setRegistryName("minecraft:andesite"));
		rocks.addOreType(new OreType.Impl(rocks, Blocks.SANDSTONE.getDefaultState(), OreTypeDictionary.ROCKY, OreTypeDictionary.SANDY, OreTypeDictionary.COMPACT, OreTypeDictionary.DRY).setHardness(.8F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/sandstone_normal")).setRegistryName("minecraft:sandstone"));
		rocks.addOreType(new OreType.Impl(rocks, Blocks.RED_SANDSTONE.getDefaultState(), OreTypeDictionary.ROCKY, OreTypeDictionary.SANDY, OreTypeDictionary.COMPACT, OreTypeDictionary.DRY).setHardness(.8F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/red_sandstone_normal")).setRegistryName("minecraft:red_sandstone"));
		rocks.addOreType(new OreType.Impl(rocks, Blocks.NETHERRACK.getDefaultState(), OreTypeDictionary.ROCKY, OTD_NETHER, OreTypeDictionary.HOT, OreTypeDictionary.COMPACT).setHardness(.4F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/netherrack")).setRegistryName("minecraft:netherrack"));
		rocks.addOreType(new OreType.Impl(rocks, Blocks.END_STONE.getDefaultState(), OreTypeDictionary.ROCKY, OTD_END, OreTypeDictionary.DENSE).setHardness(3F).setResistance(15F).setModelTexture(new ResourceLocation("minecraft:blocks/end_stone")).setRegistryName("minecraft:end_stone"));
		rocks.addOreType(new OreType.Impl(rocks, Blocks.BEDROCK.getDefaultState(), OreTypeDictionary.ROCKY, OreTypeDictionary.DENSE).setHardness(-1F).setResistance(6000000F).setModelTexture(new ResourceLocation("minecraft:blocks/bedrock")).setRegistryName("minecraft:bedrock"));
		event.getRegistry().register(rocks);
		OreTypes dirts = new OreTypes().setRegistryName("dirts");
		dirts.addOreType(new OreType.Impl(dirts, Blocks.DIRT.getStateFromMeta(0), OreTypeDictionary.DIRTY, OreTypeDictionary.COMPACT).setHardness(.5F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/dirt")).setRegistryName("minecraft:dirt"));
		dirts.addOreType(new OreType.Impl(dirts, Blocks.DIRT.getStateFromMeta(1), OreTypeDictionary.DIRTY, OreTypeDictionary.ROCKY, OreTypeDictionary.COMPACT).setHardness(.5F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/coarse_dirt")).setRegistryName("minecraft:coarse_dirt"));
		dirts.addOreType(new OreType.Impl(dirts, Blocks.SAND.getStateFromMeta(0), OreTypeDictionary.DIRTY, OreTypeDictionary.SANDY, OreTypeDictionary.LOOSE, OreTypeDictionary.DRY).setHardness(.5F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/sand")).setRegistryName("minecraft:sand"));
		dirts.addOreType(new OreType.Impl(dirts, Blocks.SAND.getStateFromMeta(1), OreTypeDictionary.DIRTY, OreTypeDictionary.SANDY, OreTypeDictionary.LOOSE, OreTypeDictionary.DRY).setHardness(.5F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/red_sand")).setRegistryName("minecraft:red_sand"));
		dirts.addOreType(new OreType.Impl(dirts, Blocks.CLAY.getDefaultState(), OreTypeDictionary.DIRTY, OreTypeDictionary.COMPACT, OreTypeDictionary.WET).setHardness(.6F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/clay")).setRegistryName("minecraft:clay"));
		dirts.addOreType(new OreType.Impl(dirts, Blocks.GRAVEL.getDefaultState(), OreTypeDictionary.DIRTY, OreTypeDictionary.ROCKY, OreTypeDictionary.LOOSE).setHardness(.6F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/gravel")).setRegistryName("minecraft:gravel"));
		dirts.addOreType(new OreType.Impl(dirts, Blocks.SOUL_SAND.getDefaultState(), OreTypeDictionary.DIRTY, OreTypeDictionary.SANDY, OTD_NETHER)
		{
			@Override
			public void handleEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity)
			{
				entity.motionX *= .4;
				entity.motionZ *= .4;
			}
			
			@Override
			public AxisAlignedBB getSelectionBox(World world, BlockPos pos)
			{
				return new AxisAlignedBB(0, 0, 0, 1, 0.875, 1);
			}
		}.setHardness(.5F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/soul_sand")).setRegistryName("minecraft:soul_sand"));
		event.getRegistry().register(dirts);
	}
	
	@SubscribeEvent
	public static void onOreMaterialsRegister(RegistryEvent.Register<OreMaterial> event)
	{
		event.getRegistry().register(new OreMaterial.Impl(ConfigurationOreProperties.func("copper_ore", true, 20, 0, 64, -Float.MAX_VALUE, Float.MAX_VALUE, 9, Predicates.alwaysTrue())).setHarvestLevel(1).setItemDroppedAsOre().setCreativeTab(METALLURGIC_EXTRAS).setOverrides(COPPER_EVT).setLanguageKey("tile.metalextras:copper_ore").setRegistryName("metalextras:copper_ore"));
		event.getRegistry().register(new OreMaterial.Impl(ConfigurationOreProperties.func("tin_ore", true, 20, 0, 64, -Float.MAX_VALUE, Float.MAX_VALUE, 9, OreTypeDictionary.notAny(OTD_END))).setHarvestLevel(1).setItemDroppedAsOre().setCreativeTab(METALLURGIC_EXTRAS).setOverrides(TIN_EVT).setLanguageKey("tile.metalextras:tin_ore").setRegistryName("metalextras:tin_ore"));
		event.getRegistry().register(new OreMaterial.Impl(ConfigurationOreProperties.func("aluminum_ore", true, 6, 32, 128, -Float.MAX_VALUE, Float.MAX_VALUE, 5, OreTypeDictionary.all(ROCKY))).setHarvestLevel(1).setItemDroppedAsOre().setCreativeTab(METALLURGIC_EXTRAS).setOverrides(ALUMINUM_EVT).setLanguageKey("tile.metalextras:aluminum_ore").setRegistryName("metalextras:aluminum_ore"));
		event.getRegistry().register(new OreMaterial.Impl(ConfigurationOreProperties.func("lead_ore", true, 8, 32, 64, -Float.MAX_VALUE, Float.MAX_VALUE, 8, OreTypeDictionary.notAny(DIRTY, OTD_END))).setHarvestLevel(2).setItemDroppedAsOre().setCreativeTab(METALLURGIC_EXTRAS).setOverrides(LEAD_EVT).setLanguageKey("tile.metalextras:lead_ore").setRegistryName("metalextras:lead_ore"));
		event.getRegistry().register(new OreMaterial.Impl(ConfigurationOreProperties.func("silver_ore", true, 8, 0, 32, -Float.MAX_VALUE, Float.MAX_VALUE, 8, OreTypeDictionary.notAny(DIRTY, SANDY, OTD_END))).setHarvestLevel(2).setItemDroppedAsOre().setCreativeTab(METALLURGIC_EXTRAS).setOverrides(SILVER_EVT).setLanguageKey("tile.metalextras:silver_ore").setRegistryName("metalextras:silver_ore"));
		event.getRegistry().register(new OreMaterial.Impl(ConfigurationOreProperties.func("ender_ore", true, 20, 0, 64, -Float.MAX_VALUE, Float.MAX_VALUE, 9, OreTypeDictionary.all(OTD_END))).setHarvestLevel(3).setItemDropped(ENDER_GEM, 0, 3, 7).setCreativeTab(METALLURGIC_EXTRAS).setOverrides(ENDER_EVT).setLanguageKey("tile.metalextras:ender_ore").setRegistryName("metalextras:ender_ore"));
		event.getRegistry().register(new OreMaterial.Impl(ConfigurationOreProperties.func("sapphire_ore", true, 20, 0, 64, -Float.MAX_VALUE, 0.2F, 3, OreTypeDictionary.notAny(LOOSE, DRY, SANDY, HOT, OTD_NETHER))).setHarvestLevel(3).setItemDropped(SAPPHIRE_GEM, 0, 3, 7).setCreativeTab(METALLURGIC_EXTRAS).setOverrides(SAPPHIRE_EVT).setLanguageKey("tile.metalextras:sapphire_ore").setRegistryName("metalextras:sapphire_ore"));
		event.getRegistry().register(new OreMaterial.Impl(ConfigurationOreProperties.func("ruby_ore", true, 20, 0, 64, 1F, Float.MAX_VALUE, 3, new Predicate<Collection<OreTypeDictionary>>()
		{
			@Override
			public boolean apply(Collection<OreTypeDictionary> entries)
			{
				if((entries.contains(DIRTY)))// && (entries.contains(LOOSE) ?
												// (entries.contains(SANDY) &&
												// entries.contains(DRY)) :
												// (entries.contains(SANDY) ||
												// entries.contains(WET) ? false
												// :
												// !entries.contains(MetalExtras_OH.OTD_END))))
												// || (!entries.contains(DIRTY)
												// &&
												// !entries.contains(MetalExtras_OH.OTD_END)))
				{
					if(entries.contains(LOOSE))
					{
						return entries.contains(SANDY) && entries.contains(DRY);
					}
					else if(entries.contains(SANDY) || entries.contains(WET))
						return false;
				}
				return !entries.contains(OTD_END);
			}
		})).setHarvestLevel(3).setItemDropped(RUBY_GEM, 0, 3, 7).setOverrides(RUBY_EVT).setCreativeTab(METALLURGIC_EXTRAS).setLanguageKey("tile.metalextras:ruby_ore").setModel(new ResourceLocation("metalextras:block/ruby_ore")).setRegistryName("metalextras", "ruby_ore"));
		event.getRegistry().register(new VanillaOreMaterial(Blocks.COAL_ORE.getDefaultState(), 0, 2, ModelType.IRON, EventType.COAL)
		{
			@Override
			public int getSpawnTries(World world, Random random)
			{
				return OreGeneration.getChunkProviderSettings(world).coalCount;
			}
			
			@Override
			public int[] getSpawnParams(World world)
			{
				ChunkProviderSettings settings = OreGeneration.getChunkProviderSettings(world);
				return new int[] { settings.coalMinHeight, settings.coalMaxHeight };
			}
			
			@Override
			public int getVeinSize(BiomeDecorator decorator)
			{
				return OreGeneration.getChunkProviderSettings(decorator).coalSize;
			}
		}.setRegistryName("metalextras:coal_ore"));
		event.getRegistry().register(new VanillaOreMaterial(Blocks.IRON_ORE.getDefaultState(), 0, 0, ModelType.IRON, EventType.IRON)
		{
			@Override
			public int getSpawnTries(World world, Random random)
			{
				return OreGeneration.getChunkProviderSettings(world).ironCount;
			}
			
			@Override
			public int[] getSpawnParams(World world)
			{
				ChunkProviderSettings settings = OreGeneration.getChunkProviderSettings(world);
				return new int[] { settings.ironMinHeight, settings.ironMaxHeight };
			}
			
			@Override
			public int getVeinSize(BiomeDecorator decorator)
			{
				return (decorator.chunkProviderSettings == null ? OreGeneration.defaultSettings : decorator.chunkProviderSettings).ironSize;
			}
		}.setRegistryName("metalextras:iron_ore"));
		event.getRegistry().register(new VanillaOreMaterial(Blocks.LAPIS_ORE.getDefaultState(), 2, 5, ModelType.LAPIS, EventType.LAPIS)
		{
			@Override
			public int getSpawnTries(World world, Random random)
			{
				return OreGeneration.getChunkProviderSettings(world).lapisCount;
			}
			
			@Override
			public int[] getSpawnParams(World world)
			{
				ChunkProviderSettings settings = OreGeneration.getChunkProviderSettings(world);
				return new int[] { settings.lapisCenterHeight, settings.lapisSpread };
			}
			
			@Override
			public GenerateType getGenerateType()
			{
				return GenerateType.ORE2;
			}
			
			@Override
			public int getVeinSize(BiomeDecorator decorator)
			{
				return OreGeneration.getChunkProviderSettings(decorator).lapisSize;
			}
		}.setRegistryName("metalextras:lapis_ore"));
		event.getRegistry().register(new VanillaOreMaterial(Blocks.GOLD_ORE.getDefaultState(), 0, 0, ModelType.IRON, EventType.GOLD)
		{
			@Override
			public int getSpawnTries(World world, Random random)
			{
				return OreGeneration.getChunkProviderSettings(world).goldCount;
			}
			
			@Override
			public int[] getSpawnParams(World world)
			{
				ChunkProviderSettings settings = OreGeneration.getChunkProviderSettings(world);
				return new int[] { settings.goldMinHeight, settings.goldMaxHeight };
			}
			
			@Override
			public int getVeinSize(BiomeDecorator decorator)
			{
				return OreGeneration.getChunkProviderSettings(decorator).goldSize;
			}
		}.setRegistryName("metalextras:gold_ore"));
		event.getRegistry().register(new VanillaOreMaterial(Blocks.REDSTONE_ORE.getDefaultState(), 0, 0, ModelType.IRON, EventType.REDSTONE)
		{
			@Override
			public int getSpawnTries(World world, Random random)
			{
				return OreGeneration.getChunkProviderSettings(world).redstoneCount;
			}
			
			@Override
			public int[] getSpawnParams(World world)
			{
				ChunkProviderSettings settings = OreGeneration.getChunkProviderSettings(world);
				return new int[] { settings.redstoneMinHeight, settings.redstoneMaxHeight };
			}
			
			@Override
			public int getVeinSize(BiomeDecorator decorator)
			{
				return OreGeneration.getChunkProviderSettings(decorator).redstoneSize;
			}
		}.setRegistryName("metalextras:redstone_ore"));
		event.getRegistry().register(new VanillaOreMaterial(Blocks.EMERALD_ORE.getDefaultState(), 3, 7, ModelType.EMERALD, EventType.EMERALD)
		{
			@Override
			public int getSpawnTries(World world, Random random)
			{
				return 3 + random.nextInt(6);
			}
			
			@Override
			public int[] getSpawnParams(World world)
			{
				ChunkProviderSettings settings = OreGeneration.getChunkProviderSettings(world);
				return new int[] { 28, 4 };
			}
			
			@Override
			public GenerateType getGenerateType()
			{
				return GenerateType.ORE3;
			}
			
			@Override
			public int getVeinSize(BiomeDecorator decorator)
			{
				return 1;
			}
		}.setRegistryName("metalextras:emerald_ore"));
		event.getRegistry().register(new VanillaOreMaterial(Blocks.DIAMOND_ORE.getDefaultState(), 3, 7, ModelType.IRON, EventType.DIAMOND)
		{
			@Override
			public int getSpawnTries(World world, Random random)
			{
				return OreGeneration.getChunkProviderSettings(world).diamondCount;
			}
			
			@Override
			public int[] getSpawnParams(World world)
			{
				ChunkProviderSettings settings = OreGeneration.getChunkProviderSettings(world);
				return new int[] { settings.diamondMinHeight, settings.diamondMaxHeight };
			}
			
			@Override
			public int getVeinSize(BiomeDecorator decorator)
			{
				return OreGeneration.getChunkProviderSettings(decorator).diamondSize;
			}
		}.setRegistryName("metalextras:diamond_ore"));
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
}
