package ai.bizone.jsontransform.adapters.gson;

import ai.bizone.jsontransform.JsonTransformerConfiguration;
import ai.bizone.jsontransform.adapters.JsonAdapterHelpers;
import ai.bizone.jsontransform.adapters.gson.adapters.ISODateAdapter;
import ai.bizone.jsontransform.adapters.gson.adapters.InstantTypeAdapter;
import ai.bizone.jsontransform.adapters.gson.adapters.LocalDateTypeAdapter;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;
import java.util.function.Supplier;

public class GsonHelpers {

    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static class BigDecimalToNumberPolicy implements ToNumberStrategy {
        @Override
        public Number readNumber(JsonReader in) throws IOException {
            String value = in.nextString();
            try {
                return JsonAdapterHelpers.unwrapNumber(new BigDecimal(value), true);
            } catch (NumberFormatException e) {
                throw new JsonParseException("Cannot parse " + value + "; at path " + in.getPreviousPath(), e);
            }
        }
    }
    private static BigDecimalToNumberPolicy bigDecimalToNumberPolicy = new BigDecimalToNumberPolicy();

    public static GsonBuilder gsonBuilder() {
        return new GsonBuilder()
                .setDateFormat(ISO_DATETIME_FORMAT)
                .registerTypeAdapter(Date.class, new ISODateAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .setNumberToNumberStrategy(bigDecimalToNumberPolicy)
                .setObjectToNumberStrategy(bigDecimalToNumberPolicy);
    }

    private static final Supplier<Gson> DEFAULT_GSON_SUPPLIER = () -> gsonBuilder().create();

    private static ThreadLocal<Gson> threadSafeFactory = ThreadLocal.withInitial(DEFAULT_GSON_SUPPLIER);

    static Gson GSON() {
        return threadSafeFactory.get();
    }

    static com.jayway.jsonpath.Configuration setFactoryAndReturnJsonPathConfig(Supplier<Gson> gsonSupplier) {
        if (gsonSupplier != null) {
            threadSafeFactory = ThreadLocal.withInitial(gsonSupplier);
        }
        return new Configuration.ConfigurationBuilder()
                .jsonProvider(new GsonJsonProvider(threadSafeFactory.get()))
                .mappingProvider(new GsonMappingProvider(() -> threadSafeFactory.get()))
                .options(Set.of(
                        Option.SUPPRESS_EXCEPTIONS
                ))
                .build();
    }

    /**
     * Please use <code>JsonTransformerConfiguration.set(new GsonJsonTransformerConfiguration(supplier))</code> instead
     */
    @Deprecated(forRemoval = true)
    public static void setGson(Supplier<Gson> supplier) {
        JsonTransformerConfiguration.set(new GsonJsonTransformerConfiguration(supplier));
    }
}
