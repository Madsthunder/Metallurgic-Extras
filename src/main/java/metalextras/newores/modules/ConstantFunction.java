package metalextras.newores.modules;

import java.util.function.Function;

public class ConstantFunction<I, O> implements Function<I, O>
{
	private final O output;
	
	public ConstantFunction(O output)
	{
		this.output = output;
	}
	
	@Override
	public O apply(I input)
	{
		return this.output;
	}
	
	public static <I, O> Function<I, O> of(O output)
	{
		return new ConstantFunction<I, O>(output);
	}
}
