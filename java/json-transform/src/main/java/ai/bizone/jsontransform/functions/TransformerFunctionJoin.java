package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TransformerFunctionJoin extends TransformerFunction {
    public TransformerFunctionJoin() {
        super(FunctionDescription.of(
            Map.of(
            "delimiter", ArgumentType.of(ArgType.String).position(0).defaultValue("").aliases(List.of("$$delimiter")),
            "prefix", ArgumentType.of(ArgType.String).position(1).defaultValue(""),
            "suffix", ArgumentType.of(ArgType.String).position(2).defaultValue(""),
            "keep_nulls", ArgumentType.of(ArgType.Boolean).position(3).defaultValue(false)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var arr = context.getJsonElementStreamer(null);
        var delimiter = context.getString("$$delimiter"); // backwards compat.
        if (delimiter == null) {
            delimiter = context.getString("delimiter");
        }
        var prefix = Optional.ofNullable(context.getString("prefix")).orElse("");
        var suffix = Optional.ofNullable(context.getString("suffix")).orElse("");
        var stream = arr.stream().map(context::getAsString);
        if (!context.getBoolean("keep_nulls")) {
            stream = stream.filter(Objects::nonNull);
        }
        return stream.collect(Collectors.joining(delimiter, prefix, suffix));
    }
}
