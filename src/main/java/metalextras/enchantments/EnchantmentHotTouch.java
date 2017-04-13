package metalextras.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentHotTouch extends Enchantment
{
	
	public EnchantmentHotTouch()
	{
		super(Rarity.VERY_RARE, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
		this.setName("metalextras:hot_touch");
	}
	
	@Override
	public int getMinEnchantability(int level)
	{
		return 20;
	}
	
	@Override
	public int getMaxEnchantability(int level)
	{
		return super.getMinEnchantability(level) + 50;
	}
	
}
