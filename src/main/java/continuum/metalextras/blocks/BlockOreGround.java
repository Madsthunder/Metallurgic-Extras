package continuum.metalextras.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import continuum.essentials.block.CTBlock;
import continuum.essentials.mod.CTMod;
import continuum.metalextras.api.IOre;
import continuum.metalextras.api.OrePredicate;
import continuum.metalextras.api.OreProperties;
import continuum.metalextras.blocks.BlockOreRock.EnumRockType;
import continuum.metalextras.items.ItemBlockOre;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockStateContainer.Builder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BlockOreGround extends CTBlock implements IOre
{
	public static final PropertyEnum<EnumGroundType> groundType = PropertyEnum.<EnumGroundType>create("type", EnumGroundType.class);
	private final String name;
	public final MetalExtras_OH objectHolder;
	public OreProperties properties;
	private String itemDropped = null;
	private Integer minItemDrops = null;
	private Integer maxItemDrops = null;
	private Integer metaDropped = 0;
	private Integer fortuneAdditive = 0;
	private Integer minXP = 0;
	private Integer maxXP = 0;
	
	public BlockOreGround(MetalExtras_OH objectHolder, String name, Integer index)
	{
		super(Material.GROUND, name + "_ore_ground", ItemBlockOre.class);
		this.objectHolder = objectHolder;
		this.name = name + "_ore";
		this.setSoundType(SoundType.GROUND);
		this.setUnlocalizedName(name + "_ore_ground");
		this.setDefaultState(this.getDefaultState().withProperty(groundType, EnumGroundType.DIRT));
		for(EnumGroundType type : EnumGroundType.values())
			this.setHarvestLevel("shovel", index, this.getDefaultState().withProperty(groundType, type));
	}
	
	public BlockOreGround(MetalExtras_OH objectHolder, String name, String itemDropped, Integer minItems, Integer maxItems, Integer fortuneAdditive, Integer minXP, Integer maxXP, Integer index)
	{
		this(objectHolder, name, index);
		this.itemDropped = itemDropped;
		this.minItemDrops = minItems;
		this.maxItemDrops = maxItems;
		this.fortuneAdditive = fortuneAdditive;
		this.minXP = minXP;
		this.maxXP = maxXP;
	}
	
	public BlockOreGround(MetalExtras_OH objectHolder, String name, String itemDropped, Integer minItems, Integer maxItems, Integer metaDropped, Integer fortuneAdditive, Integer minXP, Integer maxXP, Integer index)
	{
		this(objectHolder, name, itemDropped, minItems, maxItems, fortuneAdditive, minXP, maxXP, index);
		this.metaDropped = metaDropped;
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(groundType).ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(groundType, EnumGroundType.values()[meta]);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune)
	{
		return this.itemDropped == null ? this.objectHolder.ore : Item.getByNameOrId(this.itemDropped);
	}
	
	@Override
	public int quantityDropped(Random random)
	{
		return this.minItemDrops == null || this.maxItemDrops == null || this.minItemDrops >= this.maxItemDrops ? 1 : this.minItemDrops + random.nextInt(this.maxItemDrops + 1 - this.minItemDrops);
	}
	
	@Override
	public int damageDropped(IBlockState state)
	{
		return this.itemDropped == null ? this.objectHolder.ores.indexOf(name) : this.metaDropped;
	}
	
	@Override
	public int quantityDroppedWithBonus(int fortune, Random random)
	{
        if (fortune > 0 && !(this.minItemDrops == null || this.maxItemDrops == null))
        {
            int i = random.nextInt(fortune + this.fortuneAdditive) - 1;

            if (i < 0)
            {
                i = 0;
            }

            return this.quantityDropped(random) * (i + 1);
        }
        else
        {
            return this.quantityDropped(random);
        }
    }
	
	@Override
	public boolean canSilkHarvest()
	{
		return true;
	}
	
	@Override
    public List<ItemStack> getDrops(IBlockAccess access, BlockPos pos, IBlockState state, int fortune)
    {
		List<ItemStack> list = super.getDrops(access, pos, state, fortune);
		IBlockState gState = state.getValue(groundType).states.get(0);
		Block block = gState.getBlock();
		if(this.itemDropped == null)
			list.add(new ItemStack(block.getItemDropped(gState, new Random(), 0), block.quantityDropped(new Random()), block.damageDropped(gState)));
		return list;
    }
	
	@Override
	public int getExpDrop(IBlockState state, IBlockAccess access, BlockPos pos, int fortune)
	{
		return MathHelper.getRandomIntegerInRange(new Random(), this.minXP, this.maxXP);
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		for(EnumGroundType type : EnumGroundType.values())
			list.add(new ItemStack(item, 1, type.ordinal()));
	}
	
	@Override
	public BlockStateContainer createBlockState()
	{
		return new Builder(this).add(groundType).build();
	}
	
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
	{
		worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }
	
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }
	
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if (!worldIn.isRemote)
		{
			this.checkFallable(worldIn, pos);
		}
	}
    
    private void checkFallable(World world, BlockPos pos)
    {
        if (world.getBlockState(pos).getValue(groundType).canFall && (world.isAirBlock(pos.down()) || func_185759_i(world.getBlockState(pos.down()))) && pos.getY() >= 0)
        {
            int i = 32;

            if (!BlockFalling.fallInstantly && world.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i)))
            {
                if (!world.isRemote)
                {
                    EntityFallingBlock entityfallingblock = new EntityFallingBlock(world, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, world.getBlockState(pos));
                    world.spawnEntityInWorld(entityfallingblock);
                }
            }
            else
            {
            	world.setBlockToAir(pos);
                BlockPos blockpos;

                for (blockpos = pos.down(); (world.isAirBlock(blockpos) || func_185759_i(world.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.down())
                {
                    ;
                }

                if (blockpos.getY() > 0)
                {
                	world.setBlockState(blockpos.up(), this.getDefaultState());
                }
            }
        }
    }
    
    public static boolean func_185759_i(IBlockState p_185759_0_)
    {
        Block block = p_185759_0_.getBlock();
        Material material = p_185759_0_.getMaterial();
        return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
    }
    
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        return new AxisAlignedBB(0D, 0D, 0D, 1D, state.getValue(groundType) == EnumGroundType.SOUL_SAND ? 0.875D : 1D, 1D);
    }
    
    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
    {
    	this.setSoundType(state.getValue(groundType).sound);
    	if(state.getValue(groundType) == EnumGroundType.SOUL_SAND)
    	{
    		entity.motionX *= 0.4D;
    		entity.motionZ *= 0.4D;
    	}
    	this.setSoundType(state.getValue(groundType).sound);
    }
    
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
    	super.breakBlock(world, pos, state);
    	this.setSoundType(state.getValue(groundType).sound);
    }
    
    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess access, BlockPos pos, Entity entity)
    {
    	this.setSoundType(state.getValue(groundType).sound);
    	return super.canEntityDestroy(state, access, pos, entity);
    }
    
    @Override
    public boolean canHarvestBlock(IBlockAccess access, BlockPos pos, EntityPlayer player)
    {
    	this.setSoundType(access.getBlockState(pos).getValue(groundType).sound);
    	return super.canHarvestBlock(access, pos, player);
    }
    
    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
    	this.setSoundType(state.getValue(groundType).sound);
    	return true;
    }
    
    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos)
    {
    	this.setSoundType(state.getValue(groundType).sound);
    	return state.getValue(groundType).hardness;
    }
    
    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity entity, Explosion explosion)
    {
    	return world.getBlockState(pos).getValue(groundType).resistance * 3 / 5;
    }
    
    @Override
    public int getHarvestLevel(IBlockState state)
    {
    	this.setSoundType(state.getValue(groundType).sound);
    	IBlockState state2 = state.getValue(groundType).states.get(0);
    	return state2.getBlock().getHarvestLevel(state2);
    }
    
    @Override
    public String getHarvestTool(IBlockState state)
    {
    	this.setSoundType(state.getValue(groundType).sound);
    	IBlockState state2 = state.getValue(groundType).states.get(0);
    	return state2.getBlock().getHarvestTool(state2);
    }
    
    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos)
    {
    	this.setSoundType(state.getValue(groundType).sound);
    	return super.getPlayerRelativeBlockHardness(state, player, world, pos);
    }
    
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity entity, ItemStack stack)
    {
    	this.setSoundType(state.getValue(groundType).sound);
    	super.harvestBlock(world, player, pos, state, entity, stack);
    }
    
    @Override
    public Boolean isEntityInsideMaterial(IBlockAccess access, BlockPos pos, IBlockState state, Entity entity, double y, Material material, boolean head)
    {
    	this.setSoundType(state.getValue(groundType).sound);
    	return super.isEntityInsideMaterial(access, pos, state, entity, y, material, head);
    }
    
    @Override
    public Vec3d modifyAcceleration(World world, BlockPos pos, Entity entity, Vec3d motion)
    {
    	this.setSoundType(world.getBlockState(pos).getValue(groundType).sound);
    	return super.modifyAcceleration(world, pos, entity, motion);
    }
    
    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player)
    {
    	this.setSoundType(world.getBlockState(pos).getValue(groundType).sound);
    	super.onBlockClicked(world, pos, player);
    }
    
    @Override
    public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state)
    {
    	this.setSoundType(state.getValue(groundType).sound);
    	super.onBlockDestroyedByPlayer(world, pos, state);
    }
    
    @Override
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase entity)
    {
    	this.setSoundType(EnumGroundType.values()[meta].sound);
    	return super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, entity);
    }
    
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack)
    {
    	this.setSoundType(state.getValue(groundType).sound);
    	super.onBlockPlacedBy(world, pos, state, entity, stack);
    }
    
    @Override
    public void onFallenUpon(World world, BlockPos pos, Entity entity, float dist)
    {
    	this.setSoundType(world.getBlockState(pos).getValue(groundType).sound);
    	super.onFallenUpon(world, pos, entity, dist);
    }
    
    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d finish)
    {
    	this.setSoundType(state.getValue(groundType).sound);
    	return super.collisionRayTrace(state, world, pos, start, finish);
    }
    
	public static enum EnumGroundType implements IStringSerializable
	{
		DIRT(Blocks.DIRT, 0, SoundType.GROUND, .5F, 0F),
		COARSE_DIRT(Blocks.DIRT, 1, SoundType.GROUND, .5F, 0F),
		SAND(Blocks.SAND, 0, true, SoundType.SAND, .5F, 0F),
		RED_SAND(Blocks.SAND, 1, true, SoundType.SAND, .5F, 0F),
		CLAY(Blocks.CLAY, 0, true, SoundType.GROUND, .6F, 0F),
		GRAVEL(Blocks.GRAVEL, 0, true, SoundType.GROUND, .6F, 0F),
		SOUL_SAND(Blocks.SOUL_SAND, 0, SoundType.SAND, .5F, 0F);
		public final List<IBlockState> states;
		public final Boolean canFall;
		public final SoundType sound;
		public final Float hardness;
		public final Float resistance;
		private EnumGroundType(Block block, SoundType sound, Float hardness, Float resistance)
		{
			this(block.getBlockState().getValidStates(), false, sound, hardness, resistance);
		}

		private EnumGroundType(Block block, Integer meta, SoundType sound, Float hardness, Float resistance)
		{
			this(block, meta, false, sound, hardness, resistance);
		}
		
		private EnumGroundType(Block block, Integer meta, Boolean canFall, SoundType sound, Float hardness, Float resistance)
		{
			this(new ImmutableList.Builder<IBlockState>().add(block.getStateFromMeta(meta)).build(), canFall, sound, hardness, resistance);
		}
		
		private EnumGroundType(List<IBlockState> states, Boolean canFall, SoundType sound, Float hardness, Float resistance)
		{
			this.states = states;
			this.canFall = canFall;
			this.sound = sound;
			this.hardness = hardness;
			this.resistance = resistance;
		}

		@Override
		public String getName()
		{
			return this.name().toLowerCase();
		}
	}


	@Override
	public String getName()
	{
		return this.name;
	}
	
	@Override
	public Boolean getRandomizeGeneration()
	{
		return this.properties.randomizeGeneration;
	}

	@Override
	public Integer getSpawnTriesPerChunk(IBlockAccess access)
	{
		return this.properties.spawnTries;
	}

	@Override
	public Integer getMaxVeinSize(IBlockAccess access)
	{
		return this.properties.veinSize;
	}

	@Override
	public Integer getMinGenHeight(IBlockAccess access)
	{
		return this.properties.minHeight;
	}

	@Override
	public Integer getMaxGenHeight(IBlockAccess access)
	{
		return this.properties.maxHeight;
	}
	
	@Override
	public Boolean canSpawnWithMaterial(Object obj)
	{
		if(obj instanceof EnumRockType)
			return this.properties.materials[((EnumGroundType)obj).ordinal() + EnumRockType.values().length];
		return false;
	}
	
	@Override
	public OrePredicate getPredicate()
	{
		return this.objectHolder.orePredicates.get(this.name);
	}
	
	public Block applyOreProperties(OreProperties properties)
	{
		this.properties = properties;
		return this;
	}

	@Override
	public Boolean getSpawnInBiome(Biome biome, DimensionType dimension)
	{
		HashMap<String, Object> extraObjects = this.properties.extraObjects;
		String s = "";
		if(dimension == DimensionType.NETHER && extraObjects.containsKey(s = "canSpawnInNether"))
			return (Boolean) extraObjects.get(s);
		if(dimension == DimensionType.THE_END && extraObjects.containsKey(s = "canSpawnInEnd"))
			return (Boolean) extraObjects.get(s);
		if(extraObjects.containsKey(s = "temperatureLessThanOrEqualTo"))
			return biome.getTemperature() >= (Double)extraObjects.get(s);
		if(extraObjects.containsKey(s = "temperatureGreaterThanOrEqualTo"))
			return biome.getTemperature() <= (Double)extraObjects.get(s);
		return true;
	}
}
