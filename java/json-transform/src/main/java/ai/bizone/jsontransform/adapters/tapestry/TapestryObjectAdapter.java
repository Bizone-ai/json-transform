package ai.bizone.jsontransform.adapters.tapestry;

import ai.bizone.jsontransform.adapters.JsonObjectAdapter;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

import java.util.Map;
import java.util.Set;

public class TapestryObjectAdapter extends JsonObjectAdapter<Object, JSONArray, JSONObject> {
    
    @Override
    public JSONObject create() {
        return new JSONObject();
    }

    @Override
    public void add(JSONObject object, String key, String value) {
        object.put(key, value == null ? JSONObject.NULL : value);
    }

    @Override
    public void add(JSONObject object, String key, Number value) {
        object.put(key, value == null ? JSONObject.NULL : value);
    }

    @Override
    public void add(JSONObject object, String key, Boolean value) {
        object.put(key, value == null ? JSONObject.NULL : value);
    }

    @Override
    public void add(JSONObject object, String key, Character value) {
        object.put(key, value == null ? JSONObject.NULL : value);
    }

    @Override
    public void add(JSONObject object, String key, Object value) {
        object.put(key, value == null ? JSONObject.NULL : value);
    }

    @Override
    public void add(JSONObject object, String key, JSONArray value) {
        object.put(key, value == null ? JSONObject.NULL : value);
    }

    @Override
    public Object remove(JSONObject object, String key) {
        return object.remove(key);
    }

    @Override
    public boolean has(JSONObject object, String key) {
        return object.containsKey(key);
    }

    @Override
    public Object get(JSONObject object, String key) {
        return object.has(key) ? object.get(key) : null;
    }

    @Override
    public int size(JSONObject object) {
        return object.size();
    }

    @Override
    public boolean is(Object value) {
        return value instanceof JSONObject;
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet(JSONObject object) {
        return object.entrySet();
    }

    @Override
    public Set<String> keySet(JSONObject object) {
        return object.keySet();
    }
}
