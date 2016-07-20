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
import continuum.metalextras.items.ItemBlockOre;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockStateContainer.Builder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BlockOreRock extends CTBlock implements IOre
{
	public static final PropertyEnum<EnumRockType> stoneType = PropertyEnum.<EnumRockType>create("type", EnumRockType.class);
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
	
	public BlockOreRock(MetalExtras_OH objectHolder, String name,  Integer index)
	{
		super(Material.ROCK, name + "_ore_rock", ItemBlockOre.class);
		this.name = name + "_ore";
		this.objectHolder = objectHolder;
		this.setSoundType(SoundType.STONE);
		this.setUnlocalizedName(name + "_ore_rock");
		this.setDefaultState(this.getDefaultState().withProperty(stoneType, EnumRockType.STONE));
		for(EnumRockType type : EnumRockType.values())
			this.setHarvestLevel("pickaxe", index, this.getDefaultState().withProperty(stoneType, type));
	}
	
	public BlockOreRock(MetalExtras_OH objectHolder, String name, String itemDropped, Integer minItems, Integer maxItems, Integer fortuneAdditive, Integer minXP, Integer maxXP, Integer index)
	{
		this(objectHolder, name, index);
		this.itemDropped = itemDropped;
		this.minItemDrops = minItems;
		this.maxItemDrops = maxItems;
		this.fortuneAdditive = fortuneAdditive;
		this.minXP = minXP;
		this.maxXP = maxXP;
	}
	
	public BlockOreRock(MetalExtras_OH objectHolder, String name, String itemDropped, Integer minItems, Integer maxItems, Integer metaDropped, Integer fortuneAdditive, Integer minXP, Integer maxXP, Integer index)
	{
		this(objectHolder, name, itemDropped, minItems, maxItems, fortuneAdditive, minXP, maxXP, index);
		this.metaDropped = metaDropped;
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(stoneType).ordinal();
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult result, World world, BlockPos pos, EntityPlayer player)
	{
		return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(stoneType).ordinal());
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(stoneType, EnumRockType.values()[meta]);
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
		return this.itemDropped == null ? this.objectHolder.ores.indexOf(this.name) : this.metaDropped;
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
		IBlockState rState = state.getValue(stoneType).states.get(0);
		Block block = rState.getBlock();
		if(this.itemDropped == null)
			list.add(new ItemStack(block.getItemDropped(rState, new Random(), 0), block.quantityDropped(new Random()), block.damageDropped(rState)));
		return list;
    }
    
    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos)
    {
    	return state.getValue(stoneType).hardness;
    }
    
    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity entity, Explosion explosion)
    {
    	return world.getBlockState(pos).getValue(stoneType).resistance * 3 / 5;
    }
	
	@Override
	public int getExpDrop(IBlockState state, IBlockAccess access, BlockPos pos, int fortune)
	{
		return MathHelper.getRandomIntegerInRange(new Random(), this.minXP, this.maxXP);
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		for(EnumRockType type : EnumRockType.values())
			list.add(new ItemStack(item, 1, type.ordinal()));
	}
	
	@Override
	public BlockStateContainer createBlockState()
	{
		return new Builder(this).add(stoneType).build();
	}
	
	public static enum EnumRockType implements IStringSerializable
	{
		STONE(Blocks.STONE, 0, 1.5F, 10.0F),
		GRANITE(Blocks.STONE, 1, 1.5F, 10.0F),
		DIORITE(Blocks.STONE, 3, 1.5F, 10.0F),
		ANDESITE(Blocks.STONE, 5, 1.5F, 10.0F),
		SANDSTONE(Blocks.SANDSTONE, 0.8F, 0F),
		RED_SANDSTONE(Blocks.RED_SANDSTONE, 0.8F, 0F),
		NETHERRACK(Blocks.NETHERRACK, 0.4F, 0.0F),
		END_STONE(Blocks.END_STONE, 3.0F, 15.0F),
		BEDROCK(Blocks.BEDROCK, -1.0F, 6000000.0F);
		public final List<IBlockState> states;
		public final Float hardness;
		public final Float resistance;
		private EnumRockType(Block block, Float hardness, Float resistance)
		{
			this(block.getBlockState().getValidStates(), hardness, resistance);
		}
		
		private EnumRockType(Block block, Integer meta, Float hardness, Float resistance)
		{
			this(new ImmutableList.Builder<IBlockState>().add(block.getStateFromMeta(meta)).build(), hardness, resistance);
		}
		
		private EnumRockType(List<IBlockState> states, Float hardness, Float resistance)
		{
			this.states = states;
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
			return this.properties.materials[((EnumRockType)obj).ordinal()];
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
		Object o;
		if(dimension == DimensionType.NETHER && (o = extraObjects.get("canSpawnInNether")) instanceof Boolean)
			return (Boolean)o;
		if(dimension == DimensionType.THE_END && (o = extraObjects.get("canSpawnInEnd")) instanceof Boolean)
			return (Boolean)o;
		if((o = extraObjects.get("canSpawnIn" + biome.getBiomeName())) instanceof Boolean)
			return (Boolean)o;
		if((o = extraObjects.get("temperatureLessThanOrEqualTo")) instanceof Double)
			return biome.getTemperature() >= (Double)o;
		if((o = extraObjects.get("temperatureGreaterThanOrEqualTo")) instanceof Double)
			return biome.getTemperature() <= (Double)o;
		return true;
	}
}
