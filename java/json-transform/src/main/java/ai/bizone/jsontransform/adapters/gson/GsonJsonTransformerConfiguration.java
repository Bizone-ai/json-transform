package ai.bizone.jsontransform.adapters.gson;

import ai.bizone.jsontransform.JsonTransformerConfiguration;
import com.google.gson.Gson;

import java.util.function.Supplier;

public class GsonJsonTransformerConfiguration extends JsonTransformerConfiguration {
    public GsonJsonTransformerConfiguration(Supplier<Gson> gsonSupplier) {
        super(new GsonJsonAdapter(gsonSupplier));
    }
    public GsonJsonTransformerConfiguration() {
        this(null);
    }
}
