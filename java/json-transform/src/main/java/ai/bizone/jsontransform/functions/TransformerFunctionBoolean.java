package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionBoolean extends TransformerFunction {
    public TransformerFunctionBoolean() {
        super(FunctionDescription.of(
            Map.of(
            "style", ArgumentType.of(ArgType.String).position(0).defaultValue("JAVA")
            )
        ));
    }

    @Override
    public Object apply(FunctionContext context) {
        var jsStyle = "JS".equals(context.getEnum("style"));
        var adapter = context.getAdapter();
        return adapter.isTruthy(context.getUnwrapped(null), jsStyle);
    }
}
