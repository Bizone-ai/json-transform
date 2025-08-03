package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionReplace extends TransformerFunction {
    public TransformerFunctionReplace() {
        super(FunctionDescription.of(
                Map.of(
                        "find", ArgumentType.of(ArgType.String).position(0).defaultValue(""),
                        "replacement", ArgumentType.of(ArgType.String).position(1).defaultValue(""),
                        "type", ArgumentType.of(ArgType.String).position(2).defaultValue("STRING"),
                        "from", ArgumentType.of(ArgType.Number).position(3).defaultValue(0)
                )
        ));
    }
    private static String replaceOnce(String value, String find, String replacement, Integer fromIndex) {
        int index = value.indexOf(find, fromIndex);
        if (index == -1) {
            return value;
        }
        return value.substring(0, index)
                .concat(replacement)
                .concat(value.substring(index + find.length()));
    }

    @Override
    public Object apply(FunctionContext context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        var find = context.getString("find");
        if (find == null) {
            return str;
        }
        var replacement = context.getString("replacement");
        var from = context.getInteger("from");
        var validFrom = from > 0 && str.length() > from;
        return switch (context.getEnum("type")) {
            case "FIRST" -> replaceOnce(str, find, replacement, validFrom ? from : 0);
            case "REGEX" -> validFrom
                            ? str.substring(0, from) + str.substring(from).replaceAll(find, replacement)
                            : str.replaceAll(find, replacement);
            case "REGEX-FIRST" -> validFrom
                                  ? str.substring(0, from) + str.substring(from).replaceFirst(find, replacement)
                                  : str.replaceFirst(find, replacement);
            default -> validFrom
                       ? str.substring(0, from) + str.substring(from).replace(find, replacement)
                       : str.replace(find, replacement);
        };
    }
}
