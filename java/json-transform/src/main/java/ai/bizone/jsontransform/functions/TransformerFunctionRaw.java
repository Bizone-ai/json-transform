package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.FunctionContext;
import ai.bizone.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionRaw extends TransformerFunction {
    public TransformerFunctionRaw() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        return context.getJsonElement(null, false);
    }
}
