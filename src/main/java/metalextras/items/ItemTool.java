package metalextras.items;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemTool extends Item
{
	private final float proper_block_effeciency;
	private final int enchantability;
	private final String repair_material;
	private final boolean ore_dictionary_repair;
	private final float entity_damage;
	private final float attack_speed;
	private final Map<Block, Integer> effective_blocks;
	private final List<Material> effective_materials;
	
	public ItemTool(String tool_class, int harvest_level, float proper_block_effeciency, int enchantability, String repair_material, float entity_damage, float attack_speed, int max_uses, Object... effective_objects)
	{
		this(Lists.newArrayList(tool_class), harvest_level, proper_block_effeciency, enchantability, repair_material, entity_damage, attack_speed, max_uses, effective_objects);
	}
	
	public ItemTool(String tool_class, int harvest_level, float proper_block_effeciency, int enchantability, ResourceLocation repair_material, float entity_damage, float attack_speed, int max_uses, Object... effective_objects)
	{
		this(Lists.newArrayList(tool_class), harvest_level, proper_block_effeciency, enchantability, repair_material, entity_damage, attack_speed, max_uses, effective_objects);
	}
	
	public ItemTool(List<String> tool_classes, int harvest_level, float proper_block_effeciency, int enchantability, String repair_material, float entity_damage, float attack_speed, int max_uses, Object... effective_objects)
	{
		this(tool_classes, NonNullList.withSize(tool_classes.size(), harvest_level), proper_block_effeciency, enchantability, repair_material, entity_damage, attack_speed, max_uses, effective_objects);
	}
	
	public ItemTool(List<String> tool_classes, int harvest_level, float proper_block_effeciency, int enchantability, ResourceLocation repair_material, float entity_damage, float attack_speed, int max_uses, Object... effective_objects)
	{
		this(tool_classes, NonNullList.withSize(tool_classes.size(), harvest_level), proper_block_effeciency, enchantability, repair_material, entity_damage, attack_speed, max_uses, effective_objects);
	}
	
	public ItemTool(List<String> tool_classes, List<Integer> harvest_levels, float proper_block_effeciency, int enchantability, String repair_material, float entity_damage, float attack_speed, int max_uses, Object... effective_objects)
	{
		this(toMap(tool_classes, harvest_levels), proper_block_effeciency, enchantability, repair_material.toString(), true, entity_damage, attack_speed, max_uses, effective_objects);
	}
	
	public ItemTool(List<String> tool_classes, List<Integer> harvest_levels, float proper_block_effeciency, int enchantability, ResourceLocation repair_material, float entity_damage, float attack_speed, int max_uses, Object... effective_objects)
	{
		this(toMap(tool_classes, harvest_levels), proper_block_effeciency, enchantability, repair_material.toString(), false, entity_damage, attack_speed, max_uses, effective_objects);
	}
	
	public ItemTool(Map<String, Integer> tool_class_to_harvest_level, float proper_block_effeciency, int enchantability, String repair_material, boolean ore_dictionary_repair, float entity_damage, float attack_speed, int max_uses, Object... effective_objects)
	{
		for(Entry<String, Integer> entry : tool_class_to_harvest_level.entrySet())
			this.setHarvestLevel(entry.getKey(), entry.getValue());
		this.proper_block_effeciency = proper_block_effeciency;
		this.enchantability = enchantability;
		this.repair_material = repair_material;
		this.ore_dictionary_repair = ore_dictionary_repair;
		this.entity_damage = entity_damage;
		this.attack_speed = attack_speed;
		List<Material> effective_materials = Lists.newArrayList();
		Map<Block, Integer> effective_blocks = Maps.newHashMap();
		for(Object o : effective_objects)
			if(o instanceof Material)
				effective_materials.add((Material)o);
			else if(o instanceof Block)
				effective_blocks.put((Block)o, (int)Short.MIN_VALUE);
			else if(o instanceof Pair && ((Pair)o).getLeft() instanceof Block && ((Pair)o).getRight() instanceof Integer)
				effective_blocks.put((Block)((Pair)o).getLeft(), (Integer)((Pair)o).getRight());
		this.effective_materials = Lists.newArrayList(effective_materials);
		this.effective_blocks = Maps.newHashMap(effective_blocks);
		this.setMaxDamage(max_uses);
		this.setMaxStackSize(1);
	}
	
	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state)
	{
		if(this.effective_materials.contains(state.getMaterial()))
			return this.proper_block_effeciency;
		for(String tool : this.getToolClasses(stack))
			if(state.getBlock().isToolEffective(tool, state))
				return this.proper_block_effeciency;
		return 1F;
	}
	
	@Override
	public boolean canHarvestBlock(IBlockState state)
	{
		return this.effective_materials.contains(state.getMaterial());
	}
	
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase attacker)
	{
		stack.damageItem(2, attacker);
		return true;
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entity)
	{
		if(!world.isRemote && state.getBlockHardness(world, pos) != 0)
			stack.damageItem(1, entity);
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D()
	{
		return true;
	}
	
	@Override
	public int getItemEnchantability()
	{
		return this.enchantability;
	}
	
	@Override
	public boolean getIsRepairable(ItemStack tool, ItemStack stack)
	{
		if(stack.isEmpty())
			return false;
		if(this.ore_dictionary_repair)
			for(int i : OreDictionary.getOreIDs(stack))
			{
				System.out.println(OreDictionary.getOreName(i) + ", " + this.repair_material);
				if(this.repair_material.equals(OreDictionary.getOreName(i)))
					return true;
			}
		return stack.getItem() == ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.repair_material));
	}
	
	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot)
	{
		Multimap<String, AttributeModifier> modifiers = super.getItemAttributeModifiers(slot);
		if(slot == EntityEquipmentSlot.MAINHAND)
		{
			modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(Item.ATTACK_DAMAGE_MODIFIER, "Tool modifier", this.entity_damage, 0));
			modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(Item.ATTACK_SPEED_MODIFIER, "Tool modifier", this.attack_speed, 0));
		}
		return modifiers;
	}
	
	private static Map<String, Integer> toMap(List<String> tool_classes, List<Integer> harvest_levels)
	{
		if(tool_classes.size() != harvest_levels.size())
			throw new IllegalArgumentException("There Must Be An Equal Amount of Harvest Levels And Tool Classes.");
		Map<String, Integer> tool_class_to_harvest_level = Maps.newHashMapWithExpectedSize(tool_classes.size());
		for(int i = 0; i < harvest_levels.size(); i++)
			tool_class_to_harvest_level.put(tool_classes.get(i), harvest_levels.get(i));
		return tool_class_to_harvest_level;
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
	{
		return enchantment instanceof EnchantmentDamage ? this.getToolClasses(stack).contains("axe") : enchantment.type == EnumEnchantmentType.DIGGER;
	}
}
