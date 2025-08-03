package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import co.nlighten.jsontransform.functions.common.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.Map;

public class TransformerFunctionNumberParse extends TransformerFunction {
    public TransformerFunctionNumberParse() {
        super(FunctionDescription.of(
                Map.of(
                        "pattern", ArgumentType.of(ArgType.String).position(0).defaultValue("#0.00"),
                        "locale", ArgumentType.of(ArgType.String).position(1),
                        "grouping", ArgumentType.of(ArgType.String).position(2),
                        "decimal", ArgumentType.of(ArgType.String).position(3),
                        "radix", ArgumentType.of(ArgType.Number).position(1).defaultValue(10)
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        var pattern = context.getString("pattern");
        if (pattern == null) {
            return new BigDecimal(str);
        }
        if ("BASE".equals(pattern)) {
            var radix = context.getInteger("radix");
            return new BigDecimal(new BigInteger(str, radix));
        }

        var locale = context.getString("locale");
        var resolvedLocale = FunctionHelpers.isNullOrEmpty(locale)
                             ? FunctionHelpers.DEFAULT_LOCALE
                             : Locale.forLanguageTag(locale);
        var grouping = context.getString("grouping");
        var decimal = context.getString("decimal");

        var formatter = FunctionHelpers.getDecimalFormatter(resolvedLocale, pattern, grouping, decimal);
        try {
            return formatter.parse(str);
        } catch (Throwable t) {
            throw t instanceof RuntimeException ex ? ex : new RuntimeException(t);
        }
    }
}
