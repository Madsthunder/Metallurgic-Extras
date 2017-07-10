package metalextras.packets;

import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;

import com.google.common.collect.Lists;

import api.metalextras.OreType;
import api.metalextras.SPacketBlockOreLandingParticles;
import metalextras.newores.NewOreType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleBlockDust;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OreLandingParticleMessageHandler implements IMessageHandler<SPacketBlockOreLandingParticles, SPacketBlockOreLandingParticles>
{
	private static final Random R = new Random();
	private static final double SPEED = 0.15000000596046448D;
	
	@SideOnly(Side.CLIENT)
	@Override
	public SPacketBlockOreLandingParticles onMessage(SPacketBlockOreLandingParticles message, MessageContext context)
	{
		Minecraft.getMinecraft().addScheduledTask(() ->
		{
			NewOreType material = message.getOreMaterial();
			OreType type = message.getOreType();
	        ResourceLocation type_name = type.getTexture();
	        ResourceLocation name = new ResourceLocation(String.format("%s.%s", material.model.getTexture(), String.format("%s_%s", type_name.getResourceDomain(), type_name.getResourcePath())));
	        TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(String.format("%s:ores/%s", name.getResourceDomain(), name.getResourcePath()));
			Vec3d pos = message.getPosition();
			WorldClient world = Minecraft.getMinecraft().world;
			ParticleManager manager = Minecraft.getMinecraft().effectRenderer;
			Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
			for(int i = 0; i < message.getParticles(); i++)
			{
				double d6 = R.nextGaussian() * SPEED;
				double d7 = R.nextGaussian() * SPEED;
				double d8 = R.nextGaussian() * SPEED;
				
				try
				{
					if(entity != null && manager != null)
					{
						int i1 = Minecraft.getMinecraft().gameSettings.particleSetting == 1 && world.rand.nextInt(3) == 0 ? 2 : Minecraft.getMinecraft().gameSettings.particleSetting;
						double d0 = entity.posX - pos.x;
						double d1 = entity.posY - pos.y;
						double d2 = entity.posZ - pos.z;
						if(d0 * d0 + d1 * d1 + d2 * d2 <= 1024 && i1 <= 1)
						{
							Particle particle = new ParticleBlockDust.Factory().createParticle(EnumParticleTypes.BLOCK_DUST.getParticleID(), world, pos.x, pos.y, pos.z, d6, d7, d8, Block.getStateId(material.applyBlockState(type)));
							particle.setParticleTexture(texture);
							manager.addEffect(particle);
						}
					}
				}
				catch(Throwable var16)
				{
					LogManager.getLogger().warn("Could not spawn particle effect {}", new Object[] { EnumParticleTypes.BLOCK_DUST });
					return;
				}
			}
		});
		
		return message;
	}
}
