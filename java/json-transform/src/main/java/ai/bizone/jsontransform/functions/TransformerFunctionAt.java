package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionAt extends TransformerFunction {
    public TransformerFunctionAt() {
        super(FunctionDescription.of(
            Map.of("index", ArgumentType.of(ArgType.Number).position(0))
        ));
    }

    @Override
    public Object apply(FunctionContext context) {
        var value = context.getJsonElementStreamer(null);
        var index = context.getInteger("index");
        if (index == null) {
            return null;
        }
        if (index == 0) {
            return value.stream().findFirst().orElse(null);
        }
        if (index > 0) {
            return value.stream(index.longValue(), null).findFirst().orElse(null);
        }
        // negative
        var arr = value.toJsonArray();
        var adapter = context.getAdapter();
        return adapter.get(arr, adapter.size(arr) + index);
    }
}
