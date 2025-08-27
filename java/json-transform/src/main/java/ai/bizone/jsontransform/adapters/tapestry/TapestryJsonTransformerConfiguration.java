package ai.bizone.jsontransform.adapters.tapestry;

import ai.bizone.jsontransform.JsonTransformerConfiguration;

public class TapestryJsonTransformerConfiguration extends JsonTransformerConfiguration {
    public TapestryJsonTransformerConfiguration() {
        super(new TapestryJsonAdapter());
    }
}
