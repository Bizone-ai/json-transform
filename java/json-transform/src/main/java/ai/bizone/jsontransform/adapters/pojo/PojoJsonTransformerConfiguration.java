package ai.bizone.jsontransform.adapters.pojo;

import ai.bizone.jsontransform.JsonTransformerConfiguration;

public class PojoJsonTransformerConfiguration extends JsonTransformerConfiguration {
    public PojoJsonTransformerConfiguration() {
        super(new PojoJsonAdapter());
    }
}
