package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionIndexOf extends TransformerFunction {
    public TransformerFunctionIndexOf() {
        super(FunctionDescription.of(
            Map.of(
            "substring", ArgumentType.of(ArgType.String).position(0),
            "from", ArgumentType.of(ArgType.Number).position(1)
            )));
    }
    @Override
    public Object apply(FunctionContext context) {
        var str = context.getString(null);
        var of = context.getString("substring");
        var from = context.getInteger("from");
        if (str == null || of == null) {
            return -1;
        }

        return from != null ? str.indexOf(of, from) : str.indexOf(of);
    }
}
