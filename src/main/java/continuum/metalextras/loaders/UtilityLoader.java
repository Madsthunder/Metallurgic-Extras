package continuum.metalextras.loaders;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import continuum.api.metalextras.BlockOre;
import continuum.api.metalextras.OreCategory;
import continuum.api.metalextras.OreMaterial;
import continuum.api.metalextras.OreType;
import continuum.essentials.hooks.ItemHooks;
import continuum.essentials.mod.CTMod;
import continuum.essentials.mod.ObjectLoader;
import continuum.essentials.util.CreativeTab;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import continuum.metalextras.world.gen.OreGeneration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class UtilityLoader implements ObjectLoader<MetalExtras_OH, MetalExtras_EH>
{
	
	@Override
	public void construction(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		MetalExtras_OH holder = mod.getObjectHolder();
		holder.oreCategories.add(holder.rock_category = new OreCategory().setRegistryName("rock"));
		holder.rock_category.addOreType(new OreType("granite", Blocks.STONE.getStateFromMeta(1), 1.5F, 10F));
		holder.rock_category.addOreType(new OreType("diorite", Blocks.STONE.getStateFromMeta(3), 1.5F, 10F));
		holder.rock_category.addOreType(new OreType("andesite", Blocks.STONE.getStateFromMeta(5), 1.5F, 10F));
		holder.rock_category.addOreType(new OreType("sandstone", Blocks.SANDSTONE.getDefaultState(), .8F, 0F));
		holder.rock_category.addOreType(new OreType("red_sandstone", Blocks.RED_SANDSTONE.getDefaultState(), .8F, 0F));
		holder.rock_category.addOreType(new OreType("netherrack", Blocks.NETHERRACK.getDefaultState(), .4F, 0F));
		holder.rock_category.addOreType(new OreType("end_stone", Blocks.END_STONE.getDefaultState(), 3F, 15F));
		holder.rock_category.addOreType(new OreType("bedrock", Blocks.BEDROCK.getDefaultState(), -1F, 6000000F));
		holder.oreCategories.add(holder.ground_category = new OreCategory().setRegistryName("ground"));
		holder.ground_category.addOreType(new OreType("dirt", Blocks.DIRT.getStateFromMeta(0), .5F, 0F));
		holder.ground_category.addOreType(new OreType("coarse_dirt", Blocks.DIRT.getStateFromMeta(1), .5F, 0F));
		holder.ground_category.addOreType(new OreType("sand", Blocks.SAND.getStateFromMeta(0), .5F, 0F));
		holder.ground_category.addOreType(new OreType("red_sand", Blocks.SAND.getStateFromMeta(1), .5F, 0F));
		holder.ground_category.addOreType(new OreType("clay", Blocks.CLAY.getDefaultState(), .6F, 0F));
		holder.ground_category.addOreType(new OreType("gravel", Blocks.GRAVEL.getDefaultState(), .6F, 0F));
		holder.ground_category.addOreType(new OreType("soul_sand", Blocks.SOUL_SAND.getDefaultState(), .5F, 0F)
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
		});
		holder.ingotList = Maps.newHashMap();
		holder.ores.add(new OreMaterial(1, "metalextras:copper_ingot", 0, false, 20, 1, 9, 0, 64, Lists.newArrayList()).setRegistryName("metalextras:copper_ore"));
		holder.ores.add(new OreMaterial(1, "metalextras:tin_ingot", 0, false, 20, 1, 9, 0, 64, Lists.newArrayList(Pair.of("minecraft", "end_stone"))).setRegistryName("metalextras:tin_ore"));
		holder.ores.add(new OreMaterial(1, "metalextras:aluminum_ingot", 0, false, 6, 1, 5, 32, 128, Lists.newArrayList(Pair.of("minecraft", "dirt"), Pair.of("minecraft", "sand"), Pair.of("minecraft", "red_sand"), Pair.of("minecraft", "clay"), Pair.of("minecraft", "gravel"), Pair.of("minecraft", "soul_sand"))).setRegistryName("metalextras:aluminum_ore"));
		holder.ores.add(new OreMaterial(2, "metalextras:lead_ingot", 0, false, 8, 1, 8, 32, 64, Lists.newArrayList(Pair.of("minecraft", "end_stone"), Pair.of("minecraft", "dirt"), Pair.of("minecraft", "coarse_dirt"), Pair.of("minecraft", "sand"), Pair.of("minecraft", "red_sand"), Pair.of("minecraft", "clay"), Pair.of("minecraft", "gravel"), Pair.of("minecraft", "soul_sand"))).setRegistryName("metalextras:lead_ore"));
		holder.ores.add(new OreMaterial(2, "metalextras:silver_ingot", 0, false, 8, 1, 8, 0, 32, Lists.newArrayList(Pair.of("minecraft", "sandstone"), Pair.of("minecraft", "red_sandstone"), Pair.of("minecraft", "end_stone"), Pair.of("minecraft", "dirt"), Pair.of("minecraft", "coarse_dirt"), Pair.of("minecraft", "sand"), Pair.of("minecraft", "red_sand"), Pair.of("minecraft", "clay"), Pair.of("minecraft", "gravel"), Pair.of("minecraft", "soul_sand"))).setRegistryName("metalextras:silver_ore"));
		holder.ores.add(new OreMaterial(3, "metalextras:sapphire_gem", 0, false, 20, 1, 3, 0, 64, Lists.newArrayList(Pair.of("minecraft", "sandstone"), Pair.of("minecraft", "red_sandstone"), Pair.of("minecraft", "netherrack"), Pair.of("minecraft", "coarse_dirt"), Pair.of("minecraft", "sand"), Pair.of("minecraft", "red_sand"), Pair.of("minecraft", "gravel"), Pair.of("minecraft", "soul_sand"), Pair.of("temperature_<=_to", 0.2D), Pair.of("canSpawnInEnd", true))).setRegistryName("metalextras:sapphire_ore"));
		holder.ores.add(new OreMaterial(3, "metalextras:ruby_gem", 0, false, 20, 1, 3, 0, 64, Lists.newArrayList(Pair.of("minecraft", "end_stone"), Pair.of("minecraft", "clay"), Pair.of("minecraft", "gravel"), Pair.of("minecraft", "soul_sand"), Pair.of("temperature_>=_to", 1.0D), Pair.of("canSpawnInNether", true))).setRegistryName("metalextras:ruby_ore"));
		holder.ores.add(new OreMaterial(3, "metalextras:ender_gem", 0, false, 20, 1, 9, 0, 64, Lists.newArrayList(Pair.of("minecraft", "stone"), Pair.of("minecraft", "granite"), Pair.of("minecraft", "diorite"), Pair.of("minecraft", "andesite"), Pair.of("minecraft", "sandstone"), Pair.of("minecraft", "red_sandstone"), Pair.of("minecraft", "netherrack"), Pair.of("minecraft", "bedrock"))).setRegistryName("metalextras:mystery_ore"));
		holder.ores.add(new OreMaterial(0, "coal", 1, 1, 0, 2, 0, 2, true, 20, 1, 17, 0, 128, Lists.newArrayList()).setRegistryName("minecraft:coal_ore"));
		holder.ores.add(new OreMaterial(1, "iron_ore", true, 20, 1, 9, 0, 64, Lists.newArrayList()).setRegistryName("minecraft:iron_ore"));
		holder.ores.add(new OreMaterial(1, "dye", 4, 8, EnumDyeColor.BLUE.getDyeDamage(), 2, 2, 5, true, 1, 1, 7, 0, 32, Lists.newArrayList()).setRegistryName("minecraft:lapis_ore"));
		holder.ores.add(new OreMaterial(2, "gold_ore", true, 2, 1, 9, 0, 32, Lists.newArrayList()).setRegistryName("minecraft:gold_ore"));
		holder.ores.add(new OreMaterial(2, "redstone", 4, 5, 0, 1, 1, 5, true, 8, 1, 8, 0, 16, Lists.newArrayList()).setRegistryName("minecraft:redstone_ore"));
		holder.ores.add(new OreMaterial(2, "emerald", 1, 1, 0, 2, 3, 7, true, 3, 1, 1, 0, 28, Lists.newArrayList()).setRegistryName("minecraft:emerald_ore"));
		holder.ores.add(new OreMaterial(2, "diamond", 1, 1, 0, 2, 3, 7, true, 1, 1, 8, 0, 16, Lists.newArrayList()).setRegistryName("minecraft:diamond_ore"));
		holder.oresToReplace = Lists.newArrayList();
	}
	
	@Override
	public void pre(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		MetalExtras_OH holder = mod.getObjectHolder();
		holder.tabOres = new CreativeTab("ores", holder.ender_gem);
		for(OreMaterial material : holder.ores)
		{
			if(material.shouldReplaceExisting())
				holder.oresToReplace.add(material.getRegistryName());
			for(BlockOre ore : material.getBlocks())
				ore.setCreativeTab(holder.tabOres);
		}
		holder.copper_block.setCreativeTab(holder.tabOres);
		holder.tin_block.setCreativeTab(holder.tabOres);
		holder.aluminum_block.setCreativeTab(holder.tabOres);
		holder.lead_block.setCreativeTab(holder.tabOres);
		holder.silver_block.setCreativeTab(holder.tabOres);
		holder.ender_block.setCreativeTab(holder.tabOres);
		holder.sapphire_block.setCreativeTab(holder.tabOres);
		holder.ruby_block.setCreativeTab(holder.tabOres);
		
		holder.ore.setCreativeTab(holder.tabOres);
		holder.copper_ingot.setCreativeTab(holder.tabOres);
		holder.tin_ingot.setCreativeTab(holder.tabOres);
		holder.aluminum_ingot.setCreativeTab(holder.tabOres);
		holder.lead_ingot.setCreativeTab(holder.tabOres);
		holder.silver_ingot.setCreativeTab(holder.tabOres);
		holder.ender_gem.setCreativeTab(holder.tabOres);
		holder.sapphire_gem.setCreativeTab(holder.tabOres);
		holder.ruby_gem.setCreativeTab(holder.tabOres);
		
	}
	
	@Override
	public void post(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		MetalExtras_OH holder = mod.getObjectHolder();
		GameRegistry.registerWorldGenerator(holder.oreGenerator = new OreGeneration(holder), 100);
		for(OreMaterial material : MetalExtras_OH.ores)
			MetalExtras_EH.chunkGenerated.put(material, Sets.newHashSet());
	}
	
	public void addOreToList(MetalExtras_OH holder, OreMaterial material)
	{
	}
	
	@Override
	public String getName()
	{
		return "Utilities";
	}
}