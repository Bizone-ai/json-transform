package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.manipulation.JsonPatch;

import java.util.Map;

public class TransformerFunctionJsonPatch extends TransformerFunction {

    public TransformerFunctionJsonPatch() {
        super(FunctionDescription.of(
                Map.of(
                        "ops", ArgumentType.of(ArgType.Array).position(0)
                )
        ));
    }

    @Override
    public Object apply(FunctionContext context) {
        var source = context.getJsonElement(null);
        if (source == null) {
            return null;
        }
        return new JsonPatch(context.getAdapter()).patch(context.getJsonArray("ops"), source);
    }
}
