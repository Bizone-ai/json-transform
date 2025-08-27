package ai.bizone.jsontransform.adapters.tapestry;

import ai.bizone.jsontransform.JsonTransformer;
import ai.bizone.jsontransform.JsonTransformerConfiguration;
import ai.bizone.jsontransform.TransformerFunctionsAdapter;

public class TapestryJsonTransformer extends JsonTransformer {

    public static TapestryJsonAdapter DEFAULT_ADAPTER = new TapestryJsonAdapter();

    public static TapestryJsonAdapter getAdapter() {
        var currentAdapter = JsonTransformerConfiguration.get().getAdapter();
        if (currentAdapter instanceof TapestryJsonAdapter a) {
            return a;
        }
        return DEFAULT_ADAPTER;
    }

    public TapestryJsonTransformer(final Object definition) {
        super(definition, getAdapter());
    }

    public TapestryJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, getAdapter(), functionsAdapter);
    }
}