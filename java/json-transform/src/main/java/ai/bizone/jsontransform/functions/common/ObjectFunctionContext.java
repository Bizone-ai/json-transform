package ai.bizone.jsontransform.functions.common;

import ai.bizone.jsontransform.JsonTransformerUtils;
import ai.bizone.jsontransform.JsonTransformerFunction;
import ai.bizone.jsontransform.ParameterResolver;
import ai.bizone.jsontransform.adapters.JsonAdapter;

public class ObjectFunctionContext extends FunctionContext {
    private final Object definition;

    public ObjectFunctionContext(String path,
                                 Object definition,
                                 JsonAdapter<?, ?, ?> adapter,
                                 String functionKey,
                                 TransformerFunction function,
                                 ParameterResolver resolver, JsonTransformerFunction extractor) {
        super(path, adapter, functionKey, function, resolver, extractor, definition);
        this.definition = definition;
    }

    @Override
    public boolean has(String name) {
        return adapter.has(definition, name);
    }

    @Override
    public Object get(String name, boolean transform) {
        var el = adapter.get(definition, name == null ? alias : name);
        if (adapter.isNull(el)) {
            return function.getDefaultValue(name);
        }
        return transform ? extractor.transform(getPathFor(name), el, resolver, true) : el;
    }

    @Override
    public String getPathFor(String name) {
        return path + JsonTransformerUtils.toObjectFieldPath(adapter, name == null ? getAlias() : name);
    }
}
