package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.FunctionContext;
import ai.bizone.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionXor extends TransformerFunction {
    public TransformerFunctionXor() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        var value = context.getJsonElementStreamer(null);
        var adapter = context.getAdapter();
        return value.stream().filter(adapter::isTruthy).count() == 1;
    }
}
