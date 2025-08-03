package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import co.nlighten.jsontransform.functions.common.*;

import java.util.ArrayList;
import java.util.Map;

public class TransformerFunctionMax extends TransformerFunction {

    public TransformerFunctionMax() {
        super(FunctionDescription.of(
                Map.of(
                        "default", ArgumentType.of(ArgType.Object).position(0),
                        "by", ArgumentType.of(ArgType.Any).position(2),
                        "type", ArgumentType.of(ArgType.String).position(1).defaultValue("AUTO")
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null || streamer.knownAsEmpty())
            return null;
        var hasBy = context.has("by");
        var type = context.getEnum("type");
        var def = context.getJsonElement("default",true);

        var adapter = context.getAdapter();
        if (!hasBy) {
            var comparator = FunctionHelpers.createComparator(adapter, type);
            var result = streamer.stream()
                    .map(t -> adapter.isNull(t) ? def : t)
                    .max(comparator);
            return result.isPresent() ? adapter.getAs(type, result.get()) : adapter.jsonNull();
        } else {
            var by = context.getJsonElement("by", false);
            var comparator = CompareBy.createByComparator(adapter, 0, type);
            var result = streamer.stream()
                    .map(item -> {
                        var cb = new CompareBy(item);
                        var t = context.transformItem(by, item);
                        cb.by = new ArrayList<>();
                        cb.by.add(adapter.isNull(t) ? def : t);
                        return cb;
                    })
                    .max(comparator);
            return result.isPresent() ? adapter.getAs(type, result.get().value) : adapter.jsonNull();
        }
    }
}
