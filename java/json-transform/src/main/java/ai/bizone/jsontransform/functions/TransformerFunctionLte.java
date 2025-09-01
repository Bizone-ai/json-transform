package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionLte extends TransformerFunction {

    public TransformerFunctionLte() {
        super(FunctionDescription.of(
            Map.of(
            "value", ArgumentType.of(ArgType.Any).position(0),
            "strict", ArgumentType.of(ArgType.Boolean).position(1).defaultValue(false)
            )
        ));
    }

    @Override
    public Object apply(FunctionContext context) {
        var adapter = context.getAdapter();
        var value = context.getJsonElement("value");
        var strict = context.has("strict") && context.getBoolean("strict");
        var compareValue = !strict && adapter.isJsonNumber(value)
                ? FunctionHelpers.nullableBigDecimalJsonPrimitive(adapter, () -> context.getBigDecimal(null))
                : context.getJsonElement(null);
        var comparison = adapter.compareTo(value, compareValue);
        return comparison != null && comparison <= 0;
    }
}
