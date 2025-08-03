package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.JsonElementStreamer;
import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;

import java.util.Map;
import java.util.stream.Stream;

public class TransformerFunctionRepeat extends TransformerFunction {
    public TransformerFunctionRepeat() {
        super(FunctionDescription.of(
            Map.of(
                "count", ArgumentType.of(ArgType.Number).position(0)
            )
        ));
    }

    @Override
    public Object apply(FunctionContext context) {
        var value = context.getJsonElement(null);
        if (value == null) {
            return null;
        }
        var count = context.getInteger("count");
        if (count == null || count < 0) {
            return null;
        }

        return JsonElementStreamer.fromTransformedStream(context,
            Stream.generate(() -> value).limit(count)
        );
    }
}
