package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import co.nlighten.jsontransform.functions.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TransformerFunctionSwitch extends TransformerFunction {
    static final Logger logger = LoggerFactory.getLogger(TransformerFunctionSwitch.class);

    public TransformerFunctionSwitch() {
        super(FunctionDescription.of(
            Map.of(
            "cases", ArgumentType.of(ArgType.Object).position(0),
            "default", ArgumentType.of(ArgType.Any).position(1)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var alias = context.getAlias();
        var value = context.getString(null);
        var adapter = context.getAdapter();
        var caseMap = context.getJsonElement("cases");
        if (!adapter.isJsonObject(caseMap)) {
            logger.warn("{}.cases was not specified with an object as case map", alias);
            return null;
        }
        return adapter.has(caseMap, value)
                ? adapter.get(caseMap, value)
                : context.getJsonElement("default");
    }
}
