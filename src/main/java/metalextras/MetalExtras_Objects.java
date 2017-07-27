package metalextras;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.internal.bind.TypeAdapters;
import api.metalextras.BlockOre;
import api.metalextras.Characteristic;
import api.metalextras.IBlockOreMulti;
import api.metalextras.ModelType;
import api.metalextras.OreType;
import api.metalextras.OreTypes;
import api.metalextras.OreUtils;
import metalextras.client.model.ModelOre;
import metalextras.enchantments.EnchantmentHotTouch;
import metalextras.items.ItemBlockOre;
import metalextras.items.ItemEnderHoe;
import metalextras.items.ItemEnderTool;
import metalextras.items.ItemHoe;
import metalextras.items.ItemOre;
import metalextras.items.ItemTool;
import metalextras.mod.MetalExtras_Callbacks;
import metalextras.newores.NewOreType;
import metalextras.newores.VariableManager;
import metalextras.newores.modules.BlockModule;
import metalextras.newores.modules.GenerationModule;
import metalextras.newores.modules.TypeModelModule;
import metalextras.newores.modules.RegisterModuleFactoriesEvent;
import metalextras.newores.modules.SmeltingModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStone;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.RegistryBuilder;

@ObjectHolder(MetalExtras.MODID)
@EventBusSubscriber(modid = MetalExtras.MODID)
public class MetalExtras_Objects
{
	@ObjectHolder("minecraft:rocks")
	public static final OreTypes ROCKS = null;
	@ObjectHolder("minecraft:dirts")
	public static final OreTypes DIRTS = null;
	public static final Block COPPER_BLOCK = null;
	public static final Block TIN_BLOCK = null;
	public static final Block ALUMINUM_BLOCK = null;
	public static final Block LEAD_BLOCK = null;
	public static final Block SILVER_BLOCK = null;
	public static final Block ENDER_BLOCK = null;
	public static final Block SAPPHIRE_BLOCK = null;
	public static final Block RUBY_BLOCK = null;
	public static final ItemTool SILVER_SHOVEL = null;
	public static final ItemTool SILVER_PICKAXE = null;
	public static final ItemTool SILVER_AXE = null;
	public static final ItemEnderTool ENDER_SHOVEL = null;
	public static final ItemEnderTool ENDER_PICKAXE = null;
	public static final ItemEnderTool ENDER_AXE = null;
	public static final ItemEnderHoe ENDER_HOE = null;
	public static final ItemTool SAPPHIRE_SHOVEL = null;
	public static final ItemTool SAPPHIRE_PICKAXE = null;
	public static final ItemTool SAPPHIRE_AXE = null;
	public static final ItemHoe SAPPHIRE_HOE = null;
	public static final ItemTool RUBY_SHOVEL = null;
	public static final ItemTool RUBY_PICKAXE = null;
	public static final ItemTool RUBY_AXE = null;
	public static final ItemHoe RUBY_HOE = null;
	public static final Item COPPER_NUGGET = null;
	public static final Item TIN_NUGGET = null;
	public static final Item ALUMINUM_NUGGET = null;
	public static final Item LEAD_NUGGET = null;
	public static final Item SILVER_NUGGET = null;
	public static final Item COPPER_INGOT = null;
	public static final Item TIN_INGOT = null;
	public static final Item ALUMINUM_INGOT = null;
	public static final Item LEAD_INGOT = null;
	public static final Item SILVER_INGOT = null;
	public static final Item ENDER_GEM = null;
	public static final Item SAPPHIRE_GEM = null;
	public static final Item RUBY_GEM = null;
	public static final EnchantmentHotTouch HOT_TO_THE_TOUCH = null;

	@SubscribeEvent
	public static void onRegistriesCreate(RegistryEvent.NewRegistry event)
	{
		new RegistryBuilder<OreTypes>().setType(OreTypes.class).setIDRange(0, Integer.MAX_VALUE >> 5).setName(new ResourceLocation("metalextras", "ore_type_collections")).addCallback(MetalExtras_Callbacks.ORE_TYPES).create();
	}

	@SubscribeEvent
	public static void onBlocksRegister(RegistryEvent.Register<Block> event)
	{
		class BlockCompressed extends Block
		{
			public BlockCompressed(Material material, Integer harvest_level)
			{
				super(material);
				this.setSoundType(SoundType.METAL).setHardness(3F).setResistance(10F).setHarvestLevel("pickaxe", harvest_level);
			}
		}
		event.getRegistry().register(new BlockCompressed(Material.IRON, 1).setCreativeTab(CreativeTabs.BUILDING_BLOCKS).setUnlocalizedName("metalextras:copper_block").setRegistryName("metalextras:copper_block"));
		event.getRegistry().register(new BlockCompressed(Material.IRON, 1).setCreativeTab(CreativeTabs.BUILDING_BLOCKS).setUnlocalizedName("metalextras:tin_block").setRegistryName("metalextras:tin_block"));
		event.getRegistry().register(new BlockCompressed(Material.IRON, 1).setCreativeTab(CreativeTabs.BUILDING_BLOCKS).setUnlocalizedName("metalextras:aluminum_block").setRegistryName("metalextras:aluminum_block"));
		event.getRegistry().register(new BlockCompressed(Material.IRON, 2).setCreativeTab(CreativeTabs.BUILDING_BLOCKS).setUnlocalizedName("metalextras:lead_block").setRegistryName("metalextras:lead_block"));
		event.getRegistry().register(new BlockCompressed(Material.IRON, 2).setCreativeTab(CreativeTabs.BUILDING_BLOCKS).setUnlocalizedName("metalextras:silver_block").setRegistryName("metalextras:silver_block"));
		event.getRegistry().register(new BlockCompressed(Material.IRON, 3).setCreativeTab(CreativeTabs.BUILDING_BLOCKS).setUnlocalizedName("metalextras:ender_block").setRegistryName("metalextras:ender_block"));
		event.getRegistry().register(new BlockCompressed(Material.IRON, 3).setCreativeTab(CreativeTabs.BUILDING_BLOCKS).setUnlocalizedName("metalextras:sapphire_block").setRegistryName("metalextras:sapphire_block"));
		event.getRegistry().register(new BlockCompressed(Material.IRON, 3).setCreativeTab(CreativeTabs.BUILDING_BLOCKS).setUnlocalizedName("metalextras:ruby_block").setRegistryName("metalextras:ruby_block"));
	}

	@SubscribeEvent
	public static void onItemsRegister(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(new ItemOre().setCreativeTab(MetalExtras.METALLURGIC_EXTRAS).setRegistryName("ore"));
		class ItemShovel extends ItemTool
		{
			public ItemShovel(int harvest_level, float proper_block_effeciency, int enchantability, String repair_material, float entity_damage, float attack_speed, int max_uses, Object... effective_objects)
			{
				super("shovel", harvest_level, proper_block_effeciency, enchantability, repair_material, entity_damage, attack_speed, max_uses, effective_objects);
			}

			@Override
			public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
			{
				ItemStack stack = player.getHeldItem(hand);
				if(!player.canPlayerEdit(pos.offset(side), side, stack))
					return EnumActionResult.FAIL;
				else if(side != EnumFacing.DOWN && world.getBlockState(pos.up()).getMaterial() == Material.AIR && world.getBlockState(pos).getBlock() == Blocks.GRASS)
				{
					world.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1F, 1F);
					if(!world.isRemote)
					{
						world.setBlockState(pos, Blocks.GRASS_PATH.getDefaultState(), 11);
						stack.damageItem(1, player);
					}
					return EnumActionResult.SUCCESS;
				}
				return EnumActionResult.PASS;
			}
		}
		class ItemEnderShovel extends ItemEnderTool
		{
			public ItemEnderShovel(float entity_damage, float attack_speed, Object... effective_objects)
			{
				super("shovel", entity_damage, attack_speed, effective_objects);
			}

			@Override
			public boolean canHarvestBlock(IBlockState state)
			{
				Block block = state.getBlock();
				return block == Blocks.SNOW_LAYER || block == Blocks.SNOW;
			}

			@Override
			public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
			{
				ItemStack stack = player.getHeldItem(hand);
				if(!player.canPlayerEdit(pos.offset(side), side, stack))
					return EnumActionResult.FAIL;
				else if(side != EnumFacing.DOWN && world.getBlockState(pos.up()).getMaterial() == Material.AIR && world.getBlockState(pos).getBlock() == Blocks.GRASS)
				{
					world.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1F, 1F);
					if(!world.isRemote)
					{
						world.setBlockState(pos, Blocks.GRASS_PATH.getDefaultState(), 11);
						stack.damageItem(1, player);
					}
					return EnumActionResult.SUCCESS;
				}
				return EnumActionResult.PASS;
			}
		}
		event.getRegistry().register(new ItemEnderShovel(4F, -3F).setCreativeTab(CreativeTabs.TOOLS).setUnlocalizedName("metalextras:ender_shovel").setRegistryName("metalextras:ender_shovel"));
		event.getRegistry().register(new ItemEnderTool("pickaxe", 3.5F, -2.8F, Material.IRON, Material.ANVIL, Material.ROCK).setCreativeTab(CreativeTabs.TOOLS).setUnlocalizedName("metalextras:ender_pickaxe").setRegistryName("metalextras:ender_pickaxe"));
		event.getRegistry().register(new ItemEnderTool("axe", 8F, -3F, Material.WOOD, Material.PLANTS, Material.VINE).setCreativeTab(CreativeTabs.TOOLS).setUnlocalizedName("metalextras:ender_axe").setRegistryName("metalextras:ender_axe"));
		event.getRegistry().register(new ItemEnderHoe().setCreativeTab(CreativeTabs.TOOLS).setUnlocalizedName("metalextras:ender_hoe").setRegistryName("metalextras:ender_hoe"));
		event.getRegistry().register(new ItemShovel(4, 10F, 18, "gemSapphire", 4.5F, -3F, 2000).setCreativeTab(CreativeTabs.TOOLS).setUnlocalizedName("metalextras:sapphire_shovel").setRegistryName("metalextras:sapphire_shovel"));
		event.getRegistry().register(new ItemTool("pickaxe", 4, 10F, 18, "gemSapphire", 4.5F, 1F, 2000, Material.IRON, Material.ANVIL, Material.ROCK).setCreativeTab(CreativeTabs.TOOLS).setUnlocalizedName("metalextras:sapphire_pickaxe").setRegistryName("metalextras:sapphire_pickaxe"));
		event.getRegistry().register(new ItemTool("axe", 4, 10F, 18, "gemSapphire", 8F, -3F, 2000, Material.WOOD, Material.PLANTS, Material.VINE).setCreativeTab(CreativeTabs.TOOLS).setUnlocalizedName("metalextras:sapphire_axe").setRegistryName("metalextras:sapphire_axe"));
		event.getRegistry().register(new ItemHoe("gemSapphire", true, 4.5F, 2000).setCreativeTab(CreativeTabs.TOOLS).setUnlocalizedName("metalextras:sapphire_hoe").setRegistryName("metalextras:sapphire_hoe"));
		event.getRegistry().register(new ItemShovel(4, 10F, 18, "gemRuby", 4.5F, -3F, 2000).setCreativeTab(CreativeTabs.TOOLS).setUnlocalizedName("metalextras:ruby_shovel").setRegistryName("metalextras:ruby_shovel"));
		event.getRegistry().register(new ItemTool("pickaxe", 4, 10F, 18, "gemRuby", 4.5F, 1F, 2000, Material.IRON, Material.ANVIL, Material.ROCK).setCreativeTab(CreativeTabs.TOOLS).setUnlocalizedName("metalextras:ruby_pickaxe").setRegistryName("metalextras:ruby_pickaxe"));
		event.getRegistry().register(new ItemTool("axe", 4, 10F, 18, "gemRuby", 8F, -3F, 2000, Material.WOOD, Material.PLANTS, Material.VINE).setCreativeTab(CreativeTabs.TOOLS).setUnlocalizedName("metalextras:ruby_axe").setRegistryName("metalextras:ruby_axe"));
		event.getRegistry().register(new ItemHoe("gemRuby", true, 4.5F, 2000).setCreativeTab(CreativeTabs.TOOLS).setUnlocalizedName("metalextras:ruby_hoe").setRegistryName("metalextras:ruby_hoe"));
		event.getRegistry().register(new Item().setCreativeTab(CreativeTabs.MATERIALS).setUnlocalizedName("metalextras:copper_nugget").setRegistryName("metalextras:copper_nugget"));
		event.getRegistry().register(new Item().setCreativeTab(CreativeTabs.MATERIALS).setUnlocalizedName("metalextras:tin_nugget").setRegistryName("metalextras:tin_nugget"));
		event.getRegistry().register(new Item().setCreativeTab(CreativeTabs.MATERIALS).setUnlocalizedName("metalextras:aluminum_nugget").setRegistryName("metalextras:aluminum_nugget"));
		event.getRegistry().register(new Item().setCreativeTab(CreativeTabs.MATERIALS).setUnlocalizedName("metalextras:lead_nugget").setRegistryName("metalextras:lead_nugget"));
		event.getRegistry().register(new Item().setCreativeTab(CreativeTabs.MATERIALS).setUnlocalizedName("metalextras:silver_nugget").setRegistryName("metalextras:silver_nugget"));
		event.getRegistry().register(new Item().setCreativeTab(CreativeTabs.MATERIALS).setUnlocalizedName("metalextras:copper_ingot").setRegistryName("metalextras:copper_ingot"));
		event.getRegistry().register(new Item().setCreativeTab(CreativeTabs.MATERIALS).setUnlocalizedName("metalextras:tin_ingot").setRegistryName("metalextras:tin_ingot"));
		event.getRegistry().register(new Item().setCreativeTab(CreativeTabs.MATERIALS).setUnlocalizedName("metalextras:aluminum_ingot").setRegistryName("metalextras:aluminum_ingot"));
		event.getRegistry().register(new Item().setCreativeTab(CreativeTabs.MATERIALS).setUnlocalizedName("metalextras:lead_ingot").setRegistryName("metalextras:lead_ingot"));
		event.getRegistry().register(new Item().setCreativeTab(CreativeTabs.MATERIALS).setUnlocalizedName("metalextras:silver_ingot").setRegistryName("metalextras:silver_ingot"));
		event.getRegistry().register(new Item().setCreativeTab(CreativeTabs.MATERIALS).setUnlocalizedName("metalextras:ender_gem").setRegistryName("metalextras:ender_gem"));
		event.getRegistry().register(new Item().setCreativeTab(CreativeTabs.MATERIALS).setUnlocalizedName("metalextras:sapphire_gem").setRegistryName("metalextras:sapphire_gem"));
		event.getRegistry().register(new Item().setCreativeTab(CreativeTabs.MATERIALS).setUnlocalizedName("metalextras:ruby_gem").setRegistryName("metalextras:ruby_gem"));
		event.getRegistry().register(new ItemBlock(COPPER_BLOCK).setRegistryName(COPPER_BLOCK.getRegistryName()));
		event.getRegistry().register(new ItemBlock(TIN_BLOCK).setRegistryName(TIN_BLOCK.getRegistryName()));
		event.getRegistry().register(new ItemBlock(ALUMINUM_BLOCK).setRegistryName(ALUMINUM_BLOCK.getRegistryName()));
		event.getRegistry().register(new ItemBlock(LEAD_BLOCK).setRegistryName(LEAD_BLOCK.getRegistryName()));
		event.getRegistry().register(new ItemBlock(SILVER_BLOCK).setRegistryName(SILVER_BLOCK.getRegistryName()));
		event.getRegistry().register(new ItemBlock(ENDER_BLOCK).setRegistryName(ENDER_BLOCK.getRegistryName()));
		event.getRegistry().register(new ItemBlock(SAPPHIRE_BLOCK).setRegistryName(SAPPHIRE_BLOCK.getRegistryName()));
		event.getRegistry().register(new ItemBlock(RUBY_BLOCK).setRegistryName(RUBY_BLOCK.getRegistryName()));
	}

	@SubscribeEvent
	public static void onEnchantmentsRegister(RegistryEvent.Register<Enchantment> event)
	{
		List<Enchantment> enchantments = Lists.newArrayList();
		enchantments.add(new EnchantmentHotTouch().setRegistryName("metalextras:hot_to_the_touch"));
		event.getRegistry().registerAll(Iterables.toArray(enchantments, Enchantment.class));
	}

	@SubscribeEvent
	public static void onOreTypesRegister(RegistryEvent.Register<OreTypes> event)
	{
		OreTypes rocks = new OreTypes().setRegistryName("rocks");
		rocks.addOreType(new OreType.Impl(rocks, Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE), Characteristic.ROCKY, Characteristic.DENSE)
		{
			@Override
			public IModel getModel(ModelType model_type)
			{
				if(model_type == ModelType.EMERALD)
					return OreType.Impl.getModelFromTexture(new ResourceLocation("metalextras:blocks/emerald_stone"));
				return super.getModel(model_type);
			}
		}.setHardness(1.5F).setResistance(10F).setModelTexture(new ResourceLocation("minecraft:blocks/stone")).setRegistryName("minecraft:stone"));
		rocks.addOreType(new OreType.Impl(rocks, Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE), Characteristic.ROCKY, Characteristic.DENSE).setHardness(1.5F).setResistance(10F).setModelTexture(new ResourceLocation("minecraft:blocks/stone_granite")).setRegistryName("minecraft:granite"));
		rocks.addOreType(new OreType.Impl(rocks, Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE), Characteristic.ROCKY, Characteristic.DENSE).setHardness(1.5F).setResistance(10F).setModelTexture(new ResourceLocation("minecraft:blocks/stone_diorite")).setRegistryName("minecraft:diorite"));
		rocks.addOreType(new OreType.Impl(rocks, Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE), Characteristic.ROCKY, Characteristic.DENSE).setHardness(1.5F).setResistance(10F).setModelTexture(new ResourceLocation("minecraft:blocks/stone_andesite")).setRegistryName("minecraft:andesite"));
		rocks.addOreType(new OreType.Impl(rocks, Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.DEFAULT), Characteristic.ROCKY, Characteristic.SANDY, Characteristic.COMPACT, Characteristic.DRY).setHardness(.8F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/sandstone_normal")).setRegistryName("minecraft:sandstone"));
		rocks.addOreType(new OreType.Impl(rocks, Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.DEFAULT), Characteristic.ROCKY, Characteristic.SANDY, Characteristic.COMPACT, Characteristic.DRY).setHardness(.8F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/red_sandstone_normal")).setRegistryName("minecraft:red_sandstone"));
		rocks.addOreType(new OreType.Impl(rocks, Blocks.NETHERRACK.getDefaultState(), Characteristic.ROCKY, MetalExtras.OTD_NETHER, Characteristic.HOT, Characteristic.COMPACT).setHardness(.4F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/netherrack")).setRegistryName("minecraft:netherrack"));
		rocks.addOreType(new OreType.Impl(rocks, Blocks.END_STONE.getDefaultState(), Characteristic.ROCKY, MetalExtras.OTD_END, Characteristic.DENSE).setHardness(3F).setResistance(15F).setModelTexture(new ResourceLocation("minecraft:blocks/end_stone")).setRegistryName("minecraft:end_stone"));
		rocks.addOreType(new OreType.Impl(rocks, Blocks.BEDROCK.getDefaultState(), Characteristic.ROCKY, Characteristic.DENSE).setHardness(-1F).setResistance(6000000F).setModelTexture(new ResourceLocation("minecraft:blocks/bedrock")).setRegistryName("minecraft:bedrock"));
		event.getRegistry().register(rocks);
		OreTypes dirts = new OreTypes().setRegistryName("dirts");
		dirts.addOreType(new OreType.Impl(dirts, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), Characteristic.DIRTY, Characteristic.COMPACT).setHardness(.5F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/dirt")).setRegistryName("minecraft:dirt"));
		dirts.addOreType(new OreType.Impl(dirts, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT), Characteristic.DIRTY, Characteristic.ROCKY, Characteristic.COMPACT).setHardness(.5F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/coarse_dirt")).setRegistryName("minecraft:coarse_dirt"));
		dirts.addOreType(new OreType.Impl(dirts, Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.SAND), Characteristic.DIRTY, Characteristic.SANDY, Characteristic.LOOSE, Characteristic.DRY).setHardness(.5F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/sand")).setRegistryName("minecraft:sand"));
		dirts.addOreType(new OreType.Impl(dirts, Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND), Characteristic.DIRTY, Characteristic.SANDY, Characteristic.LOOSE, Characteristic.DRY).setHardness(.5F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/red_sand")).setRegistryName("minecraft:red_sand"));
		dirts.addOreType(new OreType.Impl(dirts, Blocks.CLAY.getDefaultState(), Characteristic.DIRTY, Characteristic.COMPACT, Characteristic.WET).setHardness(.6F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/clay")).setRegistryName("minecraft:clay"));
		dirts.addOreType(new OreType.Impl(dirts, Blocks.GRAVEL.getDefaultState(), Characteristic.DIRTY, Characteristic.ROCKY, Characteristic.LOOSE).setHardness(.6F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/gravel")).setRegistryName("minecraft:gravel"));
		dirts.addOreType(new OreType.Impl(dirts, Blocks.SOUL_SAND.getDefaultState(), Characteristic.DIRTY, Characteristic.SANDY, MetalExtras.OTD_NETHER)
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
		OreUtils.getTypesRegistry().clear();
		ModContainer previous_active_mod = Loader.instance().activeModContainer();
		Loader.instance().setActiveModContainer(null);
		for(ModContainer container : Loader.instance().getActiveModList())
			addModulesFromMod(container);
		Loader.instance().setActiveModContainer(previous_active_mod);
	}

	public static void addModulesFromMod(ModContainer container)
	{
		File source = ("minecraft".equals(container.getModId()) ? Loader.instance().getIndexedModList().get("metalextras") : container).getSource();
		try(FileSystem file_system = source.isFile() ? FileSystems.newFileSystem(source.toPath(), null) : null)
		{
			VariableManager.masterModuleStream().forEach((entry) ->
			{
				try
				{
					String format = String.format("assets/%s/ores/%s", container.getModId(), entry.getValue());
					Path root = file_system == null ? source.toPath().resolve(format) : file_system.getPath(format);
					if(Files.exists(root))
						Files.walk(root).forEach((path) ->
						{
							Loader.instance().setActiveModContainer(container);
							String relative_name = root.relativize(path).toString();
							if("json".equals(FilenameUtils.getExtension(relative_name)))
							{
								BufferedReader reader = null;
								try
								{
									reader = Files.newBufferedReader(path);
									VariableManager.newMasterModule(new ResourceLocation(container.getModId(), FilenameUtils.removeExtension(relative_name).replaceAll("\\\\", "/")), entry.getKey(), TypeAdapters.JSON_ELEMENT.fromJson(reader));
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
								finally
								{
									IOUtils.closeQuietly(reader);
								}
							}
						});
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onTexturesStitch(TextureStitchEvent.Pre event)
	{
		TextureMap map = event.getMap();
		class OreTexture extends TextureAtlasSprite
		{
			private final ResourceLocation foreground;
			private final ResourceLocation background;

			public OreTexture(ResourceLocation foreground, ResourceLocation background)
			{
				super(String.format("%s:ores/%s.%s_%s", foreground.getResourceDomain(), foreground.getResourcePath(), background.getResourceDomain(), background.getResourcePath()));
				this.foreground = foreground;
				this.background = background;
			}

			@Override
			public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location)
			{
				return true;
			}

			@Override
			public Collection<ResourceLocation> getDependencies()
			{
				return ImmutableList.of(this.background, this.foreground);
			}

			@Override
			public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter)
			{
				TextureAtlasSprite foreground = textureGetter.apply(this.foreground);
				TextureAtlasSprite background = textureGetter.apply(this.background);
				int[][] foreground_pixels = foreground.getFrameTextureData(0);
				int[][] background_pixels = background.getFrameTextureData(0);
				float foreground_pixel_increment;
				float background_pixel_increment;
				if(foreground_pixels[0].length >= background_pixels[0].length)
				{
					this.width = foreground.getIconWidth();
					this.height = foreground.getIconHeight();
					foreground_pixel_increment = 1F;
					background_pixel_increment = background.getIconWidth() / this.width;
				}
				else
				{
					this.width = background.getIconWidth();
					this.height = background.getIconHeight();
					foreground_pixel_increment = foreground.getIconWidth() / this.width;
					background_pixel_increment = 1F;
				}
				float foreground_pixel_x_subindex = 0;
				float foreground_pixel_y_subindex = 0;
				float background_pixel_x_subindex = 0;
				float background_pixel_y_subindex = 0;
				int[][] pixels = new int[Minecraft.getMinecraft().gameSettings.mipmapLevels + 1][];
				pixels[0] = new int[this.width * this.height];
				for(int pixel = 0; pixel < this.width * this.height; pixel++)
				{
					int foreground_pixel = foreground_pixels[0][pixel];
					pixels[0][pixel] = foreground_pixel == 0 ? background_pixels[0][pixel] : foreground_pixel;
				}
				for(int y = 0; y < this.height; y++)
				{
					for(int x = 0; x < this.width; x++)
					{
						int foreground_pixel = foreground_pixels[0][MathHelper.floor(foreground_pixel_y_subindex) * this.width + MathHelper.floor(foreground_pixel_x_subindex)];
						pixels[0][y * this.width + x] = foreground_pixel == 0 ? background_pixels[0][MathHelper.floor(background_pixel_y_subindex) * this.width + MathHelper.floor(background_pixel_x_subindex)] : foreground_pixel;
						foreground_pixel_x_subindex += foreground_pixel_increment;
						background_pixel_x_subindex += background_pixel_increment;
					}
					foreground_pixel_x_subindex = background_pixel_x_subindex = 0;
					foreground_pixel_y_subindex += foreground_pixel_increment;
					background_pixel_y_subindex += background_pixel_increment;
				}
				this.clearFramesTextureData();
				this.framesTextureData.add(pixels);
				return false;
			}
		}
		for(NewOreType material : OreUtils.getTypesRegistry())
			for(OreTypes types : OreUtils.getTypeCollectionsRegistry())
				for(OreType type : types)
					map.setTextureEntry(new OreTexture(material.getChildModule(TypeModelModule.class).getTexture(), type.getTexture()));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onModelsRegister(ModelRegistryEvent event)
	{
		ModelLoaderRegistry.registerLoader(new ICustomModelLoader()
		{
			private final ModelOre model = new ModelOre();

			@Override
			public void onResourceManagerReload(IResourceManager resourceManager)
			{
			}

			@Override
			public boolean accepts(ResourceLocation location)
			{
				return MetalExtras.MODID.equals(location.getResourceDomain()) && "models/block/ore".equals(location.getResourcePath());
			}

			@Override
			public IModel loadModel(ResourceLocation location) throws Exception
			{
				return this.model;
			}
		});
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.ENDER_SHOVEL, 0, new ModelResourceLocation("metalextras:ender_shovel", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.ENDER_PICKAXE, 0, new ModelResourceLocation("metalextras:ender_pickaxe", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.ENDER_AXE, 0, new ModelResourceLocation("metalextras:ender_axe", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.ENDER_HOE, 0, new ModelResourceLocation("metalextras:ender_hoe", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.SAPPHIRE_SHOVEL, 0, new ModelResourceLocation("metalextras:sapphire_shovel", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.SAPPHIRE_PICKAXE, 0, new ModelResourceLocation("metalextras:sapphire_pickaxe", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.SAPPHIRE_AXE, 0, new ModelResourceLocation("metalextras:sapphire_axe", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.SAPPHIRE_HOE, 0, new ModelResourceLocation("metalextras:sapphire_hoe", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.RUBY_SHOVEL, 0, new ModelResourceLocation("metalextras:ruby_shovel", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.RUBY_PICKAXE, 0, new ModelResourceLocation("metalextras:ruby_pickaxe", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.RUBY_AXE, 0, new ModelResourceLocation("metalextras:ruby_axe", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.RUBY_HOE, 0, new ModelResourceLocation("metalextras:ruby_hoe", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.COPPER_NUGGET, 0, new ModelResourceLocation("metalextras:copper_nugget", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.TIN_NUGGET, 0, new ModelResourceLocation("metalextras:tin_nugget", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.ALUMINUM_NUGGET, 0, new ModelResourceLocation("metalextras:aluminum_nugget", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.LEAD_NUGGET, 0, new ModelResourceLocation("metalextras:lead_nugget", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.SILVER_NUGGET, 0, new ModelResourceLocation("metalextras:silver_nugget", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.COPPER_INGOT, 0, new ModelResourceLocation("metalextras:copper_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.TIN_INGOT, 0, new ModelResourceLocation("metalextras:tin_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.ALUMINUM_INGOT, 0, new ModelResourceLocation("metalextras:aluminum_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.LEAD_INGOT, 0, new ModelResourceLocation("metalextras:lead_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.SILVER_INGOT, 0, new ModelResourceLocation("metalextras:silver_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.ENDER_GEM, 0, new ModelResourceLocation("metalextras:ender_gem", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.SAPPHIRE_GEM, 0, new ModelResourceLocation("metalextras:sapphire_gem", "inventory"));
		ModelLoader.setCustomModelResourceLocation(MetalExtras_Objects.RUBY_GEM, 0, new ModelResourceLocation("metalextras:ruby_gem", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MetalExtras_Objects.COPPER_BLOCK), 0, new ModelResourceLocation("metalextras:copper_block", "normal"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MetalExtras_Objects.TIN_BLOCK), 0, new ModelResourceLocation("metalextras:tin_block", "normal"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MetalExtras_Objects.ALUMINUM_BLOCK), 0, new ModelResourceLocation("metalextras:aluminum_block", "normal"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MetalExtras_Objects.LEAD_BLOCK), 0, new ModelResourceLocation("metalextras:lead_block", "normal"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MetalExtras_Objects.SILVER_BLOCK), 0, new ModelResourceLocation("metalextras:silver_block", "normal"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MetalExtras_Objects.ENDER_BLOCK), 0, new ModelResourceLocation("metalextras:ender_block", "normal"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MetalExtras_Objects.SAPPHIRE_BLOCK), 0, new ModelResourceLocation("metalextras:sapphire_block", "normal"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MetalExtras_Objects.RUBY_BLOCK), 0, new ModelResourceLocation("metalextras:ruby_block", "normal"));
		class OreStateMapper extends StateMapperBase implements ItemMeshDefinition
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return new ModelResourceLocation("metalextras:ore#normal");
			}

			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("metalextras:ore#normal");
			}
		}
		OreStateMapper mapper = new OreStateMapper();
		for(NewOreType material : OreUtils.getTypesRegistry())
		{
			for(BlockOre block : material.getBlocks())
			{
				ModelLoader.setCustomStateMapper(block, mapper);
				Item item = Item.getItemFromBlock(block);
				for(int i = 0; i < block.getOreTypeProperty().getAllowedValues().size(); i++)
					ModelLoader.setCustomModelResourceLocation(item, i, mapper.getModelLocation(new ItemStack(item, 1, i)));
				if(block instanceof IBlockOreMulti)
					for(BlockOre other : ((IBlockOreMulti)block).getOthers())
					{
						ModelLoader.setCustomStateMapper(other, mapper);
						item = Item.getItemFromBlock(other);
						if(item != null)
							for(int i = 0; i < other.getOreTypeProperty().getAllowedValues().size(); i++)
								ModelLoader.setCustomModelResourceLocation(item, i, mapper.getModelLocation(new ItemStack(item, 1, i)));
					}
			}
			ModelLoader.setCustomModelResourceLocation(OreUtils.ORE, OreUtils.getTypesRegistry().getValues().indexOf(material), new ModelResourceLocation(new ResourceLocation("metalextras", material.getRegistryName().getResourcePath() + "_item"), "inventory"));
		}
		Optional.of(Minecraft.getMinecraft().getResourceManager()).filter((manager) -> manager instanceof IReloadableResourceManager).ifPresent((manager) -> ((IReloadableResourceManager)manager).registerReloadListener(ModelOre::reload));
	}

	@SubscribeEvent
	public static void registerModuleFactories(RegisterModuleFactoriesEvent event)
	{
		VariableManager.registerMasterModuleFactory(NewOreType.class, "types", (name, json) ->
		{
			ModContainer container = Loader.instance().activeModContainer();
			NewOreType type = new NewOreType(String.format("types/%s", name), json.getAsJsonObject(), true);
			type.setRegistryName(name);
			OreUtils.getTypesRegistry().register(type);
			for(OreTypes types : OreUtils.getTypeCollectionsRegistry())
				for(BlockOre block : type.getBlocksToRegister(types))
				{
					ModContainer previous_mod = Loader.instance().activeModContainer();
					Loader.instance().setActiveModContainer(Loader.instance().getIndexedModList().get(type.getRegistryName().getResourceDomain()));
					Item item = new ItemBlockOre(block, block.getOreTypeProperty()).setRegistryName(block.getRegistryName());
					Loader.instance().setActiveModContainer("minecraft".equals(container.getModId()) ? Loader.instance().getIndexedModList().get("metalextras") : container);
					ForgeRegistries.BLOCKS.register(block);
					ForgeRegistries.ITEMS.register(item);
					Loader.instance().setActiveModContainer(previous_mod);
				}
			return type;
		});
		VariableManager.registerModuleFactory(BlockModule.class, "block", (path, json) -> new BlockModule(path, json, true));
		VariableManager.registerModuleFactory(GenerationModule.class, "generation", (path, json) -> new GenerationModule(path, json, true));
		VariableManager.registerModuleFactory(SmeltingModule.class, "smelting", (path, json) -> new SmeltingModule(path, json, true));
		VariableManager.registerModuleFactory(TypeModelModule.class, "model", (path, json) -> new TypeModelModule(path, json, true));
	}
}
