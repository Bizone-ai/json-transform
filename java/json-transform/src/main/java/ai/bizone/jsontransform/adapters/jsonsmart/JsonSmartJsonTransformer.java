package ai.bizone.jsontransform.adapters.jsonsmart;

import ai.bizone.jsontransform.JsonTransformer;
import ai.bizone.jsontransform.JsonTransformerConfiguration;
import ai.bizone.jsontransform.TransformerFunctionsAdapter;

public class JsonSmartJsonTransformer extends JsonTransformer {

    public static JsonSmartJsonAdapter DEFAULT_ADAPTER = new JsonSmartJsonAdapter();

    public static JsonSmartJsonAdapter getAdapter() {
        var currentAdapter = JsonTransformerConfiguration.get().getAdapter();
        if (currentAdapter instanceof JsonSmartJsonAdapter joa) {
            return joa;
        }
        return DEFAULT_ADAPTER;
    }

    public JsonSmartJsonTransformer(final Object definition) {
        super(definition, getAdapter());
    }

    public JsonSmartJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, getAdapter(), functionsAdapter);
    }
}