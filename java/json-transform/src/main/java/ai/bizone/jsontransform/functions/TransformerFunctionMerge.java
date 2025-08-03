package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.adapters.JsonAdapter;
import ai.bizone.jsontransform.functions.common.*;
import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;
import java.util.stream.Stream;

public class TransformerFunctionMerge extends TransformerFunction {

    public TransformerFunctionMerge() {
        super(FunctionDescription.of(
            Map.of(
                "deep", ArgumentType.of(ArgType.Boolean).position(0).defaultValue(false),
                "arrays", ArgumentType.of(ArgType.Boolean).position(1).defaultValue(false)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null || streamer.knownAsEmpty())
            return null;

        var deep = context.getBoolean("deep");
        var arrays = context.getBoolean("arrays");

        var options = new JsonAdapter.JsonMergeOptions(deep, arrays);

        var adapter = context.getAdapter();
        var result = adapter.createObject();

        return ((Stream<Object>)streamer.stream())
                .reduce(result, (acc, value) -> adapter.merge(acc, value, options));
    }
}
