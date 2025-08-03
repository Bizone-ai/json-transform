package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionSubstring extends TransformerFunction {
    public TransformerFunctionSubstring() {
        super(FunctionDescription.of(
            Map.of(
            "begin", ArgumentType.of(ArgType.Number).position(0).defaultValue(0),
            "end", ArgumentType.of(ArgType.Number).position(1)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        var length = str.length();
        var beginIndex = context.getInteger("begin");
        if (beginIndex == null) {
            return str;
        }
        if (beginIndex < 0) {
            beginIndex = Math.max(0, length + beginIndex);
        }
        var endValue = context.getInteger("end");
        if (endValue == null) {
            return str.substring(beginIndex);
        }
        var endIndex = Math.min(endValue, length);
        if (endIndex < 0) {
            endIndex = Math.max(0, length + endIndex);
        }
        return str.substring(beginIndex, endIndex);
    }
}
