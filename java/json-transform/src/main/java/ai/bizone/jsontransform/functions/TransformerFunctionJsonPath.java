package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;

import java.util.*;

public class TransformerFunctionJsonPath extends TransformerFunction {
    public TransformerFunctionJsonPath() {
        super(FunctionDescription.of(
                Map.of(
                        "path", ArgumentType.of(ArgType.String).position(0),
                        "options", ArgumentType.of(ArgType.Array).position(1)
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var source = context.getJsonElement(null);
        if (source == null) {
            return null;
        }
        var path = context.getString("path");
        if (path == null) {
            return null;
        }
        var adapter = context.getAdapter();
        var optionsArray = context.getJsonArray("options");
        List<String> options = null;
        if (optionsArray != null && !adapter.isEmpty(optionsArray)) {
            options = new ArrayList<>();
            for (var option : adapter.asIterable(optionsArray)) {
                options.add(adapter.getAsString(option));
            }
        }
        return adapter.getDocumentContext(source, options).read(path);
    }
}
