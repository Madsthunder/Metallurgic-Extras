package continuum.metalextras.loaders;

import continuum.essentials.mod.CTMod;
import continuum.essentials.mod.ObjectLoader;
import continuum.metalextras.blocks.BlockCompressed;
import continuum.metalextras.blocks.BlockOreGround;
import continuum.metalextras.blocks.BlockOreRock;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BlockLoader implements ObjectLoader<MetalExtras_OH, MetalExtras_EH>
{

	@Override
	public void construction(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		
	}
	
	@Override
	public void pre(CTMod<MetalExtras_OH, MetalExtras_EH> mod) 
	{
		MetalExtras_OH holder = mod.getObjectHolder();
		holder.copperOreRock = new BlockOreRock(holder, "copper", 1);
		holder.copperOreGround = new BlockOreGround(holder, "copper", 1);
		holder.tinOreRock = new BlockOreRock(holder, "tin", 1);
		holder.tinOreGround = new BlockOreGround(holder, "tin", 1);
		holder.aluminumOreRock = new BlockOreRock(holder, "aluminum", 1);
		holder.aluminumOreGround = new BlockOreGround(holder, "aluminum", 1);
		holder.leadOreRock = new BlockOreRock(holder, "lead", 2);
		holder.leadOreGround = new BlockOreGround(holder, "lead", 2);
		holder.silverOreRock = new BlockOreRock(holder, "silver", 2);
		holder.silverOreGround = new BlockOreGround(holder, "silver", 2);
		holder.mysteryOreRock = new BlockOreRock(holder, "mystery", 3);
		holder.mysteryOreGround = new BlockOreGround(holder, "mystery", 3);
		holder.sapphireOreRock = new BlockOreRock(holder, "sapphire", 3);
		holder.sapphireOreGround = new BlockOreGround(holder, "sapphire", 3);
		holder.rubyOreRock = new BlockOreRock(holder, "ruby", 3);
		holder.rubyOreGround = new BlockOreGround(holder, "ruby", 3);
		holder.coalOreRock = new BlockOreRock(holder, "coal", "minecraft:coal", 1, 1, 2, 0, 2, 0);
		holder.coalOreGround = new BlockOreGround(holder, "coal", "minecraft:coal", 1, 1, 2, 0, 2, 0);
		holder.ironOreRock = new BlockOreRock(holder, "iron", 1);
		holder.ironOreGround = new BlockOreGround(holder, "iron", 1);
		holder.lapisOreRock = new BlockOreRock(holder, "lapis", "minecraft:dye", 4, 8, EnumDyeColor.BLUE.getDyeDamage(), 2, 2, 5, 1);
		holder.lapisOreGround = new BlockOreGround(holder, "lapis", "minecraft:dye", 4, 8, EnumDyeColor.BLUE.getDyeDamage(), 2, 2, 5, 1);
		holder.goldOreRock = new BlockOreRock(holder, "gold", 2);
		holder.goldOreGround = new BlockOreGround(holder, "gold", 2);
		holder.redstoneOreRock = new BlockOreRock(holder, "redstone", "minecraft:redstone", 4, 5, 1, 1, 5, 2);
		holder.redstoneOreGround = new BlockOreGround(holder, "redstone", "minecraft:redstone", 4, 5, 1, 1 , 5, 2);
		holder.emeraldOreRock = new BlockOreRock(holder, "emerald", "minecraft:emerald", 1, 1, 2, 3, 7, 2);
		holder.emeraldOreGround = new BlockOreGround(holder, "emerald", "minecraft:emerald", 1, 1, 2, 3, 7, 2);
		holder.diamondOreRock = new BlockOreRock(holder, "diamond", "minecraft:diamond", 1, 1, 2, 3, 7, 2);
		holder.diamondOreGround = new BlockOreGround(holder, "diamond", "minecraft:diamond", 1, 1, 2, 3, 7, 2);
		ForgeRegistries.BLOCKS.register(holder.copper_block = new BlockCompressed("copper"));
		ForgeRegistries.BLOCKS.register(holder.tin_block = new BlockCompressed("tin"));
		ForgeRegistries.BLOCKS.register(holder.aluminum_block = new BlockCompressed("aluminum"));
		ForgeRegistries.BLOCKS.register(holder.lead_block = new BlockCompressed("lead"));
		ForgeRegistries.BLOCKS.register(holder.silver_block = new BlockCompressed("silver"));
		ForgeRegistries.BLOCKS.register(holder.ender_block = new BlockCompressed("ender"));
		ForgeRegistries.BLOCKS.register(holder.sapphire_block = new BlockCompressed("sapphire"));
		ForgeRegistries.BLOCKS.register(holder.ruby_block = new BlockCompressed("ruby"));
	}
	
	@Override
	public String getName()
	{
		return "Blocks";
	}
}