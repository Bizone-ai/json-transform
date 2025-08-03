package ai.bizone.jsontransform.template;

import ai.bizone.jsontransform.JsonTransformer;
import ai.bizone.jsontransform.ParameterResolver;
import ai.bizone.jsontransform.adapters.JsonAdapter;

public class TextTemplateJsonTransformer extends JsonTransformer {
    public TextTemplateJsonTransformer(JsonAdapter<?, ?, ?> adapter) {
        super(null, adapter);
    }

    public Object transformString(Object definition, ParameterResolver resolver) {
        return fromJsonPrimitive("$", definition, resolver, false);
    }
}
