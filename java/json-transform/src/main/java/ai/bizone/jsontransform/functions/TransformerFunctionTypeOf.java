package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionTypeOf extends TransformerFunction {

    public TransformerFunctionTypeOf() {
        super();
    }

    @Override
    public Object apply(FunctionContext context) {
        var adapter = context.getAdapter();
        var value = context.getJsonElement(null);
        if (value == null || adapter.isNull(value)) return "null";
        if (adapter.isJsonString(value)) return "string";
        if (adapter.isJsonNumber(value)) return "number";
        if (adapter.isJsonBoolean(value)) return "boolean";
        if (adapter.isJsonArray(value)) return "array";
        return "object";
    }
}
