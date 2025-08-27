package ai.bizone.jsontransform.adapters.tapestry;

import ai.bizone.jsontransform.adapters.JsonAdapter;
import ai.bizone.jsontransform.adapters.jsonorg.JsonOrgHelpers;
import ai.bizone.jsontransform.adapters.pojo.PojoJsonTransformer;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.spi.mapper.MappingException;
import org.apache.tapestry5.json.*;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TapestryJsonAdapter extends JsonAdapter<Object, JSONArray, JSONObject> {

    public TapestryJsonAdapter() {
        super(
                TapestryObjectAdapter::new,
                TapestryArrayAdapter::new,
                TapestryHelpers.getJsonPathConfig()
        );
    }

    @Override
    public boolean is(Object value) {
        return value == null || // this should be mitigated on insertion
                JSONObject.NULL.equals(value) ||
                value instanceof JSONCollection ||
                value instanceof JSONString ||
                value instanceof String ||
                value instanceof Number ||
                value instanceof Boolean;
    }

    @Override
    public boolean isJsonString(Object value) {
        return value instanceof String || value instanceof JSONString;
    }

    @Override
    public boolean isJsonNumber(Object value) {
        return value instanceof Number;
    }

    @Override
    public boolean isJsonBoolean(Object value) {
        return value instanceof Boolean;
    }

    @Override
    public boolean isNull(Object value) {
        return value == null || JSONObject.NULL.equals(value);
    }

    @Override
    public Object jsonNull() {
        return JSONObject.NULL;
    }

    @Override
    public Object wrap(Object value) {
        return TapestryHelpers.convert(value, false, false);
    }

    @Override
    public Object unwrap(Object value, boolean reduceBigDecimals) {
        return TapestryHelpers.convert(value, true, reduceBigDecimals);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(Object value, Class<T> targetType) {
        if(value == null){
            return null;
        }
        if (targetType.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        try {
            if (targetType.isAssignableFrom(ArrayList.class) && isJsonArray(value)) {
                return jsonPathConfiguration.mappingProvider().map(value, targetType, jsonPathConfiguration);
            }
            var unwrappedValue = unwrap(value, false);
            return PojoJsonTransformer.getAdapter().deserialize(unwrappedValue, targetType);
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    @Override
    public Object parse(String value) {
        var wrapper = "[" + value + "]";
        return new JSONArray(wrapper).get(0);
    }

    @Override
    public Object clone(Object value) {
        return wrap(value);
    }

    @Override
    public Number getNumber(Object value) {
        if (value instanceof Number n) {
            return n;
        }
        if (value instanceof String s) {
            return new BigDecimal(s);
        }
        if (value instanceof JSONString js) {
            return new BigDecimal(js.toString());
        }
        return null;
    }

    @Override
    public BigDecimal getNumberAsBigDecimal(Object value) {
        if (value instanceof BigDecimal b) {
            return b;
        }
        if (value instanceof Number n) {
            return new BigDecimal(n.toString());
        }
        if (value instanceof String s) {
            return new BigDecimal(s);
        }
        if (value instanceof JSONString js) {
            return new BigDecimal(js.toString());
        }
        return null;
    }

    @Override
    public Boolean getBoolean(Object value) {
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof String s) {
            return Boolean.valueOf(s);
        }
        if (value instanceof JSONString js) {
            return Boolean.valueOf(js.toString());
        }
        return null;
    }

    @Override
    public DocumentContext getDocumentContext(Object payload, Iterable<String> options) {
        if (isNull(payload)) {
            return NullDocumentContext.INSTANCE;
        }
        return super.getDocumentContext(payload, options);
    }


    @Override
    public boolean nodesComparable() {
        return false;
    }

    @Override
    public boolean areEqual(Object value, Object other) {
        if (value instanceof JSONObject jo) {
            return other instanceof JSONObject o && jo.toMap().equals(o.toMap());
        }
        if (value instanceof JSONArray ja) {
            if (!(other instanceof JSONArray o) || o.size() != ja.size()) {
                return false;
            }
            for (var i = 0 ; i < ja.size() ; i++) {
                if (!areEqual(ja.get(i), o.get(i))) {
                    return false;
                }
            }
            return true;
        }
        if (value instanceof JSONString vjs) {
            return other instanceof JSONString ojs && vjs.toString().equals(ojs.toString());
        }
        return super.areEqual(value, other);
    }

    @Override
    public int hashCode(Object value) {
        return TapestryHelpers.getContentsHashCode(value);
    }


    @Override
    public String toString(Object value) {
        if (value == null || JSONObject.NULL.equals(value)) {
            return JSONObject.NULL.toString();
        }
        if (value instanceof JSONCollection c) {
            return c.toCompactString();
        }
        if (value instanceof JSONString js) {
            return js.toJSONString();
        }
        if (value instanceof String s) {
            var container = new JSONArray();
            container.add(s);
            var arrJson = container.toCompactString();
            return arrJson.substring(1, arrJson.length() - 1); // remove starting and ending [, ]
        }
        if (value instanceof Boolean || value instanceof Number) {
            return getAsString(value);
        }
        var wrapped = wrap(value);
        return toString(wrapped);
    }
}
