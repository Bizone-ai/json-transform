package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.FunctionContext;
import ai.bizone.jsontransform.functions.common.TransformerFunction;
import ai.bizone.jsontransform.JsonElementStreamer;

/*
 * For tests
 *
 * @see TransformerFunctionValueTest
 */
public class TransformerFunctionValue extends TransformerFunction {
    public TransformerFunctionValue() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        var result = context.get(null, true);
        if (result instanceof JsonElementStreamer streamer) {
            result = streamer.toJsonArray();
        }
        if (context.getAdapter().isTruthy(result)) {
            return result;
        }
        return null;
    }
}
