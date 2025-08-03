package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.FunctionContext;
import ai.bizone.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionLower extends TransformerFunction {
    public TransformerFunctionLower() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        var result = context.getString(null);
        return result == null ? null : result.toLowerCase();
    }
}
