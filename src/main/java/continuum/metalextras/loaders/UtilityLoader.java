package continuum.metalextras.loaders;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import continuum.api.metalextras.IOre;
import continuum.api.metalextras.IOreData;
import continuum.api.metalextras.IOreGroup;
import continuum.api.metalextras.IOreType;
import continuum.api.metalextras.OrePredicate;
import continuum.essentials.helpers.ItemHooks;
import continuum.essentials.mod.CTMod;
import continuum.essentials.mod.ObjectLoader;
import continuum.essentials.util.CreativeTab;
import continuum.metalextras.blocks.BlockOre;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import continuum.metalextras.world.gen.OreGeneration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
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
		holder.oreTypes.add(new IOreType.Impl("granite", "rock", Blocks.STONE.getStateFromMeta(1), 1.5F, 10F));
		holder.oreTypes.add(new IOreType.Impl("diorite", "rock", Blocks.STONE.getStateFromMeta(3), 1.5F, 10F));
		holder.oreTypes.add(new IOreType.Impl("andesite", "rock", Blocks.STONE.getStateFromMeta(5), 1.5F, 10F));
		holder.oreTypes.add(new IOreType.Impl("sandstone", "rock", Blocks.SANDSTONE.getDefaultState(), .8F, 0F));
		holder.oreTypes.add(new IOreType.Impl("red_sandstone", "rock", Blocks.RED_SANDSTONE.getDefaultState(), .8F, 0F));
		holder.oreTypes.add(new IOreType.Impl("netherrack", "rock", Blocks.NETHERRACK.getDefaultState(), .4F, 0F));
		holder.oreTypes.add(new IOreType.Impl("end_stone", "rock", Blocks.END_STONE.getDefaultState(), 3F, 15F));
		holder.oreTypes.add(new IOreType.Impl("bedrock", "rock", Blocks.BEDROCK.getDefaultState(), -1F, 6000000F));
		holder.oreTypes.add(new IOreType.Impl("dirt", "ground", Blocks.DIRT.getStateFromMeta(0), .5F, 0F));
		holder.oreTypes.add(new IOreType.Impl("coarse_dirt", "ground", Blocks.DIRT.getStateFromMeta(1), .5F, 0F));
		holder.oreTypes.add(new IOreType.Impl("sand", "ground", Blocks.SAND.getStateFromMeta(0), .5F, 0F));
		holder.oreTypes.add(new IOreType.Impl("red_sand", "ground", Blocks.SAND.getStateFromMeta(1), .5F, 0F));
		holder.oreTypes.add(new IOreType.Impl("clay", "ground", Blocks.CLAY.getDefaultState(), .6F, 0F));
		holder.oreTypes.add(new IOreType.Impl("gravel", "ground", Blocks.GRAVEL.getDefaultState(), .6F, 0F));
		holder.oreTypes.add(new IOreType.Impl("soul_sand", "ground", Blocks.SOUL_SAND.getDefaultState(), .5F, 0F)
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
		holder.oreGroups.add(new IOreGroup.Impl("rock"));
		holder.oreGroups.add(new IOreGroup.Impl("ground"));
		holder.orePredicates = Maps.newHashMap();
		holder.ingotList = Maps.newHashMap();
		holder.ores.add(new IOreData.Impl("metalextras:copper_ore", 1, "metalextras:copper_ingot", 0, false, 20, 9, 0, 64));
		holder.ores.add(new IOreData.Impl("metalextras:tin_ore", 1, "metalextras:tin_ingot", 0, false, 20, 9, 0, 64, Pair.of("minecraft", "end_stone")));
		holder.ores.add(new IOreData.Impl("metalextras:aluminum_ore", 1, "metalextras:aluminum_ingot", 0, false, 6, 5, 32, 128, Pair.of("minecraft", "dirt"), Pair.of("minecraft", "sand"), Pair.of("minecraft", "red_sand"), Pair.of("minecraft", "clay"), Pair.of("minecraft", "gravel"), Pair.of("minecraft", "soul_sand")));
		holder.ores.add(new IOreData.Impl("metalextras:lead_ore", 2, "metalextras:lead_ingot", 0, false, 8, 8, 32, 64, Pair.of("minecraft", "end_stone"), Pair.of("minecraft", "dirt"), Pair.of("minecraft", "coarse_dirt"), Pair.of("minecraft", "sand"), Pair.of("minecraft", "red_sand"), Pair.of("minecraft", "clay"), Pair.of("minecraft", "gravel"), Pair.of("minecraft", "soul_sand")));
		holder.ores.add(new IOreData.Impl("metalextras:silver_ore", 2, "metalextras:silver_ingot", 0, false, 8, 8, 0, 32, Pair.of("minecraft", "sandstone"), Pair.of("minecraft", "red_sandstone"), Pair.of("minecraft", "end_stone"), Pair.of("minecraft", "dirt"), Pair.of("minecraft", "coarse_dirt"), Pair.of("minecraft", "sand"), Pair.of("minecraft", "red_sand"), Pair.of("minecraft", "clay"), Pair.of("minecraft", "gravel"), Pair.of("minecraft", "soul_sand")));
		holder.ores.add(new IOreData.Impl("metalextras:sapphire_ore", 3, "metalextras:sapphire_gem", 0, false, 20, 3, 0, 64, Pair.of("minecraft", "sandstone"), Pair.of("minecraft", "red_sandstone"), Pair.of("minecraft", "netherrack"), Pair.of("minecraft", "coarse_dirt"), Pair.of("minecraft", "sand"), Pair.of("minecraft", "red_sand"), Pair.of("minecraft", "gravel"), Pair.of("minecraft", "soul_sand"), Pair.of( "temperatureLessThanOrEqualTo", 0.2D), Pair.of("canSpawnInEnd", true)));
		holder.ores.add(new IOreData.Impl("metalextras:ruby_ore", 3, "metalextras:ruby_gem", 0, false, 20, 3, 0, 64, Pair.of("minecraft", "end_stone"), Pair.of("minecraft", "clay"), Pair.of("minecraft", "gravel"), Pair.of("minecraft", "soul_sand"), Pair.of("temperatureGreaterThanOrEqualTo", 1.0D),  Pair.of("canSpawnInNether", true)));
		holder.ores.add(new IOreData.Impl("metalextras:mystery_ore", 3, "metalextras:ender_gem", 0, false, 20, 9, 0, 64, Pair.of("minecraft", "stone"), Pair.of("minecraft", "granite"), Pair.of("minecraft", "diorite"), Pair.of("minecraft", "andesite"), Pair.of("minecraft", "sandstone"), Pair.of("minecraft", "red_sandstone"), Pair.of("minecraft", "netherrack"), Pair.of("minecraft", "bedrock")));
		holder.ores.add(new IOreData.Impl("coal_ore", 0, "coal", 1, 1, 0, 2, 0, 2, true, 20, 17, 0, 128));
		holder.ores.add(new IOreData.Impl("iron_ore", 1, "iron_ore", true, 20, 9, 0, 64));
		holder.ores.add(new IOreData.Impl("lapis_ore", 1, "dye", 4, 8, EnumDyeColor.BLUE.getDyeDamage(), 2, 2, 5, true, 1, 7, 0, 32));
		holder.ores.add(new IOreData.Impl("gold_ore", 2, "gold_ore", true, 2, 9, 0, 32));
		holder.ores.add(new IOreData.Impl("redstone_ore", 2, "redstone", 4, 5, 0, 1, 1, 5, true, 8, 8, 0, 16));
		holder.ores.add(new IOreData.Impl("emerald_ore", 2, "emerald", 1, 1, 0, 2, 3, 7, true, 3, 1, 0, 28));
		holder.ores.add(new IOreData.Impl("diamond_ore", 2, "diamond", 1, 1, 0, 2, 3, 7, true, 1, 8, 0, 16));
		holder.oresToReplace = Lists.newArrayList();
	}
	
	@Override
	public void pre(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		MetalExtras_OH holder = mod.getObjectHolder();
		holder.tabOres = new CreativeTab("ores", holder.ender_gem);
		for(IOreData data : holder.ores)
		{
			this.addOreToList(holder, data);
			for(BlockOre ore : data.getOre().getOreBlocks())
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
	public void init(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		MetalExtras_OH holder = mod.getObjectHolder();
		GameRegistry.registerWorldGenerator(holder.oreGenerator = new OreGeneration(holder), 100);
		for(IOreData data : MetalExtras_OH.ores)
		{
			MetalExtras_EH.chunkGenerated.put(data, Lists.newArrayList());
		}
	}

	@Override
	public void post(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		
	}
	
	public IOre addOreToList(MetalExtras_OH holder, IOreData data)
	{
		List<BlockOre> ores = Lists.newArrayList();
		for(IOreGroup group : MetalExtras_OH.oreGroups)
		{
			BlockOre block = new BlockOre(holder, data, group);
			ForgeRegistries.BLOCKS.register(block);
			ItemHooks.registerItemBlockMeta(block, block.getOreTypeProperty().getAllowedValues().size() - 1);
			ores.add(block);
		}
		if(data.shouldReplaceExisting()) holder.oresToReplace.add(data.getOreName());
		IOre ore = new IOre.Impl(holder, data.getOreName(), ores.toArray(new BlockOre[0]));
		holder.ingotList.put(data.getOreName(), data.getIngot());
		data.setOre(ore);
		return ore;
	}
	
	@Override
	public String getName()
	{
		return "Utilities";
	}
}