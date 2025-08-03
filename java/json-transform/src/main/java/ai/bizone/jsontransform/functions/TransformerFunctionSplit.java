package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionSplit extends TransformerFunction {
    public TransformerFunctionSplit() {
        super(FunctionDescription.of(
                Map.of(
                        "delimiter", ArgumentType.of(ArgType.String).position(0).defaultValue(""),
                        "limit", ArgumentType.of(ArgType.Number).position(1).defaultValue(0)
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        return str.split(context.getString("delimiter"), context.getInteger("limit"));
    }
}
