package metalextras.newores.modules;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import metalextras.MetalExtras;
import metalextras.newores.NewOreMaterial;
import metalextras.newores.VariableManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MaterialBlockModule extends OreModule<NewOreMaterial, MaterialBlockModule>
{
	protected IBlockState state = Blocks.AIR.getDefaultState();
	protected Function<WorldContext<MaterialBlockModule, Void>, String> name_getter = MaterialBlockModule::getNameFromContext;
	protected Function<WorldContext<MaterialBlockModule, Void>, Float> hardness_getter = MaterialBlockModule::getHarnessFromContext;
	protected Function<WorldContext<MaterialBlockModule, Explosion>, Float> resistance_getter = MaterialBlockModule::getResistanceFromContext; 
	protected Function<WorldContext<MaterialBlockModule, Void>, SoundType> sound_getter = MaterialBlockModule::getSoundFromContext;
	protected Function<WorldContext<MaterialBlockModule, Void>, String> harvest_tool_getter = MaterialBlockModule::getHarvestToolFromContext;
	protected Function<WorldContext<MaterialBlockModule, Void>, Integer> harvest_level_getter = MaterialBlockModule::getHarvestLevelFromContext;
	protected Predicate<WorldContext<MaterialBlockModule, Void>> fallable_getter = MaterialBlockModule::getFallableFromContext;
	protected Function<WorldContext<MaterialBlockModule, Void>, AxisAlignedBB> collision_box_getter = MaterialBlockModule::getCollisionBoxFromContext;
	protected Consumer<WorldContext<MaterialBlockModule, Void>> entity_collision_handler = MaterialBlockModule::handleEntityCollision;
	
	public MaterialBlockModule(String path, JsonElement json, boolean parse)
	{
		super(path, NewOreMaterial.class, MaterialBlockModule.class, json);
		if(parse)
		{
			JsonObject block_object = json.getAsJsonObject();
			this.hardness_getter = VariableManager.getFloatVar(this.module_type, Void.class, path, json, "hardness", this.hardness_getter);
			this.resistance_getter = VariableManager.getFloatVar(this.module_type, Explosion.class, path, json, "resistance", this.resistance_getter);
			this.sound_getter = VariableManager.variable(this.module_type, Void.class, SoundType.class, path, json, "sound", (e) ->
			{
				if(e.isJsonPrimitive() && e.getAsJsonPrimitive().isString())
				{
					SoundEvent sound = Optional.ofNullable(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(e.getAsString()))).orElse(SoundEvents.BLOCK_STONE_HIT);
					return ConstantFunction.of(new SoundType(1, 1, sound, sound, sound, sound, sound));
				}
				else if(e.isJsonObject())
				{
					JsonObject sound_object = e.getAsJsonObject();
					Function<WorldContext<MaterialBlockModule, Void>, Float> volume_getter = VariableManager.getFloatVar(this.module_type, Void.class, path.concat("/sound"), sound_object, "volume", ConstantFunction.of(1F));
					Function<WorldContext<MaterialBlockModule, Void>, Float> pitch_getter = VariableManager.getFloatVar(this.module_type, Void.class, path.concat("/sound"), sound_object, "pitch", ConstantFunction.of(1F));;
					Function<WorldContext<MaterialBlockModule, Void>, SoundEvent> break_sound_getter = VariableManager.getRegistryEntryVar(this.module_type, Void.class, ForgeRegistries.SOUND_EVENTS, path.concat("/sound"), json, "break", ConstantFunction.of(SoundEvents.BLOCK_STONE_BREAK));
					Function<WorldContext<MaterialBlockModule, Void>, SoundEvent> step_sound_getter = VariableManager.getRegistryEntryVar(this.module_type, Void.class, ForgeRegistries.SOUND_EVENTS, path.concat("/sound"), json, "step", ConstantFunction.of(SoundEvents.BLOCK_STONE_STEP));
					Function<WorldContext<MaterialBlockModule, Void>, SoundEvent> place_sound_getter = VariableManager.getRegistryEntryVar(this.module_type, Void.class, ForgeRegistries.SOUND_EVENTS, path.concat("/sound"), json, "place", ConstantFunction.of(SoundEvents.BLOCK_STONE_PLACE));
					Function<WorldContext<MaterialBlockModule, Void>, SoundEvent> hit_sound_getter = VariableManager.getRegistryEntryVar(this.module_type, Void.class, ForgeRegistries.SOUND_EVENTS, path.concat("/sound"), json, "hit", ConstantFunction.of(SoundEvents.BLOCK_STONE_HIT));
					Function<WorldContext<MaterialBlockModule, Void>, SoundEvent> fall_sound_getter = VariableManager.getRegistryEntryVar(this.module_type, Void.class, ForgeRegistries.SOUND_EVENTS, path.concat("/sound"), json, "fall", ConstantFunction.of(SoundEvents.BLOCK_STONE_FALL));
					return (context) -> new SoundType(volume_getter.apply(context), pitch_getter.apply(context), break_sound_getter.apply(context), step_sound_getter.apply(context), place_sound_getter.apply(context), hit_sound_getter.apply(context), fall_sound_getter.apply(context));
				}
				return null;
			}, this.sound_getter, VariableManager.JSON_STRING_NAME, VariableManager.JSON_OBJECT_NAME);
			this.harvest_tool_getter = VariableManager.getStringVar(this.module_type, Void.class, path, json, "harvest_tool", this.harvest_tool_getter);
			this.harvest_level_getter = VariableManager.getIntegerVar(this.module_type, Void.class, path, json, "harvest_level", this.harvest_level_getter);
			this.fallable_getter = VariableManager.getBooleanVar(this.module_type, Void.class, path, json, "fallable", this.fallable_getter);
			this.collision_box_getter = VariableManager.variable(this.module_type, Void.class, AxisAlignedBB.class, path, json, "collision_box", (e) ->
			{
				if(e.isJsonArray())
				{
					JsonArray collision_box_array = e.getAsJsonArray();
					if(collision_box_array.size() == 6)
					{
						double fromX = VariableManager.getDoubleConst(this.module_type, path.concat("/collision_box[0]"), collision_box_array.get(0), null, 0);
						double fromY = VariableManager.getDoubleConst(this.module_type, path.concat("/collision_box[1]"), collision_box_array.get(1), null, 0);
						double fromZ = VariableManager.getDoubleConst(this.module_type, path.concat("/collision_box[2]"), collision_box_array.get(2), null, 0);
						double toX = VariableManager.getDoubleConst(this.module_type, path.concat("/collision_box[3]"), collision_box_array.get(3), null, 1);
						double toY = VariableManager.getDoubleConst(this.module_type, path.concat("/collision_box[4]"), collision_box_array.get(4), null, 1);
						double toZ = VariableManager.getDoubleConst(this.module_type, path.concat("/collision_box[5]"), collision_box_array.get(5), null, 1);
						return ConstantFunction.of(new AxisAlignedBB(fromX, fromY, fromZ, toX, toY, toZ));
					}
					MetalExtras.LOGGER.warn(String.format("The JsonArray at %s/collision_box must be 6 elements, got %s elements instead.", path, collision_box_array.size()));
				}
				else if(e.isJsonObject())
				{
					JsonObject collision_box_object = e.getAsJsonObject();
					double[] from = VariableManager.constant(this.module_type, double[].class, path, collision_box_object, "from", (e1) ->
					{
						if(e1.isJsonArray())
						{
							JsonArray from_positions_array = e1.getAsJsonArray();
							if(from_positions_array.size() == 3)
							{
								double[] ret = new double[3];
								ret[0] = VariableManager.getDoubleConst(this.module_type, path.concat(String.format("/collision_box/from[%s]", 0)), from_positions_array.get(0), null, VariableManager.getDoubleConst(this.module_type, path.concat("/collision_box"), collision_box_object, "fromX", 0));
								ret[1] = VariableManager.getDoubleConst(this.module_type, path.concat(String.format("/collision_box/from[%s]", 1)), from_positions_array.get(1), null, VariableManager.getDoubleConst(this.module_type, path.concat("/collision_box"), collision_box_object, "fromY", 0));
								ret[2] = VariableManager.getDoubleConst(this.module_type, path.concat(String.format("/collision_box/from[%s]", 2)), from_positions_array.get(2), null, VariableManager.getDoubleConst(this.module_type, path.concat("/collision_box"), collision_box_object, "fromZ", 0));
								return ret;
							}
							MetalExtras.LOGGER.warn(String.format("The JsonArray at %s/collision_box/from must be 3 elements, got %s elements instead.", path, from_positions_array.size()));
						}
						return null;
					}, new double[] { 0, 0, 0 });
					double[] to = VariableManager.constant(this.module_type, double[].class, path, collision_box_object, "to", (e1) ->
					{
						if(e1.isJsonArray())
						{
							JsonArray to_positions_array = e1.getAsJsonArray();
							if(to_positions_array.size() == 3)
							{
								double[] ret = new double[3];
								ret[0] = VariableManager.getDoubleConst(this.module_type, path.concat(String.format("/collision_box/to[%s]", 0)), to_positions_array.get(0), null, VariableManager.getDoubleConst(this.module_type, path.concat("/collision_box"), collision_box_object, "toX", 1));
								ret[1] = VariableManager.getDoubleConst(this.module_type, path.concat(String.format("/collision_box/to[%s]", 1)), to_positions_array.get(1), null, VariableManager.getDoubleConst(this.module_type, path.concat("/collision_box"), collision_box_object, "toY", 1));
								ret[2] = VariableManager.getDoubleConst(this.module_type, path.concat(String.format("/collision_box/to[%s]", 2)), to_positions_array.get(2), null, VariableManager.getDoubleConst(this.module_type, path.concat("/collision_box"), collision_box_object, "toZ", 1));
								return ret;
							}
							MetalExtras.LOGGER.warn(String.format("The JsonArray at %s/collision_box/to must be 3 elements, got %s elements instead.", path, to_positions_array.size()));
						}
						return null;
					}, new double[] { 1, 1, 1 });
					return ConstantFunction.of(new AxisAlignedBB(from[0], from[1], from[2], to[0], to[1], to[2]));
				}
				return null;
			}, this.collision_box_getter, VariableManager.JSON_ARRAY_NAME, VariableManager.JSON_OBJECT_NAME);
			//TODO put a entity collision handler
		}
	}
	
	public final IBlockState getState()
	{
		return this.state;
	}
	
	public final String getName()
	{
		return this.name_getter.apply(WorldContext.of(this, this.state));
	}
	
	public final float getHardness(World world, BlockPos pos)
	{
		return this.hardness_getter.apply(WorldContext.of(this, this.state, world, pos));
	}
	
	public final SoundType getSound(World world, BlockPos pos, Entity entity)
	{
		return this.sound_getter.apply(WorldContext.of(this, this.state, world, pos, entity));
	}
	
	public final String getHarvestTool()
	{
		return this.harvest_tool_getter.apply(WorldContext.of(this, this.state));
	}
	
	public final int getHarvestLevel()
	{
		return this.harvest_level_getter.apply(WorldContext.of(this, this.state));
	}
	
	public final boolean canFall()
	{
		return this.fallable_getter.test(WorldContext.of(this, this.state));
	}
	
	public final AxisAlignedBB getCollisionBox(World world, BlockPos pos)
	{
		return this.collision_box_getter.apply(WorldContext.of(this, this.state, world, pos));
	}
	
	public final void handleEntityCollision(World world, BlockPos pos, Entity entity)
	{
		this.entity_collision_handler.accept(WorldContext.of(this, this.state, world, pos, entity));
	}
	
	public static String getNameFromContext(WorldContext<MaterialBlockModule, ?> context)
	{
		return context.state.getBlock().getUnlocalizedName();
	}
	
	public static float getHarnessFromContext(WorldContext<MaterialBlockModule, ?> context)
	{
		return context.processIfWorldPresent((world, context1) -> context1.state.getBlockHardness(world, context1.pos)).orElse(0F);
	}
	
	public static float getResistanceFromContext(WorldContext<MaterialBlockModule, Explosion> context)
	{
		return context.processIfWorldPresent((world, context1) -> context1.state.getBlock().getExplosionResistance(world, context1.pos, context1.entity, context1.extra)).orElse(0F);
	}
	
	public static SoundType getSoundFromContext(WorldContext<MaterialBlockModule, ?> context)
	{
		return context.processIfWorldPresent((world, context1) -> context1.state.getBlock().getSoundType(context1.state, world, context1.pos, context1.entity)).orElse(SoundType.STONE);
	}
	
	public static String getHarvestToolFromContext(WorldContext<MaterialBlockModule, ?> context)
	{
		return context.state.getBlock().getHarvestTool(context.state);
	}
	
	public static int getHarvestLevelFromContext(WorldContext<MaterialBlockModule, ?> context)
	{
		return context.state.getBlock().getHarvestLevel(context.state);
	}
	
	public static boolean getFallableFromContext(WorldContext<MaterialBlockModule, ?> context)
	{
		return context.state.getBlock() instanceof BlockFalling;
	}
	
	public static AxisAlignedBB getCollisionBoxFromContext(WorldContext<MaterialBlockModule, ?> context)
	{
		return context.processIfWorldPresent((world, context1) -> context1.state.getCollisionBoundingBox(world, context1.pos)).orElse(Block.FULL_BLOCK_AABB);
	}
	
	public static void handleEntityCollision(WorldContext<MaterialBlockModule, ?> context)
	{
		context.<Void>processIfWorldPresent((world, context1) -> 
		{
			context1.state.getBlock().onEntityCollidedWithBlock(world, context1.pos, context1.state, context1.entity);
			return null;
		});
	}
}