package metalextras.packets;

import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;

import com.google.common.collect.Lists;

import api.metalextras.OreMaterial;
import api.metalextras.OreType;
import api.metalextras.SPacketBlockOreLandingParticles;
import net.minecraft.block.Block;
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
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class OreLandingParticleMessageHandler implements IMessageHandler<SPacketBlockOreLandingParticles, SPacketBlockOreLandingParticles>
{
	private static final Random R = new Random();
	private static final double SPEED = 0.15000000596046448D;
	
	@Override
	public SPacketBlockOreLandingParticles onMessage(SPacketBlockOreLandingParticles message, MessageContext context)
	{
		Minecraft.getMinecraft().addScheduledTask(() ->
		{
			OreMaterial material = message.getOreMaterial();
			OreType type = message.getOreType();
			Vec3d pos = message.getPosition();
			WorldClient world = Minecraft.getMinecraft().world;
			ParticleManager manager = Minecraft.getMinecraft().effectRenderer;
			Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
			for(int i = 0; i < message.getParticles(); i++)
			{
				List<TextureAtlasSprite> textures = Lists.newArrayList();
				{
					TextureAtlasSprite texture = material.getModel(type).bake(ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter()).getParticleTexture();
					if(texture != null)
						textures.add(texture);
					texture = type.getModel(material).bake(ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter()).getParticleTexture();
					if(texture != null)
						textures.add(texture);
				}
				double d6 = R.nextGaussian() * SPEED;
				double d7 = R.nextGaussian() * SPEED;
				double d8 = R.nextGaussian() * SPEED;
				
				try
				{
					if(entity != null && manager != null)
					{
						int i1 = Minecraft.getMinecraft().gameSettings.particleSetting == 1 && world.rand.nextInt(3) == 0 ? 2 : Minecraft.getMinecraft().gameSettings.particleSetting;
						double d0 = entity.posX - pos.xCoord;
						double d1 = entity.posY - pos.yCoord;
						double d2 = entity.posZ - pos.zCoord;
						if(d0 * d0 + d1 * d1 + d2 * d2 <= 1024 && i1 <= 1)
						{
							Particle particle = new ParticleBlockDust.Factory().createParticle(EnumParticleTypes.BLOCK_DUST.getParticleID(), world, pos.xCoord, pos.yCoord, pos.zCoord, d6, d7, d8, Block.getStateId(material.applyBlockState(type)));
							particle.setParticleTexture(textures.get(world.rand.nextInt(textures.size())));
							manager.addEffect(particle);
						}
					}
					
					/**
					 * for(IWorldEventListener listener :
					 * Minecraft.getMinecraft().world.listen)
					 * Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.BLOCK_DUST,
					 * false, this.pos.xCoord, this.pos.yCoord, this.pos.zCoord,
					 * d6, d7, d8,
					 * Block.getStateId(this.material.applyBlockState(this.type)));
					 */
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
