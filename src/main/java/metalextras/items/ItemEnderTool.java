package metalextras.items;

public class ItemEnderTool extends ItemTool
{
	public ItemEnderTool(String tool_class, float entity_damage, float attack_speed, Object... effective_objects)
	{
		super(tool_class, 4, 8F, 16, "gemEnder", entity_damage, attack_speed, 1561, effective_objects);
	}
}
