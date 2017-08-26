package metalextras.newores;

import metalextras.newores.modules.OreModule;

public interface ContextualPredicate<M extends OreModule<?, M>, E, V>
{
	public boolean test(Class<?> module_type, Class<?> context_type, Class<?> return_type);
	
	public static <M extends OreModule<?, M>, E, V> ContextualPredicate<M, E, V> of(Class<M> module_type, Class<E> context_type, Class<V> return_type)
	{
		return (m, c, r) -> (module_type == null || m.isAssignableFrom(module_type)) && (context_type == null || c.isAssignableFrom(context_type)) && (return_type == null || r.isAssignableFrom(return_type));
	}
}
