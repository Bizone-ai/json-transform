package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

public class TransformerFunctionRandom extends TransformerFunction {
    final Random random = new Random();

    public TransformerFunctionRandom() {
        super(FunctionDescription.of(
            Map.of(
            "min", ArgumentType.of(ArgType.Number).position(0).defaultValue(BigDecimal.ZERO),
            "max", ArgumentType.of(ArgType.Number).position(1).defaultValue(BigDecimal.ONE)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        BigDecimal min;
        BigDecimal max;
        if (context.has("min")) {
            min = context.getBigDecimal("min");
            max = context.getBigDecimal("max");
        } else {
            var arr = context.getJsonArray(null);
            var adapter = context.getAdapter();
            var length = adapter.size(arr);
            min = length > 0 ? adapter.getNumberAsBigDecimal(adapter.get(arr, 0)) : null;
            max = length > 1 ? adapter.getNumberAsBigDecimal(adapter.get(arr, 1)) : null;
        }
        // sanity check
        if (min == null || max == null || max.compareTo(min) < 0) {
            return null;
        }
        var rand = BigDecimal.valueOf(random.nextDouble());
        var diff = max.subtract(min);
        var result = min.add(diff.multiply(rand, FunctionHelpers.DEFAULT_MATH_CONTEXT));
        // cap scale at max
        if (result.scale() > FunctionHelpers.MAX_SCALE) {
            result = result.setScale(FunctionHelpers.MAX_SCALE, FunctionHelpers.MAX_SCALE_ROUNDING);
        }
        return result;
    }
}
