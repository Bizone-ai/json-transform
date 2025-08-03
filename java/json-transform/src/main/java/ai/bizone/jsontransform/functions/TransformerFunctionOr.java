package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.FunctionContext;
import ai.bizone.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionOr extends TransformerFunction {
    public TransformerFunctionOr() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        var value = context.getJsonElementStreamer(null);
        var adapter = context.getAdapter();
        return value.stream().anyMatch(adapter::isTruthy);
    }
}
