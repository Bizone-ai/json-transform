package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;
import java.util.Optional;

public class TransformerFunctionWrap extends TransformerFunction {
    public TransformerFunctionWrap() {
        super(FunctionDescription.of(
                Map.of(
                    "prefix", ArgumentType.of(ArgType.String).position(0).defaultValue(""),
                    "suffix", ArgumentType.of(ArgType.String).position(1).defaultValue("")
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var res = context.getString(null);
        if (res == null)
            return null;
        return Optional.ofNullable(context.getString("prefix")).orElse("")
                + res
                + Optional.ofNullable(context.getString("suffix")).orElse("");
    }
}
