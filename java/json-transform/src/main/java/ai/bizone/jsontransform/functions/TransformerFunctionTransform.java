package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionTransform extends TransformerFunction {
    public TransformerFunctionTransform() {
        super(FunctionDescription.of(
            Map.of(
            "to", ArgumentType.of(ArgType.Any).position(0)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var input = context.getJsonElement(null);
        var to = context.getJsonElement("to", false);
        return context.transformItem(to, input);
    }
}
