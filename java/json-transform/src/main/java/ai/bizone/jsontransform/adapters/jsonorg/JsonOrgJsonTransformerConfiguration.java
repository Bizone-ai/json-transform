package ai.bizone.jsontransform.adapters.jsonorg;

import ai.bizone.jsontransform.JsonTransformerConfiguration;

public class JsonOrgJsonTransformerConfiguration extends JsonTransformerConfiguration {
    public JsonOrgJsonTransformerConfiguration() {
        super(new JsonOrgJsonAdapter());
    }
}
