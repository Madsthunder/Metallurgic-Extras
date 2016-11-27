package metalextras.tileentity;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TileEntityTeleporter extends TileEntity
{
	public BlockPos other = null;
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		if(this.other != null)
			compound.setIntArray("other", new int[] { other.getX(), other.getY(), other.getZ() });
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		int[] ints = compound.getIntArray("other");
		if(ints != null && ints.length == 3)
			this.other = new BlockPos(ints[0], ints[1], ints[2]);
	}
}
