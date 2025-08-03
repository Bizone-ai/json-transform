package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.ParameterResolver;
import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.template.ParameterDefaultResolveOptions;
import ai.bizone.jsontransform.template.TextTemplate;

import java.util.Map;

import static ai.bizone.jsontransform.functions.common.FunctionContext.pathOfVar;

public class TransformerFunctionTemplate extends TransformerFunction {
    private static final Object DOLLAR = "$";

    public TransformerFunctionTemplate() {
        super(FunctionDescription.of(
                Map.of(
                    "payload", ArgumentType.of(ArgType.Any).position(0),
                    "default_resolve", ArgumentType.of(ArgType.String).position(1).defaultValue(ParameterDefaultResolveOptions.UNIQUE.name()),
                    "url_encode", ArgumentType.of(ArgType.Boolean).position(2).defaultValue(false)
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var input = context.getString(null);
        if (input == null) {
            return null;
        }
        var adapter = context.getAdapter();

        var defaultResolveValue = context.getEnum("default_resolve");
        var defaultResolver = ParameterDefaultResolveOptions.valueOf(defaultResolveValue);

        var urlEncode = context.getBoolean("url_encode");

        var currentResolver = context.getResolver();
        ParameterResolver resolver = currentResolver;
        var payload = context.getJsonElement("payload");
        if (!adapter.isNull(payload)) {
            var dc = adapter.getDocumentContext(payload);
            resolver = name -> {
                if (pathOfVar("##current", name)) {
                    return dc.read(DOLLAR + name.substring(9));
                }
                return currentResolver.get(name);
            };
        }

        var tt = TextTemplate.get(input, defaultResolver);
        return tt.render(resolver, adapter, urlEncode);
    }
}
