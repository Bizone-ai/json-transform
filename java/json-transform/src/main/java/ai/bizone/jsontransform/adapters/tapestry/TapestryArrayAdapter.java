package ai.bizone.jsontransform.adapters.tapestry;

import ai.bizone.jsontransform.adapters.JsonAdapterHelpers;
import ai.bizone.jsontransform.adapters.JsonArrayAdapter;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

import java.util.stream.Stream;

public class TapestryArrayAdapter extends JsonArrayAdapter<Object, JSONArray, JSONObject> {
    @Override
    public JSONArray create() {
        return new JSONArray();
    }

    @Override
    public JSONArray create(int capacity) {
        // no capacity at this version
        return new JSONArray();
    }

    @Override
    public void add(JSONArray array, String value) {
        array.add(value == null ? JSONObject.NULL : value);
    }

    @Override
    public void add(JSONArray array, Number value) {
        array.add(value == null ? JSONObject.NULL : value);
    }

    @Override
    public void add(JSONArray array, Boolean value) {
        array.add(value == null ? JSONObject.NULL : value);
    }

    @Override
    public void add(JSONArray array, Character value) {
        array.add(value == null ? JSONObject.NULL : value.toString());
    }

    @Override
    public void add(JSONArray array, Object value) {
        array.add(value == null ? JSONObject.NULL : value);
    }

    @Override
    public void add(JSONArray array, JSONArray value) {
        array.add(value == null ? JSONObject.NULL : value);
    }

    @Override
    public void set(JSONArray array, int index, Object value) {
        if (array.size() > index || JsonAdapterHelpers.trySetArrayAtOOB(this, array, index, value == null ? JSONObject.NULL : value, null)) {
            array.put(index, value == null ? JSONObject.NULL : value);
        }
    }

    @Override
    public Object remove(JSONArray array, int index) {
        return array.remove(index);
    }

    @Override
    public Object get(JSONArray array, int index) {
        return array.get(index);
    }

    @Override
    public int size(JSONArray array) {
        return array.size();
    }

    @Override
    public boolean is(Object value) {
        return value instanceof JSONArray;
    }

    @Override
    public Stream<Object> stream(JSONArray array, boolean parallel) {
        return array.stream();
    }
}
