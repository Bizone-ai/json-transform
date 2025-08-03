package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.FunctionContext;
import ai.bizone.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionLong extends TransformerFunction {
    public TransformerFunctionLong() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        return context.getLong(null);
    }
}
