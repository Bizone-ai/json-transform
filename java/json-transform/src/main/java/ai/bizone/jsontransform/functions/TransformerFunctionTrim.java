package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionTrim extends TransformerFunction {
    public TransformerFunctionTrim() {
        super(FunctionDescription.of(
                Map.of(
                    "type", ArgumentType.of(ArgType.String).position(0).defaultValue("BOTH"),
                    "what", ArgumentType.of(ArgType.String).position(1)
                )
        ));
    }

    private int indexOfFirstGood(String input, String badCharacters) {
        int i = 0;
        while (i < input.length()) {
            var cp = input.codePointAt(i);
            if (badCharacters.indexOf(cp) < 0) {
                return i;
            }
            i += Character.charCount(cp);
        }
        return i;
    }

    private int indexOfLastGood(String input, String badCharacters) {
        var codePoints = input.codePoints().toArray();
        int i = codePoints.length;
        for (int j = codePoints.length - 1; j >= 0; j--) {
            var cp = codePoints[j];
            if (badCharacters.indexOf(cp) < 0) {
                return i - Character.charCount(cp);
            }
            i -= Character.charCount(cp);
        }
        return -1;
    }

    @Override
    public Object apply(FunctionContext context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        var type = context.getEnum("type");
        if ("JAVA".equals(type)) {
            return str.trim();
        }
        if ("INDENT".equals(type)) {
            return str.stripIndent();
        }

        var what = context.getString("what");
        if (what != null) {
            return switch (type) {
                case "START" -> str.substring(indexOfFirstGood(str, what));
                case "END" -> str.substring(0, indexOfLastGood(str, what) + 1);
                default -> str.substring(indexOfFirstGood(str, what), indexOfLastGood(str, what) + 1);
            };
        }
        return switch (type) {
            case "START" -> str.stripLeading();
            case "END" -> str.stripTrailing();
            default -> str.strip();
        };
    }
}
