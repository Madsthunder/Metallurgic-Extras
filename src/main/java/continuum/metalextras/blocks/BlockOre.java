package continuum.metalextras.blocks;

import java.util.List;
import java.util.Random;

import continuum.api.metalextras.IOreData;
import continuum.api.metalextras.IOreGroup;
import continuum.api.metalextras.IOreType;
import continuum.essentials.block.state.PropertyValues;
import continuum.essentials.hooks.ObjectHooks;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockStateContainer.Builder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockOre extends Block
{
	private final PropertyValues<IOreType> oreType;
	private final BlockStateContainer realContainer;
	private final ResourceLocation name;
	public final MetalExtras_OH objectHolder;
	private final IOreData data;
	private final IOreGroup group;
	
	public BlockOre(MetalExtras_OH objectHolder, IOreData data, IOreGroup group)
	{
		super(Material.ROCK);
		this.setRegistryName(new ResourceLocation("metalextras", data.getOreName().getResourcePath() + "_" + group.getName().getResourcePath()));
		this.objectHolder = objectHolder;
		this.realContainer = new Builder(this).add(this.oreType = PropertyValues.create("type", IOreType.class, (this.group = group).getOreTypes())).build();
		this.name = (this.data = data).getOreName();
		this.setUnlocalizedName(this.name.getResourcePath());
		this.setDefaultState(this.realContainer.getBaseState());
		this.setDefaultState(this.getDefaultState().withProperty(oreType, group.getOreTypes().get(0)));
		this.setHarvestLevel(null, data.getHarvestLevel());
	}
	
	public int getMetaFromState(IBlockState state)
	{
		return this.oreType.getAllowedValues().indexOf(state.getValue(this.oreType));
	}
	
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(this.oreType, this.oreType.getAllowedValues().get(meta));
	}
	
	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity)
	{
		return state.getValue(this.oreType).getSoundType();
	}
	
	@Override
    public List<ItemStack> getDrops(IBlockAccess access, BlockPos pos, IBlockState state, int fortune)
    {
        Random random = access instanceof World ? ((World)access).rand : RANDOM;
		List<ItemStack> list = super.getDrops(access, pos, state, fortune);
		IBlockState typeState = state.getValue(this.oreType).getState();
		Block typeBlock = typeState.getBlock();
		if(this.data.getItem() != this.objectHolder.ore)
			list.add(new ItemStack(typeBlock.getItemDropped(typeState, random, 0), typeBlock.quantityDropped(random), typeBlock.damageDropped(typeState)));
		return list;
    }
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult result, World world, BlockPos pos, EntityPlayer player)
	{
		return new ItemStack(this, 1, this.getMetaFromState(state));
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune)
	{
		return this.data.getItem();
	}
	
	@Override
	public int quantityDropped(Random random)
	{
		Integer minItems = this.data.getMinItems();
		Integer maxItems = this.data.getMaxItems();
		return minItems >= maxItems ? maxItems : minItems + random.nextInt(maxItems - minItems + 1);
	}
	
	@Override
	public int damageDropped(IBlockState state)
	{
		return this.data.getItem() == this.objectHolder.ore ? this.objectHolder.ores.indexOf(this.data) : this.data.getMetadata();
	}
	
	@Override
	public int quantityDroppedWithBonus(int fortune, Random random)
	{
        if (fortune > 0)
        {
            int i = random.nextInt(fortune + this.data.getFortuneAdditive()) - 1;
            if (i < 0)
                i = 0;
            return this.quantityDropped(random) * (i + 1);
        }
        else
            return this.quantityDropped(random);
    }
	
	@Override
	public boolean canSilkHarvest()
	{
		return true;
	}
	
	@Override
	public int getExpDrop(IBlockState state, IBlockAccess access, BlockPos pos, int fortune)
	{
		return MathHelper.getRandomIntegerInRange(new Random(), this.data.getMinXP(), this.data.getMaxXP());
	}
    
    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos)
    {
    	return state.getValue(this.oreType).getHardness();
    }
    
    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity entity, Explosion explosion)
    {
    	return world.getBlockState(pos).getValue(this.oreType).getResistance();
    }
    
    @Override
    public int getHarvestLevel(IBlockState state)
    {
    	return state.getValue(this.oreType).getHarvestLevel();
    }
    
    @Override
    public String getHarvestTool(IBlockState state)
    {
    	return state.getValue(this.oreType).getHarvestTool();
    }
	
    @Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		if(state.getValue(this.oreType).canFall()) world.scheduleUpdate(pos, this, this.tickRate(world));
    }
	
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block)
    {
        if(state.getValue(this.oreType).canFall()) world.scheduleUpdate(pos, this, this.tickRate(world));
    }
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		for(Integer i : ObjectHooks.increment(this.oreType.getAllowedValues().size()))
			list.add(new ItemStack(item, 1, i));
	}
	
    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
    {
    	state.getValue(this.oreType).handleEntityCollision(world, pos, state, entity);
    }

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if(!worldIn.isRemote) this.checkFallable(worldIn, pos);
	}
    
	public PropertyValues<IOreType> getOreTypeProperty()
	{
		return this.oreType;
	}
	
	public Boolean containsOreType(IOreType type)
	{
		return this.getOreTypeProperty().getAllowedValues().contains(type);
	}
	
	public IOreType getOreType(IBlockState state)
	{
		return state.getValue(this.getOreTypeProperty());
	}
	
	public IBlockState withOreType(IOreType type)
	{
		return this.getDefaultState().withProperty(this.getOreTypeProperty(), type);
	}
    
	public IOreData getOreData()
	{
		return this.data;
	}
	
	public IOreGroup getCategory()
	{
		return this.group;
	}
	
	@Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        return state.getValue(this.getOreTypeProperty()).getSelectionBox(world, pos);
    }
	
	public BlockStateContainer getBlockState()
	{
		return this.realContainer;
	}
	
    public void checkFallable(World world, BlockPos pos)
    {
        if (world.getBlockState(pos).getValue(this.oreType).canFall() && (world.isAirBlock(pos.down()) || BlockFalling.canFallThrough(world.getBlockState(pos.down()))) && pos.getY() >= 0)
        {
            if (!BlockFalling.fallInstantly && world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
                if(!world.isRemote) world.spawnEntityInWorld(new EntityFallingBlock(world, pos.getX() + .5, pos.getY(), pos.getZ() + .5, world.getBlockState(pos)));
            else
            {
            	IBlockState state = world.getBlockState(pos);
            	world.setBlockToAir(pos);
            	do pos = pos.down();
                while((world.isAirBlock(pos) || BlockFalling.canFallThrough(world.getBlockState(pos))) && pos.getY() > 0);
                if(pos.getY() > 0) world.setBlockState(pos.up(), state);
            }
        }
    }
}
