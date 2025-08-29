package ai.bizone.jsontransform.adapters.gson;

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
    private static final BigDecimalToNumberPolicy bigDecimalToNumberPolicy = new BigDecimalToNumberPolicy();

    public static GsonBuilder gsonBuilder() {
        return new GsonBuilder()
                .setDateFormat(ISO_DATETIME_FORMAT)
                .registerTypeAdapter(Date.class, new ISODateAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .setNumberToNumberStrategy(bigDecimalToNumberPolicy)
                .setObjectToNumberStrategy(bigDecimalToNumberPolicy);
    }

    private static Gson gson = gsonBuilder().create();

    static Gson getGSON() {
        return gson;
    }

    static com.jayway.jsonpath.Configuration setFactoryAndReturnJsonPathConfig(Supplier<Gson> gsonSupplier) {
        if (gsonSupplier != null) {
            gson = gsonSupplier.get();
        }
        return new Configuration.ConfigurationBuilder()
                .jsonProvider(new GsonJsonProvider(gson))
                .mappingProvider(new GsonMappingProvider(() -> gson))
                .options(Set.of(
                        Option.SUPPRESS_EXCEPTIONS
                ))
                .build();
    }
}
