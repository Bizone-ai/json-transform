package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public class TransformerFunctionSum extends TransformerFunction {

    public TransformerFunctionSum() {
        super(FunctionDescription.of(
            Map.of(
            "default", ArgumentType.of(ArgType.Number).position(0).defaultValue(BigDecimal.ZERO),
            "by", ArgumentType.of(ArgType.Any).position(1)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null || streamer.knownAsEmpty())
            return null;
        var hasBy = context.has("by");
        var by = context.getJsonElement("by", false);
        var def = Objects.requireNonNullElse(context.getBigDecimal("default"), BigDecimal.ZERO);
        var adapter = context.getAdapter();
        var result = streamer.stream()
                .map(t -> {
                    var res = hasBy ? context.transformItem(by, t) : t;
                    return adapter.isNull(res) ? def : adapter.getNumberAsBigDecimal(res);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // cap scale at max
        if (result.scale() > FunctionHelpers.MAX_SCALE) {
            result = result.setScale(FunctionHelpers.MAX_SCALE, FunctionHelpers.MAX_SCALE_ROUNDING);
        }
        return result;
    }
}
