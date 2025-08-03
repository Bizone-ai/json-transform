package ai.bizone.jsontransform.adapters.gson;

import ai.bizone.jsontransform.JsonTransformer;
import ai.bizone.jsontransform.JsonTransformerConfiguration;
import ai.bizone.jsontransform.TransformerFunctionsAdapter;

public class GsonJsonTransformer extends JsonTransformer {

    public static GsonJsonAdapter DEFAULT_ADAPTER = new GsonJsonAdapter();

    public static GsonJsonAdapter getAdapter() {
        var currentAdapter = JsonTransformerConfiguration.get().getAdapter();
        if (currentAdapter instanceof GsonJsonAdapter gja) {
            return gja;
        }
        return DEFAULT_ADAPTER;
    }

    public GsonJsonTransformer(final Object definition) {
        super(definition, getAdapter());
    }

    public GsonJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, getAdapter(), functionsAdapter);
    }
}
