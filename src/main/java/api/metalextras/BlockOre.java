package api.metalextras;

import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import api.metalextras.SPacketBlockOreLandingParticles.SendLandingParticlesEvent;
import metalextras.newores.NewOreType;
import metalextras.newores.modules.BlockModule.Drop;
import metalextras.newores.modules.ModelModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOre extends net.minecraft.block.BlockOre
{
	private final OreTypeProperty property;
	private final NewOreType type;

	public BlockOre(NewOreType type, OreTypes types)
	{
		this(type, types, BlockOre::getDefaultRegistryName);
	}

	public BlockOre(NewOreType type, OreTypes types, BiFunction<NewOreType, OreTypes, ResourceLocation> registry_name_getter)
	{
		this.setRegistryName(registry_name_getter.apply(type, types));
		this.property = new OreTypeProperty(types, this);
		this.setDefaultState(this.getBlockState().getBaseState());
		this.setDefaultState(this.getBlockState().getProperties().isEmpty() ? this.getDefaultState() : this.getDefaultState().withProperty(this.getOreTypeProperty(), this.getOreTypeProperty().getAllowedValues().get(0)));
		this.type = type;
	}

	public final NewOreType getOreType()
	{
		return this.type;
	}

	@Override
	public final Material getMaterial(IBlockState state)
	{
		return this.getOreType(state).getMaterial();
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity)
	{
		return this.getOreType(state).getSoundType();
	}

	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity entity, @Nullable ItemStack stack)
	{
		boolean harvest = false;
		if(stack != null)
			for(String tool : stack.getItem().getToolClasses(stack))
				if(this.isToolEffective(tool, state))
				{
					harvest = true;
					break;
				}
		if(harvest)
			super.harvestBlock(world, player, pos, state, entity, stack);
		else
		{
			OreType type = this.getOreType(state);
			Block block = type.getState().getBlock();
			if(type.getState().getBlock().canHarvestBlock(world, pos, player))
				Block.spawnAsEntity(world, pos, new ItemStack(block.getItemDropped(type.getState(), world.rand, 0), block.quantityDropped(world.rand), block.damageDropped(type.getState())));
		}
	}

	@Override
	public void getDrops(NonNullList<ItemStack> stacks, IBlockAccess access, BlockPos pos, IBlockState state, int fortune)
	{
		Random random = new Random((access instanceof World ? ((World)access).rand : RANDOM).nextLong());
		{
			IBlockState type_state = this.getOreType(state).getState();
			int count = type_state.getBlock().quantityDropped(type_state, fortune, random);
			for(int i = 0; i < count; i++)
			{
				Item item = type_state.getBlock().getItemDropped(type_state, random, fortune);
				if(item != Items.AIR)
					stacks.add(new ItemStack(item, 1, type_state.getBlock().damageDropped(type_state)));
			}
		}
		for(Drop drop : this.type.getBlockModule().getDrops())
			if(random.nextFloat() >= 1F - drop.getChance(fortune))
			{
				int min_count = drop.getMinCount(fortune);
				int max_count = drop.getMaxCount(fortune);
				int count = Math.max(0, min_count >= max_count ? max_count : random.nextInt(max_count - min_count + 1) + min_count);
				for(int i = 0; i < count; i++)
				{
					NBTTagCompound compound = drop.getNbt();
					compound.setString("id", drop.getItem().getRegistryName().toString());
					compound.setByte("Count", (byte)1);
					compound.setInteger("Damage", drop.getMetadata());
					stacks.add(new ItemStack(compound));
				}
			}
	}

	@Override
	public int getExpDrop(IBlockState state, IBlockAccess access, BlockPos pos, int fortune)
	{
		int min_xp = this.type.getBlockModule().getMinXp();
		int max_xp = this.type.getBlockModule().getMaxXp();
		return Math.max(0, min_xp >= max_xp ? max_xp : (access instanceof World ? ((World)access).rand : Block.RANDOM).nextInt(max_xp - min_xp + 1) + min_xp);
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced)
	{
		tooltip.add(this.getOreType(stack.getMetadata()).getState().getBlock().getLocalizedName());
	}

	@Override
	public int getHarvestLevel(IBlockState state)
	{
		int materialHarvest = this.getOreType().getBlockModule().getHarvestLevel();
		int typeHarvest = this.getOreType(state).getHarvestLevel();
		return materialHarvest == -1 || typeHarvest == -1 ? -1 : Math.max(materialHarvest, typeHarvest);
	}

	@Override
	public String getHarvestTool(IBlockState state)
	{
		return this.getOreType(state).getHarvestTool();
	}

	@Override
	public boolean canSilkHarvest()
	{
		return true;
	}

	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos)
	{
		return this.getOreType(state).getHardness();
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity entity, Explosion explosion)
	{
		return this.getOreType(world.getBlockState(pos)).getResistance();
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult result, World world, BlockPos pos, EntityPlayer player)
	{
		return new ItemStack(this, 1, this.getMetaFromState(state));
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		if(this.getOreType(state).canFall())
			world.scheduleUpdate(pos, this, 2);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos neighbor)
	{
		if(this.getOreType(state).canFall())
			world.scheduleUpdate(pos, this, 2);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
	{
		if(this.getOreType(state).canFall() && !world.isRemote)
			checkFallable(this, world, pos);
	}

	protected void onStartFalling(EntityFallingBlock entity)
	{
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random)
	{
		if(this.getOreType(state).canFall() && random.nextInt(16) == 0)
		{
			BlockPos blockpos = pos.down();
			if(BlockFalling.canFallThrough(world.getBlockState(blockpos)))
			{
				double x = pos.getX() + random.nextFloat();
				double y = pos.getY() - .05;
				double z = pos.getZ() + random.nextFloat();
				world.spawnParticle(EnumParticleTypes.FALLING_DUST, x, y, z, 0, 0, 0, Block.getStateId(this.getOreType(state).getState()));
			}
		}
	}

	@Override
	public final void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
	{
		Item item = Item.getItemFromBlock(this);
		for(int i = 0; i < this.getOreTypeProperty().getTypes().getOreTypes().size(); i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		this.getOreType(state).handleEntityCollision(world, pos, state, entity);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)
	{
		IBlockState state = world.getBlockState(pos).getActualState(world, pos);
		ResourceLocation type_name = this.getOreType(state).getTexture();
		ResourceLocation name = new ResourceLocation(String.format("%s.%s", this.type.getChildModule(ModelModule.class).getTexture(), String.format("%s_%s", type_name.getResourceDomain(), type_name.getResourcePath())));
		TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(String.format("%s:ores/%s", name.getResourceDomain(), name.getResourcePath()));
		for(int j = 0; j < 4; j++)
			for(int k = 0; k < 4; k++)
				for(int l = 0; l < 4; l++)
				{
					double x = pos.getX() + (j + .5) / 4;
					double y = pos.getY() + (k + .5) / 4;
					double z = pos.getZ() + (l + .5) / 4;
					ParticleDigging particle = (ParticleDigging)new ParticleDigging.Factory().createParticle(0, world, x, y, z, x - pos.getX() - .5, y - pos.getY() - .5, z - pos.getZ() - .5, Block.getStateId(state));
					particle.setBlockPos(pos);
					particle.setParticleTexture(texture);
					manager.addEffect(particle);
				}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(IBlockState state, World world, RayTraceResult result, ParticleManager manager)
	{
		ResourceLocation type_name = this.getOreType(state).getTexture();
		ResourceLocation name = new ResourceLocation(String.format("%s.%s", this.type.getChildModule(ModelModule.class).getTexture(), String.format("%s_%s", type_name.getResourceDomain(), type_name.getResourcePath())));
		TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(String.format("%s:ores/%s", name.getResourceDomain(), name.getResourcePath()));
		BlockPos pos = result.getBlockPos();
		EnumFacing side = result.sideHit;
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		AxisAlignedBB axisalignedbb = state.getBoundingBox(world, pos);
		double d0 = i + world.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minX;
		double d1 = j + world.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minY;
		double d2 = k + world.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minZ;
		switch(side)
		{
			case DOWN :
			{
				d1 = j + axisalignedbb.minY - 0.10000000149011612D;
				break;
			}
			case UP :
			{
				d1 = j + axisalignedbb.maxY + 0.10000000149011612D;
				break;
			}
			case NORTH :
			{
				d2 = k + axisalignedbb.minZ - 0.10000000149011612D;
				break;
			}
			case SOUTH :
			{
				d2 = k + axisalignedbb.maxZ + 0.10000000149011612D;
				break;
			}
			case WEST :
			{
				d0 = i + axisalignedbb.minX - 0.10000000149011612D;
				break;
			}
			case EAST :
			{
				d0 = i + axisalignedbb.maxX + 0.10000000149011612D;
				break;
			}
			default :
				;
		}
		ParticleDigging particle = (ParticleDigging)new ParticleDigging.Factory().createParticle(0, world, d0, d1, d2, 0, 0, 0, Block.getStateId(state));
		particle.setBlockPos(pos);
		particle.multiplyVelocity(0.2F);
		particle.multipleParticleScaleBy(0.6F);
		particle.setParticleTexture(texture);
		manager.addEffect(particle);
		return true;
	}

	@Override
	public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos, IBlockState useless, EntityLivingBase entity, int particles)
	{
		MinecraftForge.EVENT_BUS.post(new SendLandingParticlesEvent(world.provider.getDimension(), new SPacketBlockOreLandingParticles(this.getOreType(), this.getOreType(state), new Vec3d(entity.posX, entity.posY, entity.posZ), particles)));
		return true;
	}

	public OreTypeProperty getOreTypeProperty()
	{
		return this.property;
	}

	public boolean hasOreType(OreType type)
	{
		return this.getOreTypeProperty().getAllowedValues().contains(type);
	}

	public IBlockState withOreType(OreType type)
	{
		return this.getDefaultState().withProperty(this.getOreTypeProperty(), type);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
	{
		return state.getValue(this.getOreTypeProperty()).getSelectionBox(world, pos);
	}

	@Override
	public String getUnlocalizedName()
	{
		String langKey = this.getOreType().getName();
		return langKey == null ? this.getOreType().registry_name.getResourcePath() : this.getOreType().getName();
	}

	@Override
	public final int getMetaFromState(IBlockState state)
	{
		return this.getIndex(this.getOreType(state));
	}

	@Override
	public final IBlockState getStateFromMeta(int meta)
	{
		return this.getBlockState(this.getOreType(meta));
	}

	@Override
	public final BlockStateContainer getBlockState()
	{
		return this.getOreTypeProperty().getBlockState();
	}

	public final OreType getOreType(IBlockState state)
	{
		return this.getBlockState().getProperties().isEmpty() ? this.getOreTypeProperty().getTypes().getOreTypes().get(0) : state.getValue(this.getOreTypeProperty());
	}

	public final IBlockState getBlockState(OreType type)
	{
		return this.getBlockState().getProperties().isEmpty() ? this.getDefaultState() : this.getDefaultState().withProperty(this.getOreTypeProperty(), type);
	}

	public final OreType getOreType(int index)
	{
		return this.getOreTypeProperty().getTypes().getOreTypes().get(index);
	}

	public final int getIndex(OreType type)
	{
		return type.getTypes() == this.getOreTypeProperty().getTypes() ? this.getBlockState().getProperties().isEmpty() ? 0 : this.getOreTypeProperty().getTypes().getOreTypes().indexOf(type) : -1;
	}

	public static ResourceLocation getDefaultRegistryName(NewOreType type, OreTypes types)
	{
		return new ResourceLocation(String.format("%s/%s", type.registry_name, types.getRegistryName().toString().replaceFirst(":", "/")));
	}

	public static void checkFallable(BlockOre block, World world, BlockPos pos)
	{
		if((world.isAirBlock(pos.down()) || BlockFalling.canFallThrough(world.getBlockState(pos.down()))) && pos.getY() >= 0)
		{
			IBlockState state = world.getBlockState(pos);
			if(!BlockFalling.fallInstantly && world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
			{
				if(!world.isRemote)
				{
					EntityFallingBlock entity = new EntityFallingBlock(world, pos.getX() + .5, pos.getY(), pos.getZ() + .5, state);
					block.onStartFalling(entity);
					world.spawnEntity(entity);
				}
			}
			else
			{
				world.setBlockToAir(pos);
				BlockPos.MutableBlockPos pos1 = new BlockPos.MutableBlockPos(pos.down());
				while((world.isAirBlock(pos1) || BlockFalling.canFallThrough(world.getBlockState(pos1))) && pos1.getY() > 0)
					pos1.setY(pos1.getY() - 1);
				if(pos1.getY() > 0)
					world.setBlockState(pos1.up(), state);
			}
		}
	}
}
