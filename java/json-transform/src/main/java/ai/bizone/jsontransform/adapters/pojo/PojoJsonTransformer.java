package ai.bizone.jsontransform.adapters.pojo;

import ai.bizone.jsontransform.JsonTransformer;
import ai.bizone.jsontransform.JsonTransformerConfiguration;
import ai.bizone.jsontransform.TransformerFunctionsAdapter;

public class PojoJsonTransformer extends JsonTransformer {

    public static PojoJsonAdapter DEFAULT_ADAPTER = new PojoJsonAdapter();

    public static PojoJsonAdapter getAdapter() {
        var currentAdapter = JsonTransformerConfiguration.get().getAdapter();
        if (currentAdapter instanceof PojoJsonAdapter pja) {
            return pja;
        }
        return DEFAULT_ADAPTER;
    }

    public PojoJsonTransformer(final Object definition) {
        super(definition, getAdapter());
    }

    public PojoJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, getAdapter(), functionsAdapter);
    }
}
