package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionString extends TransformerFunction {
    public TransformerFunctionString() {
        super(FunctionDescription.of(
            Map.of(
            "json", ArgumentType.of(ArgType.Boolean).position(0).defaultValue(false)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var value = context.getUnwrapped(null);
        if (context.getBoolean("json")) {
            // although gson.toJson will return "null" eventually, this is quicker
            if (value == null) {
                return "null";
            }
            var adapter = context.getAdapter();
            return adapter.toString(value);
        }
        return context.getAsString(value);
    }
}
