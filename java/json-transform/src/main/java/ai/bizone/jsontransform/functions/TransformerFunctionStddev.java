package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class TransformerFunctionStddev extends TransformerFunction {

    public TransformerFunctionStddev() {
        super(FunctionDescription.of(
            Map.of(
            "default", ArgumentType.of(ArgType.Number).position(0).defaultValue(BigDecimal.ZERO),
            "by", ArgumentType.of(ArgType.Any).position(1),
            "population", ArgumentType.of(ArgType.Boolean).position(2).defaultValue(false)
            )
        ));
    }

    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null || streamer.knownAsEmpty())
            return null;
        var hasBy = context.has("by");
        var by = context.getJsonElement( "by", false);
        var population = context.getBoolean( "population");
        var _default = Objects.requireNonNullElse(context.getBigDecimal("default"), BigDecimal.ZERO);
        var size = new AtomicInteger(0);
        var adapter = context.getAdapter();
        var values = streamer.stream()
                .map(t -> {
                    size.getAndIncrement();
                    var res = hasBy ? context.transformItem(by, t) : t;
                    return adapter.isNull(res) ? _default : adapter.getNumberAsBigDecimal(res);
                }).toList();
        var identity = BigDecimal.valueOf(0, FunctionHelpers.MAX_SCALE);
        var avg = values.stream().reduce(identity, BigDecimal::add)
                .divide(BigDecimal.valueOf(size.get()), FunctionHelpers.MAX_SCALE_ROUNDING);
        var sumOfSquares = values.stream()
                .map(val -> val.subtract(avg).pow(2))
                .reduce(identity, BigDecimal::add);
        var variance = sumOfSquares.divide(BigDecimal.valueOf(size.get() - (population ? 0 : 1)), FunctionHelpers.MAX_SCALE_ROUNDING);
        var result = variance.sqrt(FunctionHelpers.DEFAULT_MATH_CONTEXT);
        // cap scale at max
        if (result.scale() > FunctionHelpers.MAX_SCALE) {
            result = result.setScale(FunctionHelpers.MAX_SCALE, FunctionHelpers.MAX_SCALE_ROUNDING);
        }
        return result;
    }
}
