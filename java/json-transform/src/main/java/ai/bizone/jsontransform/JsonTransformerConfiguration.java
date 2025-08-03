package ai.bizone.jsontransform;

import ai.bizone.jsontransform.adapters.JsonAdapter;
import ai.bizone.jsontransform.adapters.pojo.PojoJsonTransformerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JsonTransformerConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JsonTransformerConfiguration.class);
    static volatile JsonTransformerConfiguration current = null;

    /**
     * Sets the default configuration (based on a specific JSON implementation)
     *
     * @param configuration The JSON transformer configuration implementation
     */
    public synchronized static void set(JsonTransformerConfiguration configuration) {
        if (configuration == null) {
            throw new ExceptionInInitializerError("Cannot set empty configuration");
        }
        log.debug("Setting configuration to {}", configuration);
        current = configuration;
    }

    /**
     * Gets the current default JSON transformer configuration
     */
    public static JsonTransformerConfiguration get() {
        if (current == null) {
            log.debug("Json transformers configuration was not set. Using default Pojo implementation.");
            set(new PojoJsonTransformerConfiguration());
        }
        return current;
    }

    private final JsonAdapter<?, ?, ?> adapter;

    public JsonTransformerConfiguration(JsonAdapter<?, ?, ?> adapter) {
        this.adapter = adapter;
    }

    /**
     * Gets the current JSON implementation adapter
     */
    public JsonAdapter<?, ?, ?> getAdapter() {
        return this.adapter;
    }
}
