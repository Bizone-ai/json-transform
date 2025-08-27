package ai.bizone.jsontransform.adapters.tapestry;

import ai.bizone.jsontransform.adapters.JsonAdapterHelpers;
import ai.bizone.jsontransform.adapters.pojo.PojoHelpers;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.TapestryJsonProvider;
import com.jayway.jsonpath.spi.mapper.TapestryMappingProvider;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.json.JSONString;

import java.lang.reflect.Array;
import java.util.*;

public class TapestryHelpers {

    static Configuration getJsonPathConfig() {
        return new Configuration.ConfigurationBuilder()
                .jsonProvider(new TapestryJsonProvider())
                .mappingProvider(new TapestryMappingProvider())
                .options(Set.of(
                        Option.SUPPRESS_EXCEPTIONS
                ))
                .build();
    }

    /**
     * Wraps and unwraps values for JsonSmartJsonAdapter processing
     * @param object object to convert
     * @param unwrap if true, will convert JSONObject.NULL to null, otherwise will convert null values to JSONObject.NULL
     */
    public static Object convert(Object object, boolean unwrap, boolean reduceBigDecimals) {
        if (object == null || JSONObject.NULL.equals(object)) {
            return unwrap ? null : JSONObject.NULL;
        }
        // number
        if (object instanceof Number n) {
            return JsonAdapterHelpers.unwrapNumber(n, reduceBigDecimals);
        }
        // boolean | string
        if (object instanceof Boolean ||
                object instanceof String) {
            return object;
        }
        // special case: char
        if (object instanceof Character) {
            return object.toString();
        }
        // array
        if (object instanceof Iterable<?> i) {
            var result = unwrap ? new ArrayList<>() : new JSONArray();
            for (var item : i) {
                result.add(convert(item, unwrap, reduceBigDecimals));
            }
            return result;
        } else if (object.getClass().isArray()) {
            var result = unwrap ? new ArrayList<>() : new JSONArray();
            var length = Array.getLength(object);
            for (var i = 0; i < length; i++) {
                result.add(convert(Array.get(object, i), unwrap, reduceBigDecimals));
            }
            return result;
        }
        // object
        var result = unwrap ? new HashMap<String, Object>() : new JSONObject();
        if (object instanceof Map<?, ?> m) {
            // - map
            for (var entry : m.entrySet()) {
                result.put(entry.getKey().toString(), convert(entry.getValue(), unwrap, reduceBigDecimals));
            }
        } else {
            // - class type
            PojoHelpers.getAllFields(object.getClass()).forEach(field -> {
                try {
                    field.setAccessible(true);
                    result.put(field.getName(), convert(field.get(object), unwrap, reduceBigDecimals));
                } catch (IllegalAccessException e) {
                    // e.printStackTrace();
                }
            });
        }
        return result;
    }

    public static int getContentsHashCode(Object object) {
        if (object == JSONObject.NULL || object == null)
            return 0;
        if (object instanceof JSONObject jo) {
            return jo.toCompactString().hashCode();
        }
        if (object instanceof JSONArray ja) {
            return ja.toCompactString().hashCode();
        }
        if (object instanceof JSONString js) {
            return js.toString().hashCode();
        }
        return object.hashCode();
    }

}
