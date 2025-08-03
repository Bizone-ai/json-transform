package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;

import java.math.BigDecimal;
import java.util.Map;

public class TransformerFunctionIs extends TransformerFunction {

    public TransformerFunctionIs() {
        super(FunctionDescription.of(
            Map.of(
            "op", ArgumentType.of(ArgType.String).position(0),
            "that", ArgumentType.of(ArgType.Any).position(1),
            "in", ArgumentType.of(ArgType.Array),
            "nin", ArgumentType.of(ArgType.Array),
            "eq", ArgumentType.of(ArgType.Any),
            "neq", ArgumentType.of(ArgType.Any),
            "gt", ArgumentType.of(ArgType.Any),
            "gte", ArgumentType.of(ArgType.Any),
            "lt", ArgumentType.of(ArgType.Any),
            "lte", ArgumentType.of(ArgType.Any)
            )
        ));
    }
    private Object nullableBigDecimalJsonPrimitive(FunctionContext context, BigDecimal number) {
        var adapter = context.getAdapter();
        return number == null ? adapter.jsonNull() : adapter.wrap(number);
    }

    @Override
    public Object apply(FunctionContext context) {
        var value = context.getJsonElement(null);
        var adapter = context.getAdapter();
        if (context.has("op")) {
            var op = context.getEnum("op");
            Object that = null;
            // if operator is not in/nin then prepare the "that" argument which is a JsonElement
            if (!"IN".equals(op) && !"NIN".equals(op)) {
                that = adapter.isJsonNumber(value)
                       ? nullableBigDecimalJsonPrimitive(context, context.getBigDecimal("that"))
                       : context.getJsonElement("that");
            }
            return switch (op) {
                case "IN" -> {
                    var in = context.getJsonElementStreamer("that");
                    yield in != null && in.stream().anyMatch(other -> adapter.areEqual(value, other));
                }
                case "NIN" -> {
                    var nin = context.getJsonElementStreamer("that");
                    yield nin != null && nin.stream().noneMatch(other -> adapter.areEqual(value, other));
                }
                case "GT",">" -> {
                    var comparison = adapter.compareTo(value, that);
                    yield comparison != null && comparison > 0;
                }
                case "GTE",">=" -> {
                    var comparison = adapter.compareTo(value, that);
                    yield comparison != null && comparison >= 0;
                }
                case "LT","<" -> {
                    var comparison = adapter.compareTo(value, that);
                    yield comparison != null && comparison < 0;
                }
                case "LTE","<=" -> {
                    var comparison = adapter.compareTo(value, that);
                    yield comparison != null && comparison <= 0;
                }
                case "EQ","=","==" -> adapter.areEqual(value, that);
                case "NEQ","!=","<>" -> !adapter.areEqual(value, that);
                default -> false;
            };
        }
        var result = true;
        if (context.has("in")) {
            var in = context.getJsonElementStreamer("in");
            result = in != null && in.stream().anyMatch(other -> adapter.areEqual(value, other));
        }
        if (result && context.has("nin")) {
            var nin = context.getJsonElementStreamer("nin");
            result = nin != null && nin.stream().noneMatch(other -> adapter.areEqual(value, other));
        }
        if (result && context.has("gt")) {
            var gt = context.getJsonElement("gt");
            var comparison = adapter.compareTo(value, gt);
            result = comparison != null && comparison > 0;
        }
        if (result && context.has("gte")) {
            var gte = context.getJsonElement("gte");
            var comparison = adapter.compareTo(value, gte);
            result = comparison != null && comparison >= 0;
        }
        if (result && context.has("lt")) {
            var lt = context.getJsonElement("lt");
            var comparison = adapter.compareTo(value, lt);
            result = comparison != null && comparison < 0;
        }
        if (result && context.has("lte")) {
            var lte = context.getJsonElement("lte");
            var comparison = adapter.compareTo(value, lte);
            result = comparison != null && comparison <= 0;
        }
        if (result && context.has("eq")) {
            var eq = context.getJsonElement("eq");
            result = adapter.areEqual(value, eq);
        }
        if (result && context.has("neq")) {
            var neq = context.getJsonElement("neq");
            result = !adapter.areEqual(value, neq);
        }
        return result;
    }
}
