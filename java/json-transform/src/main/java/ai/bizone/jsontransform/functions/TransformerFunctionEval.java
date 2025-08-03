package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.FunctionContext;
import ai.bizone.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionEval extends TransformerFunction {

    public TransformerFunctionEval() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        var eval = context.getJsonElement(null,true);
        return context.transform(context.getPath() + "/.", eval, true);
    }
}
