package ai.bizone.jsontransform.adapters.jsonsmart;

import ai.bizone.jsontransform.JsonTransformerConfiguration;

public class JsonSmartJsonTransformerConfiguration extends JsonTransformerConfiguration {
    public JsonSmartJsonTransformerConfiguration() {
        super(new JsonSmartJsonAdapter());
    }
}
