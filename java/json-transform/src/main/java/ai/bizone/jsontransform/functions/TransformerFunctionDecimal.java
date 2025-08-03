package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import co.nlighten.jsontransform.functions.common.*;

import java.math.RoundingMode;
import java.util.Map;

public class TransformerFunctionDecimal extends TransformerFunction {
    public TransformerFunctionDecimal() {
        super(FunctionDescription.of(
            Map.of(
            "scale", ArgumentType.of(ArgType.Number).position(0).defaultValue(-1),
            "rounding", ArgumentType.of(ArgType.String).position(1).defaultValue("HALF_UP")
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var result = context.getBigDecimal(null);
        if (result == null) return null;
        var scale = context.getInteger("scale");
        var roundingMode = context.getEnum("rounding");
        if (scale == FunctionHelpers.NO_SCALE && result.scale() > FunctionHelpers.MAX_SCALE) {
            scale = FunctionHelpers.MAX_SCALE;
        }
        if (scale > -1) {
            result = result.setScale(scale, RoundingMode.valueOf(roundingMode));
        }
        return result;
    }
}
