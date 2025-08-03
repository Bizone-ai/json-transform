package ai.bizone.jsontransform.functions.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all transformer functions.
 */
public abstract class TransformerFunction {
    private final Map<String, Object> defaultValues;
    private final FunctionDescription description;

    public TransformerFunction(FunctionDescription description) {
        this.description = description;
        this.defaultValues = new HashMap<>();
        var args = description.getArguments();
        for (var arg : args.keySet()) {
            this.defaultValues.put(arg, args.get(arg).defaultValue);
        }
    }

    public TransformerFunction() {
        this(FunctionDescription.of(Collections.emptyMap()));
    }

    /**
     * Apply the function to the given context.
     * @param context the context
     * @return the result of the function
     */
    public abstract Object apply(FunctionContext context);

    /**
     * Get the argument type for the given name.
     * @param name the name of the argument (null will return the primary argument)
     * @return the argument type or null if not found
     */
    public ArgumentType getArgument(String name) {
        if (name == null) return null;
        return description.getArguments().get(name);
    }

    /**
     * Get the arguments for this function.
     * @return the function arguments
     */
    public Map<String, ArgumentType> getArguments() {
        return description.getArguments();
    }

    /**
     * Get the default value for the given argument name.
     * @param name the argument name
     * @return the default value or null if not found
     */
    public Object getDefaultValue(String name) {
        if (name == null) return null;
        return defaultValues.get(name);
    }
}
