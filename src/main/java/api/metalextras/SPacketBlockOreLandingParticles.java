package api.metalextras;

import io.netty.buffer.ByteBuf;
import metalextras.newores.NewOreType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SPacketBlockOreLandingParticles implements IMessage
{
	private NewOreType newtype;
	private OreType type;
	private Vec3d pos;
	private int particles;

	public SPacketBlockOreLandingParticles()
	{
	}

	public SPacketBlockOreLandingParticles(NewOreType newtype, OreType type, Vec3d pos, int particles)
	{
		this.newtype = newtype;
		this.type = type;
		this.pos = pos;
		this.particles = particles;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.newtype = OreUtils.getTypesRegistry().getTypeByName(new ResourceLocation(ByteBufUtils.readUTF8String(buffer)));
		this.type = OreUtils.findOreType(new ResourceLocation(ByteBufUtils.readUTF8String(buffer)));
		this.pos = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		this.particles = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, this.newtype.getRegistryName().toString());
		ByteBufUtils.writeUTF8String(buffer, this.type.getRegistryName().toString());
		buffer.writeDouble(this.pos.x);
		buffer.writeDouble(this.pos.y);
		buffer.writeDouble(this.pos.z);
		buffer.writeInt(this.particles);
	}

	public NewOreType getOreMaterial()
	{
		return this.newtype;
	}

	public OreType getOreType()
	{
		return this.type;
	}

	public Vec3d getPosition()
	{
		return this.pos;
	}

	public int getParticles()
	{
		return this.particles;
	}

	public static class SendLandingParticlesEvent extends Event
	{
		private final int dimension;
		private final SPacketBlockOreLandingParticles message;

		public SendLandingParticlesEvent(int dimension, SPacketBlockOreLandingParticles message)
		{
			this.dimension = dimension;
			this.message = message;
		}

		public int getDimension()
		{
			return this.dimension;
		}

		public SPacketBlockOreLandingParticles getMessage()
		{
			return this.message;
		}
	}
}
