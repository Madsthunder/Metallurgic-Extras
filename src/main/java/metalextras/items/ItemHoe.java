package metalextras.items;

import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemHoe extends Item
{
    private final boolean ore_dictionary_repair;
    private final String repair_material;
    private final float attack_speed;

    
    public ItemHoe(String repair_material, boolean ore_dictionary_repair, float attack_speed, int max_uses)
    {
        this.ore_dictionary_repair = ore_dictionary_repair;
        this.repair_material = repair_material;
        this.attack_speed = attack_speed;
        this.setMaxDamage(max_uses);
        this.setMaxStackSize(1);
    }

    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getHeldItem(hand);
        if(!player.canPlayerEdit(pos.offset(facing), facing, stack))
            return EnumActionResult.FAIL;
        else
        {
            int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(stack, player, worldIn, pos);
            if (hook != 0) return hook > 0 ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;

            IBlockState state = worldIn.getBlockState(pos);
            Block block = state.getBlock();

            if(facing != EnumFacing.DOWN && worldIn.isAirBlock(pos.up()))
            {
                if(block == Blocks.GRASS || block == Blocks.GRASS_PATH)
                {
                    this.setBlock(stack, player, worldIn, pos, Blocks.FARMLAND.getDefaultState());
                    return EnumActionResult.SUCCESS;
                }
                if(block == Blocks.DIRT)
                {
                    switch(state.getValue(BlockDirt.VARIANT))
                    {
                        case DIRT:
                            this.setBlock(stack, player, worldIn, pos, Blocks.FARMLAND.getDefaultState());
                            return EnumActionResult.SUCCESS;
                        case COARSE_DIRT:
                            this.setBlock(stack, player, worldIn, pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
                            return EnumActionResult.SUCCESS;
                    }
                }
            }

            return EnumActionResult.PASS;
        }
    }

    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        stack.damageItem(1, attacker);
        return true;
    }

    protected void setBlock(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, IBlockState state)
    {
        worldIn.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
        if (!worldIn.isRemote)
        {
            worldIn.setBlockState(pos, state, 11);
            stack.damageItem(1, player);
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }

    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
    {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);
        if(equipmentSlot == EntityEquipmentSlot.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 0.0D, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double)(this.attack_speed - 4.0F), 0));
        }
        return multimap;
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
}