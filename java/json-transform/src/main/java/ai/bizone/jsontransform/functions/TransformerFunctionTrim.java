package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionTrim extends TransformerFunction {
    public TransformerFunctionTrim() {
        super(FunctionDescription.of(
                Map.of(
                        "type", ArgumentType.of(ArgType.String).position(0).defaultValue("BOTH")
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        return switch (context.getEnum("type")) {
            case "START" -> str.stripLeading();
            case "END" -> str.stripTrailing();
            case "INDENT" -> str.stripIndent();
            case "JAVA" -> str.trim();
            default -> str.strip();
        };
    }
}
