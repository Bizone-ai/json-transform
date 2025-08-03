package ai.bizone.jsontransform.adapters.jsonorg;

import ai.bizone.jsontransform.JsonTransformer;
import ai.bizone.jsontransform.JsonTransformerConfiguration;
import ai.bizone.jsontransform.TransformerFunctionsAdapter;

public class JsonOrgJsonTransformer extends JsonTransformer {

    public static JsonOrgJsonAdapter DEFAULT_ADAPTER = new JsonOrgJsonAdapter();

    public static JsonOrgJsonAdapter getAdapter() {
        var currentAdapter = JsonTransformerConfiguration.get().getAdapter();
        if (currentAdapter instanceof JsonOrgJsonAdapter joa) {
            return joa;
        }
        return DEFAULT_ADAPTER;
    }

    public JsonOrgJsonTransformer(final Object definition) {
        super(definition, getAdapter());
    }

    public JsonOrgJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, getAdapter(), functionsAdapter);
    }
}