package metalextras.ores;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import api.metalextras.BlockOre;
import api.metalextras.IBlockOreMulti;
import api.metalextras.ModelType;
import api.metalextras.OreMaterial;
import api.metalextras.OreProperties;
import api.metalextras.OreType;
import api.metalextras.OreTypes;
import api.metalextras.OreUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class VanillaOreMaterial extends OreMaterial.Impl
{
	private final IBlockState state;
	private final Properties properties = new Properties();
	private final int xpMin;
	private final int xpMax;
	private final GenerateMinable.EventType type;
	private final ModelType modelType;
	private final boolean poweredVariant;
	
	public VanillaOreMaterial(IBlockState state, int xpMin, int xpMax, ModelType modelType, GenerateMinable.EventType type)
	{
		this(state, xpMin, xpMax, modelType, type, false);
	}
	
	public VanillaOreMaterial(IBlockState state, int xpMin, int xpMax, ModelType modelType, GenerateMinable.EventType type, boolean poweredVariant)
	{
		this.state = state;
		this.xpMin = xpMin;
		this.xpMax = xpMax;
		this.modelType = modelType;
		this.type = type;
		this.poweredVariant = poweredVariant;
	}
	
	@Override
	public int getHarvestLevel()
	{
		return this.state.getBlock().getHarvestLevel(this.state);
	}
	
	@Override
	public Item getDrop()
	{
		Item drop = this.state.getBlock().getItemDropped(this.state, new Random(), 0);
		return Block.getBlockFromItem(drop) == this.state.getBlock() ? OreMaterial.ORE : drop;
	}
	
	@Override
	public int getDropCount(Random random)
	{
		return this.state.getBlock().quantityDropped(random);
	}
	
	@Override
	public int getDropCountWithFortune(int fortune, Random random)
	{
		return this.state.getBlock().quantityDropped(this.state, fortune, random);
	}
	
	@Override
	public int getDropMeta()
	{
		Item drop = this.state.getBlock().getItemDropped(this.state, new Random(), 0);
		return Block.getBlockFromItem(drop) == this.state.getBlock() ? -1 : this.state.getBlock().damageDropped(this.state);
	}
	
	@Override
	public int getDropXP(Random random)
	{
		return MathHelper.getInt(random, this.xpMin, this.xpMax);
	}
	
	@Override
	public OreProperties getOreProperties()
	{
		return this.properties;
	}
	
	@Override
	public String getLanguageKey()
	{
		return this.state.getBlock().getUnlocalizedName();
	}
	
	public abstract int getSpawnTries(World world, Random random);
	
	public abstract int[] getSpawnParams(World world);
	
	public abstract int getVeinSize(BiomeDecorator decorator);
	
	public GenerateType getGenerateType()
	{
		return GenerateType.ORE1;
	}
	
	@Override
	public Collection<GenerateMinable.EventType> getOverrides()
	{
		return Sets.newHashSet(this.type);
	}
	
	@Override
	public IModel getModel(OreType type)
	{
		return ModelLoaderRegistry.getModelOrMissing(new ResourceLocation("metalextras", "block/" + this.getRegistryName().getResourcePath()));
	}
	
	@Override
	public ModelType getModelType()
	{
		return this.modelType;
	}
	
	@Override
	public Iterable<BlockOre> getBlocksToRegister(OreTypes types)
	{
		if(this.poweredVariant)
		{
			BlockPoweredOre unpowered = (BlockPoweredOre)this.createBlock(types);
			return Lists.newArrayList(unpowered, unpowered.other);
		}
		return super.getBlocksToRegister(types);
	}
	
	@Override
	public BlockOre createBlock(OreTypes types)
	{
		if(this.poweredVariant)
		{
			BlockPoweredOre unpowered = new BlockPoweredOre(this, types, false);
			BlockPoweredOre powered = new BlockPoweredOre(this, types, true);
			unpowered.other = powered;
			powered.other = unpowered;
			return unpowered;
		}
		return new BlockOre.SimpleImpl(this, types);
	}
	
	private static class BlockPoweredOre extends BlockOre.SimpleImpl implements IBlockOreMulti
	{
		private final boolean powered;
		private BlockPoweredOre other;
		
		public BlockPoweredOre(VanillaOreMaterial material, OreTypes types, boolean powered)
		{
			super(material, types, pair -> pair.getLeft().getRegistryName().toString() + (powered ? "_powered." : ".") + pair.getRight().getRegistryName().toString().replaceFirst(":", "_"));
			this.powered = powered;
			if(powered)
			{
				this.setTickRandomly(true);
				this.setLightLevel(0.625F);
			}
		}
		
		@Override
		public void onBlockClicked(World world, BlockPos pos, EntityPlayer player)
		{
			this.activate(world, pos);
			super.onBlockClicked(world, pos, player);
		}
		
		@Override
		public void onEntityWalk(World world, BlockPos pos, Entity entity)
		{
			this.activate(world, pos);
			super.onEntityWalk(world, pos, entity);
		}
		
		@Override
		public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
		{
			this.activate(world, pos);
			return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
		}
		
		private void activate(World world, BlockPos pos)
		{
			this.spawnParticles(world, pos);
			if(!this.powered)
				world.setBlockState(pos, this.other.getBlockState(this.getOreType(world.getBlockState(pos))));
		}
		
		@Override
		public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
		{
			if(this.powered)
				world.setBlockState(pos, this.other.getBlockState(this.getOreType(state)));
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random)
		{
			if(this.powered)
				this.spawnParticles(world, pos);
		}
		
		private void spawnParticles(World world, BlockPos pos)
		{
			Random random = world.rand;
			for(int i = 0; i < 6; i++)
			{
				double x = pos.getX() + random.nextFloat();
				double y = pos.getY() + random.nextFloat();
				double z = pos.getZ() + random.nextFloat();
				if(i == 0 && !world.getBlockState(pos.up()).isOpaqueCube())
					y = pos.getY() + .0625 + 1;
				if(i == 1 && !world.getBlockState(pos.down()).isOpaqueCube())
					y = pos.getY() - .0625;
				if(i == 2 && !world.getBlockState(pos.south()).isOpaqueCube())
					z = pos.getZ() + .0625 + 1;
				if(i == 3 && !world.getBlockState(pos.north()).isOpaqueCube())
					z = pos.getZ() - .0625;
				if(i == 4 && !world.getBlockState(pos.east()).isOpaqueCube())
					x = pos.getX() + .0625 + 1;
				if(i == 5 && !world.getBlockState(pos.west()).isOpaqueCube())
					x = pos.getX() - .0625;
				if(x < pos.getX() || x > pos.getX() + 1 || y < 0.0D || y > pos.getY() + 1 || z < pos.getZ() || z > pos.getZ() + 1)
					world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0, 0, 0, new int[0]);
			}
		}
		
		@Override
		public ItemStack getSilkTouchDrop(IBlockState state)
		{
			return this.powered ? this.other.getSilkTouchDrop(this.other.getBlockState(this.getOreType(state))) : super.getSilkTouchDrop(state);
		}
		
		@Override
		public List<BlockOre> getOthers()
		{
			return Lists.newArrayList(this.other);
		}
	}
	
	private class Properties extends OreProperties
	{
		private final WorldGenerator generator;
		
		private Properties()
		{
			this.generator = new WorldGenerator()
			{
				@Override
				public boolean generate(World world, Random random, BlockPos pos)
				{
					return OreUtils.generateOres(world, pos, random, VanillaOreMaterial.this.getVeinSize(world.getBiome(pos).theBiomeDecorator), Properties.this);
				}
			};
		}
		
		@Override
		public OreMaterial getOreMaterial()
		{
			return VanillaOreMaterial.this;
		}
		
		@Override
		public boolean getSpawnEnabled()
		{
			return true;
		}
		
		@Override
		public int getSpawnTriesPerChunk(World world, Random random)
		{
			return VanillaOreMaterial.this.getSpawnTries(world, random);
		}
		
		@Override
		public BlockPos getRandomSpawnPos(World world, Random random)
		{
			return new BlockPos(random.nextInt(16), VanillaOreMaterial.this.getGenerateType().getHeight(VanillaOreMaterial.this, world, random), random.nextInt(16));
		}
		
		@Override
		public WorldGenerator getWorldGenerator(World world, BlockPos pos)
		{
			return this.generator;
		}
		
		@Override
		public boolean isValid(OreType type)
		{
			return true;
		}
	}
	
	public static enum GenerateType
	{
		ORE1,
		ORE2,
		ORE3;
		
		public int getHeight(VanillaOreMaterial material, World world, Random random)
		{
			int[] spawnParams = material.getSpawnParams(world);
			if(this == ORE2)
				return random.nextInt(spawnParams[1]) + random.nextInt(spawnParams[1]) + spawnParams[0] - spawnParams[1];
			else if(this == ORE3)
				return random.nextInt(spawnParams[0]) + spawnParams[1];
			else
				return random.nextInt(spawnParams[1] - spawnParams[0]) + spawnParams[0];
		}
	}
	
	public static enum OreModelType
	{
		IRON,
		LAPIS,
		EMERALD;
		// TODO Add some model methods
	}
}
