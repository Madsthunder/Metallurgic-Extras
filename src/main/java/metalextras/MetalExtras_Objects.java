package metalextras;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import api.metalextras.BlockOre;
import api.metalextras.Characteristic;
import api.metalextras.IBlockOreMulti;
import api.metalextras.ModelType;
import api.metalextras.OreType;
import api.metalextras.OreTypes;
import api.metalextras.OreUtils;
import metalextras.client.model.ModelOre;
import metalextras.enchantments.EnchantmentHotTouch;
import metalextras.items.ItemEnderHoe;
import metalextras.items.ItemEnderTool;
import metalextras.items.ItemHoe;
import metalextras.items.ItemOre;
import metalextras.items.ItemTool;
import metalextras.ores.VanillaOreMaterial;
import metalextras.ores.materials.OreMaterial;
import metalextras.ores.properties.ConfigurationOreProperties;
import metalextras.registry.MetalExtras_Callbacks;
import metalextras.world.gen.OreGeneration;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
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
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.RegistryBuilder;

@ObjectHolder(MetalExtras.MODID)
@EventBusSubscriber(modid = MetalExtras.MODID)
public class MetalExtras_Objects
{

    public static final VanillaOreMaterial COAL_ORE = null;
    public static final VanillaOreMaterial IRON_ORE = null;
    public static final VanillaOreMaterial LAPIS_ORE = null;
    public static final VanillaOreMaterial GOLD_ORE = null;
    public static final VanillaOreMaterial REDSTONE_ORE = null;
    public static final VanillaOreMaterial EMERALD_ORE = null;
    public static final VanillaOreMaterial DIAMOND_ORE = null;
    
    public static final OreMaterial.SimpleImpl COPPER_ORE = null;
    public static final OreMaterial.SimpleImpl TIN_ORE = null;
    public static final OreMaterial.SimpleImpl ALUMINUM_ORE = null;
    public static final OreMaterial.SimpleImpl LEAD_ORE = null;
    public static final OreMaterial.SimpleImpl SILVER_ORE = null;
    public static final OreMaterial.SimpleImpl ENDER_ORE = null;
    public static final OreMaterial.SimpleImpl SAPPHIRE_ORE = null;
    public static final OreMaterial.SimpleImpl RUBY_ORE = null;
    
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
        new RegistryBuilder().setType(OreTypes.class).setIDRange(0, Integer.MAX_VALUE >> 5).setName(new ResourceLocation("metalextras", "ore_type_collections")).addCallback(MetalExtras_Callbacks.ORE_TYPES).create();
        new RegistryBuilder().setType(OreMaterial.class).setIDRange(0, Integer.MAX_VALUE >> 5).setName(new ResourceLocation("metalextras", "ore_materials")).addCallback(MetalExtras_Callbacks.ORE_MATERIALS).create();
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
    
    @SuppressWarnings("unused")
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
        if(false)
        {
            event.getRegistry().register(new ItemShovel(2, 10F, 22, "ingotSilver", 4.5F, -3F, 59).setCreativeTab(CreativeTabs.TOOLS).setUnlocalizedName("metalextras:silver_shovel").setRegistryName("metalextras:silver_shovel"));
            event.getRegistry().register(new ItemTool("pickaxe", 2, 10F, 22, "ingotSilver", 4F, -2.8F, 59, Material.IRON, Material.ANVIL, Material.ROCK).setCreativeTab(CreativeTabs.TOOLS).setUnlocalizedName("metalextras:silver_pickaxe").setRegistryName("metalextras:silver_pickaxe"));
            event.getRegistry().register(new ItemTool("axe", 2, 10F, 22, "ingotSilver", 8F, -3F, 59, Material.WOOD, Material.PLANTS, Material.VINE).setCreativeTab(CreativeTabs.TOOLS).setUnlocalizedName("metalextras:silver_axe").setRegistryName("metalextras:silver_axe"));
            event.getRegistry().register(new ItemHoe("ingotSilver", true, 4.5F, 59).setCreativeTab(CreativeTabs.TOOLS).setUnlocalizedName("metalextras:silver_hoe").setRegistryName("metalextras:silver_hoe"));
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
        rocks.addOreType(new OreType.Impl(rocks, Blocks.STONE.getStateFromMeta(0), Characteristic.ROCKY, Characteristic.DENSE)
        {
            @Override
            public IModel getModel(OreMaterial material)
            {
                if(material.getModelType() == ModelType.EMERALD)
                    return OreType.Impl.getModelFromTexture(new ResourceLocation("metalextras:blocks/emerald_stone"));
                return super.getModel(material);
            }
        }.setHardness(1.5F).setResistance(10F).setModelTexture(new ResourceLocation("minecraft:blocks/stone")).setRegistryName("minecraft:stone"));
        rocks.addOreType(new OreType.Impl(rocks, Blocks.STONE.getStateFromMeta(1), Characteristic.ROCKY, Characteristic.DENSE).setHardness(1.5F).setResistance(10F).setModelTexture(new ResourceLocation("minecraft:blocks/stone_granite")).setRegistryName("minecraft:granite"));
        rocks.addOreType(new OreType.Impl(rocks, Blocks.STONE.getStateFromMeta(3), Characteristic.ROCKY, Characteristic.DENSE).setHardness(1.5F).setResistance(10F).setModelTexture(new ResourceLocation("minecraft:blocks/stone_diorite")).setRegistryName("minecraft:diorite"));
        rocks.addOreType(new OreType.Impl(rocks, Blocks.STONE.getStateFromMeta(5), Characteristic.ROCKY, Characteristic.DENSE).setHardness(1.5F).setResistance(10F).setModelTexture(new ResourceLocation("minecraft:blocks/stone_andesite")).setRegistryName("minecraft:andesite"));
        rocks.addOreType(new OreType.Impl(rocks, Blocks.SANDSTONE.getDefaultState(), Characteristic.ROCKY, Characteristic.SANDY, Characteristic.COMPACT, Characteristic.DRY).setHardness(.8F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/sandstone_normal")).setRegistryName("minecraft:sandstone"));
        rocks.addOreType(new OreType.Impl(rocks, Blocks.RED_SANDSTONE.getDefaultState(), Characteristic.ROCKY, Characteristic.SANDY, Characteristic.COMPACT, Characteristic.DRY).setHardness(.8F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/red_sandstone_normal")).setRegistryName("minecraft:red_sandstone"));
        rocks.addOreType(new OreType.Impl(rocks, Blocks.NETHERRACK.getDefaultState(), Characteristic.ROCKY, MetalExtras.OTD_NETHER, Characteristic.HOT, Characteristic.COMPACT).setHardness(.4F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/netherrack")).setRegistryName("minecraft:netherrack"));
        rocks.addOreType(new OreType.Impl(rocks, Blocks.END_STONE.getDefaultState(), Characteristic.ROCKY, MetalExtras.OTD_END, Characteristic.DENSE).setHardness(3F).setResistance(15F).setModelTexture(new ResourceLocation("minecraft:blocks/end_stone")).setRegistryName("minecraft:end_stone"));
        rocks.addOreType(new OreType.Impl(rocks, Blocks.BEDROCK.getDefaultState(), Characteristic.ROCKY, Characteristic.DENSE).setHardness(-1F).setResistance(6000000F).setModelTexture(new ResourceLocation("minecraft:blocks/bedrock")).setRegistryName("minecraft:bedrock"));
        event.getRegistry().register(rocks);
        OreTypes dirts = new OreTypes().setRegistryName("dirts");
        dirts.addOreType(new OreType.Impl(dirts, Blocks.DIRT.getStateFromMeta(0), Characteristic.DIRTY, Characteristic.COMPACT).setHardness(.5F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/dirt")).setRegistryName("minecraft:dirt"));
        dirts.addOreType(new OreType.Impl(dirts, Blocks.DIRT.getStateFromMeta(1), Characteristic.DIRTY, Characteristic.ROCKY, Characteristic.COMPACT).setHardness(.5F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/coarse_dirt")).setRegistryName("minecraft:coarse_dirt"));
        dirts.addOreType(new OreType.Impl(dirts, Blocks.SAND.getStateFromMeta(0), Characteristic.DIRTY, Characteristic.SANDY, Characteristic.LOOSE, Characteristic.DRY).setHardness(.5F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/sand")).setRegistryName("minecraft:sand"));
        dirts.addOreType(new OreType.Impl(dirts, Blocks.SAND.getStateFromMeta(1), Characteristic.DIRTY, Characteristic.SANDY, Characteristic.LOOSE, Characteristic.DRY).setHardness(.5F).setResistance(0F).setModelTexture(new ResourceLocation("minecraft:blocks/red_sand")).setRegistryName("minecraft:red_sand"));
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
    }
    
    @SubscribeEvent
    public static void onOreMaterialsRegister(RegistryEvent.Register<OreMaterial> event)
    {
        event.getRegistry().register(new OreMaterial.SimpleImpl(ConfigurationOreProperties.func("copper_ore", true, 20, 0, 64, -Float.MAX_VALUE, Float.MAX_VALUE, 9, Predicates.alwaysTrue())).setHarvestLevel(1).setItemDroppedAsOre().setCreativeTab(MetalExtras.METALLURGIC_EXTRAS).setOverrides(MetalExtras.COPPER_EVT).setLanguageKey("tile.metalextras:copper_ore").setTexture(new ResourceLocation("metalextras:items/copper_ore")).setRegistryName("metalextras:copper_ore"));
        event.getRegistry().register(new OreMaterial.SimpleImpl(ConfigurationOreProperties.func("tin_ore", true, 20, 0, 64, -Float.MAX_VALUE, Float.MAX_VALUE, 9, Characteristic.notAny(MetalExtras.OTD_END))).setHarvestLevel(1).setItemDroppedAsOre().setCreativeTab(MetalExtras.METALLURGIC_EXTRAS).setOverrides(MetalExtras.TIN_EVT).setLanguageKey("tile.metalextras:tin_ore").setTexture(new ResourceLocation("metalextras:items/tin_ore")).setRegistryName("metalextras:tin_ore"));
        event.getRegistry().register(new OreMaterial.SimpleImpl(ConfigurationOreProperties.func("aluminum_ore", true, 6, 32, 128, -Float.MAX_VALUE, Float.MAX_VALUE, 5, Characteristic.all(Characteristic.ROCKY))).setHarvestLevel(1).setItemDroppedAsOre().setCreativeTab(MetalExtras.METALLURGIC_EXTRAS).setOverrides(MetalExtras.ALUMINUM_EVT).setLanguageKey("tile.metalextras:aluminum_ore").setTexture(new ResourceLocation("metalextras:items/aluminum_ore")).setRegistryName("metalextras:aluminum_ore"));
        event.getRegistry().register(new OreMaterial.SimpleImpl(ConfigurationOreProperties.func("lead_ore", true, 8, 32, 64, -Float.MAX_VALUE, Float.MAX_VALUE, 8, Characteristic.notAny(Characteristic.DIRTY, MetalExtras.OTD_END))).setHarvestLevel(2).setItemDroppedAsOre().setCreativeTab(MetalExtras.METALLURGIC_EXTRAS).setOverrides(MetalExtras.LEAD_EVT).setLanguageKey("tile.metalextras:lead_ore").setTexture(new ResourceLocation("metalextras:items/lead_ore")).setRegistryName("metalextras:lead_ore"));
        event.getRegistry().register(new OreMaterial.SimpleImpl(ConfigurationOreProperties.func("silver_ore", true, 8, 0, 32, -Float.MAX_VALUE, Float.MAX_VALUE, 8, Characteristic.notAny(Characteristic.DIRTY, Characteristic.SANDY, MetalExtras.OTD_END))).setHarvestLevel(2).setItemDroppedAsOre().setCreativeTab(MetalExtras.METALLURGIC_EXTRAS).setOverrides(MetalExtras.SILVER_EVT).setLanguageKey("tile.metalextras:silver_ore").setTexture(new ResourceLocation("metalextras:items/silver_ore")).setRegistryName("metalextras:silver_ore"));
        event.getRegistry().register(new OreMaterial.SimpleImpl(ConfigurationOreProperties.func("ender_ore", true, 20, 0, 64, -Float.MAX_VALUE, Float.MAX_VALUE, 9, Characteristic.all(MetalExtras.OTD_END))).setHarvestLevel(3).setItemDropped(ENDER_GEM, 0, 3, 7).setCreativeTab(MetalExtras.METALLURGIC_EXTRAS).setOverrides(MetalExtras.ENDER_EVT).setLanguageKey("tile.metalextras:ender_ore").setTexture(new ResourceLocation("metalextras:items/ender_ore")).setRegistryName("metalextras:ender_ore"));
        event.getRegistry().register(new OreMaterial.SimpleImpl(ConfigurationOreProperties.func("sapphire_ore", true, 20, 0, 64, -Float.MAX_VALUE, 0.2F, 3, Characteristic.notAny(Characteristic.LOOSE, Characteristic.DRY, Characteristic.SANDY, Characteristic.HOT, MetalExtras.OTD_NETHER))).setHarvestLevel(3).setItemDropped(SAPPHIRE_GEM, 0, 3, 7).setCreativeTab(MetalExtras.METALLURGIC_EXTRAS).setOverrides(MetalExtras.SAPPHIRE_EVT).setLanguageKey("tile.metalextras:sapphire_ore").setTexture(new ResourceLocation("metalextras:items/sapphire_ore")).setRegistryName("metalextras:sapphire_ore"));
        event.getRegistry().register(new OreMaterial.SimpleImpl(ConfigurationOreProperties.func("ruby_ore", true, 20, 0, 64, 1F, Float.MAX_VALUE, 3, new Predicate<Collection<Characteristic>>()
        {
            @Override
            public boolean apply(Collection<Characteristic> characteristics)
            {
                if((characteristics.contains(Characteristic.DIRTY)))
                    if(characteristics.contains(Characteristic.LOOSE))
                        return characteristics.contains(Characteristic.SANDY) && characteristics.contains(Characteristic.DRY);
                    else if(characteristics.contains(Characteristic.SANDY) || characteristics.contains(Characteristic.WET))
                        return false;
                return !characteristics.contains(MetalExtras.OTD_END);
            }
        })).setHarvestLevel(3).setItemDropped(RUBY_GEM, 0, 3, 7).setOverrides(MetalExtras.RUBY_EVT).setCreativeTab(MetalExtras.METALLURGIC_EXTRAS).setLanguageKey("tile.metalextras:ruby_ore").setTexture(new ResourceLocation("metalextras:items/ruby_ore")).setModel(new ResourceLocation("metalextras:block/ruby_ore")).setRegistryName("metalextras", "ruby_ore"));
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
                ChunkGeneratorSettings settings = OreGeneration.getChunkProviderSettings(world);
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
                ChunkGeneratorSettings settings = OreGeneration.getChunkProviderSettings(world);
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
                ChunkGeneratorSettings settings = OreGeneration.getChunkProviderSettings(world);
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
                ChunkGeneratorSettings settings = OreGeneration.getChunkProviderSettings(world);
                return new int[] { settings.goldMinHeight, settings.goldMaxHeight };
            }
            
            @Override
            public int getVeinSize(BiomeDecorator decorator)
            {
                return OreGeneration.getChunkProviderSettings(decorator).goldSize;
            }
        }.setRegistryName("metalextras:gold_ore"));
        event.getRegistry().register(new VanillaOreMaterial(Blocks.REDSTONE_ORE.getDefaultState(), 0, 0, ModelType.IRON, EventType.REDSTONE, true)
        {
            @Override
            public int getSpawnTries(World world, Random random)
            {
                return OreGeneration.getChunkProviderSettings(world).redstoneCount;
            }
            
            @Override
            public int[] getSpawnParams(World world)
            {
                ChunkGeneratorSettings settings = OreGeneration.getChunkProviderSettings(world);
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
                ChunkGeneratorSettings settings = OreGeneration.getChunkProviderSettings(world);
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
                ChunkGeneratorSettings settings = OreGeneration.getChunkProviderSettings(world);
                return new int[] { settings.diamondMinHeight, settings.diamondMaxHeight };
            }
            
            @Override
            public int getVeinSize(BiomeDecorator decorator)
            {
                return OreGeneration.getChunkProviderSettings(decorator).diamondSize;
            }
        }.setRegistryName("metalextras:diamond_ore"));
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onTexturesStitch(TextureStitchEvent.Pre event)
    {
        TextureMap map = event.getMap();
        class CombinedTexture extends TextureAtlasSprite
        {
            private final ResourceLocation foreground;
            private final ResourceLocation background;
            
            public CombinedTexture(ResourceLocation foreground, ResourceLocation background)
            {
                super(OreUtils.getTextureName(foreground, background).toString());
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
                for (int pixel = 0; pixel < this.width * this.height; pixel++)
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
        class MaterialAverage extends TextureAtlasSprite
        {
            private final ResourceLocation material;
            
            public MaterialAverage(ResourceLocation material)
            {
                super(material.toString() + "_average");
                this.material = material;
            }

            @Override
            public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location)
            {
                return true;
            }
            
            @Override
            public Collection<ResourceLocation> getDependencies()
            {
                return ImmutableList.of(this.material);
            }

            @Override
            public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter)
            {
                this.width = this.height = 1;
                int count = 0;
                long red = 0;
                long green = 0;
                long blue = 0;
                for(int color : textureGetter.apply(this.material).getFrameTextureData(0)[0])
                    if(color != 0)
                    {
                        count++;
                        red += (color >> 16) & 0xFF;
                        green += (color >> 8) & 0xFF;
                        blue += (color >> 0) & 0xFF;
                    }
                int[][] pixels = new int[Minecraft.getMinecraft().gameSettings.mipmapLevels + 1][];
                pixels[0] = new int[] { (((int)(red / count) & 0xFF) << 16) | (((int)(green / count) & 0xFF) << 8) | (((int)(blue / count) & 0xFF) << 0) };
                this.clearFramesTextureData();
                this.framesTextureData.add(pixels);
                return false;
            }
        }
        for(OreMaterial material : OreUtils.getMaterialsRegistry())
            for(OreTypes types : OreUtils.getTypeCollectionsRegistry())
                for(OreType type : types)
                    map.setTextureEntry(new CombinedTexture(material.getTexture(), type.getTexture()));
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
        if(false)
        {
            ModelLoader.setCustomModelResourceLocation(SILVER_SHOVEL, 0, new ModelResourceLocation("metalextras:silver_shovel", "inventory"));
            ModelLoader.setCustomModelResourceLocation(SILVER_PICKAXE, 0, new ModelResourceLocation("metalextras:silver_pickaxe", "inventory"));
            ModelLoader.setCustomModelResourceLocation(SILVER_AXE, 0, new ModelResourceLocation("metalextras:silver_axe", "inventory"));
        }
        ModelLoader.setCustomModelResourceLocation(ENDER_SHOVEL, 0, new ModelResourceLocation("metalextras:ender_shovel", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ENDER_PICKAXE, 0, new ModelResourceLocation("metalextras:ender_pickaxe", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ENDER_AXE, 0, new ModelResourceLocation("metalextras:ender_axe", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ENDER_HOE, 0, new ModelResourceLocation("metalextras:ender_hoe", "inventory"));
        ModelLoader.setCustomModelResourceLocation(SAPPHIRE_SHOVEL, 0, new ModelResourceLocation("metalextras:sapphire_shovel", "inventory"));
        ModelLoader.setCustomModelResourceLocation(SAPPHIRE_PICKAXE, 0, new ModelResourceLocation("metalextras:sapphire_pickaxe", "inventory"));
        ModelLoader.setCustomModelResourceLocation(SAPPHIRE_AXE, 0, new ModelResourceLocation("metalextras:sapphire_axe", "inventory"));
        ModelLoader.setCustomModelResourceLocation(SAPPHIRE_HOE, 0, new ModelResourceLocation("metalextras:sapphire_hoe", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RUBY_SHOVEL, 0, new ModelResourceLocation("metalextras:ruby_shovel", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RUBY_PICKAXE, 0, new ModelResourceLocation("metalextras:ruby_pickaxe", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RUBY_AXE, 0, new ModelResourceLocation("metalextras:ruby_axe", "inventory"));
        ModelLoader.setCustomModelResourceLocation(RUBY_HOE, 0, new ModelResourceLocation("metalextras:ruby_hoe", "inventory"));
        ModelLoader.setCustomModelResourceLocation(COPPER_NUGGET, 0, new ModelResourceLocation("metalextras:copper_nugget", "inventory"));
        ModelLoader.setCustomModelResourceLocation(TIN_NUGGET, 0, new ModelResourceLocation("metalextras:tin_nugget", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ALUMINUM_NUGGET, 0, new ModelResourceLocation("metalextras:aluminum_nugget", "inventory"));
        ModelLoader.setCustomModelResourceLocation(LEAD_NUGGET, 0, new ModelResourceLocation("metalextras:lead_nugget", "inventory"));
        ModelLoader.setCustomModelResourceLocation(SILVER_NUGGET, 0, new ModelResourceLocation("metalextras:silver_nugget", "inventory"));
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
        for(OreMaterial material : OreUtils.getMaterialsRegistry())
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
            ModelBakery.registerItemVariants(OreMaterial.ORE, new ModelResourceLocation(String.format("%s_item#inventory", material.getRegistryName().toString())));
        }
        ModelLoader.setCustomMeshDefinition(OreMaterial.ORE, new ItemMeshDefinition()
        {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return new ModelResourceLocation(String.format("%s_item#inventory", OreUtils.getMaterialsRegistry().getValues().get(stack.getMetadata()).getRegistryName().toString()));
            }
        });
    }
}
