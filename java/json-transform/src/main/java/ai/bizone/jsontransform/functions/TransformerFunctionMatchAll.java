package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

public class TransformerFunctionMatchAll extends TransformerFunction {
    public TransformerFunctionMatchAll() {
        super(FunctionDescription.of(
            Map.of(
                "pattern", ArgumentType.of(ArgType.String).position(0),
                "group", ArgumentType.of(ArgType.Number).position(1).defaultValue(0)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        var patternString = context.getString("pattern");
        if (patternString == null) {
            return null;
        }
        var matcher = Pattern.compile(patternString).matcher(str);
        var group = context.getInteger("group");
        var allMatches = new ArrayList<>();
        while (matcher.find()) {
            allMatches.add(matcher.group(group));
        }
        return allMatches.size() == 0 ? null : allMatches ;
    }
}
