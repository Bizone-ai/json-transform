package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.FunctionContext;
import ai.bizone.jsontransform.functions.common.TransformerFunction;
import ai.bizone.jsontransform.JsonElementStreamer;

public class TransformerFunctionReverse extends TransformerFunction {
    public TransformerFunctionReverse() {
        super();
    }
    private  <T> int compare(T a, T b) {
        return -1;
    }

    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null) {
            return null;
        }
        return JsonElementStreamer.fromTransformedStream(context, streamer.stream().sorted(this::compare));
    }
}
