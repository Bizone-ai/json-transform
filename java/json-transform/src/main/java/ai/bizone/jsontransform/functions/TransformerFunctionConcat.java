package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.FunctionContext;
import ai.bizone.jsontransform.functions.common.TransformerFunction;
import ai.bizone.jsontransform.JsonElementStreamer;

import java.util.stream.Stream;

public class TransformerFunctionConcat extends TransformerFunction {
    public TransformerFunctionConcat() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null) return null;

        var adapter = context.getAdapter();
        return JsonElementStreamer.fromTransformedStream(context, streamer.stream()
            .flatMap(itm -> {
                if (adapter.isNull(itm)) {
                    return Stream.empty();
                } else if (adapter.isJsonArray(itm)) {
                    return adapter.stream(itm);
                }
                return Stream.of(itm);
            }));
    }
}
