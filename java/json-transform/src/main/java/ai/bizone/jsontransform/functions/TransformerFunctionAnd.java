package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.FunctionContext;
import ai.bizone.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionAnd extends TransformerFunction {
    public TransformerFunctionAnd() {
        super();
    }

    @Override
    public Object apply(FunctionContext context) {
        var adapter = context.getAdapter();
        var value = context.getJsonElementStreamer(null);
        if (value == null) {
            return false;
        }
        return value.stream().allMatch(adapter::isTruthy);
    }
}
