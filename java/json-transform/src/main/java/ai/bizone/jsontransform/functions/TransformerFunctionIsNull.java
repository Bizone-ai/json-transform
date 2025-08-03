package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.FunctionContext;
import ai.bizone.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionIsNull extends TransformerFunction {
    public TransformerFunctionIsNull() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        var value = context.getJsonElement(null, true);
        return context.getAdapter().isNull(value);
    }
}
