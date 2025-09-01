package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionIn extends TransformerFunction {

    public TransformerFunctionIn() {
        super(FunctionDescription.of(
            Map.of(
            "value", ArgumentType.of(ArgType.Any).position(0)
            )
        ));
    }

    @Override
    public Object apply(FunctionContext context) {
        var value = context.getJsonElement("value");
        var collection = context.getJsonElementStreamer(null);
        var adapter = context.getAdapter();
        return collection != null && collection.stream().anyMatch(other -> adapter.areEqual(value, other));
    }
}
