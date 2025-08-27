package ai.bizone.jsontransform.adapters;

import ai.bizone.jsontransform.MultiAdapterBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonAdapterTests extends MultiAdapterBaseTest {
    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testAsString_BigDecimal(JsonAdapter<?,?,?> adapter) {
        var x = BigDecimal.valueOf(3);
        assertEquals(adapter, "3", adapter.getAsString(x));
        x = BigDecimal.valueOf(0.5);
        assertEquals(adapter, "0.5", adapter.getAsString(x));
        x = BigDecimal.valueOf(Double.MAX_VALUE);
        assertEquals(adapter, String.format("%.0f",x), adapter.getAsString(x));
        var str = "98765432101234567890000000000000.9876543210123456789";
        x = new BigDecimal(str);
        assertEquals(adapter, str, adapter.getAsString(x));
        str = "1.5E+50";
        x = new BigDecimal(str);
        assertEquals(adapter, x.toPlainString(), adapter.getAsString(x));
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testAsString_Float(JsonAdapter<?,?,?> adapter) {
        float f = 3.0f;
        assertEquals(adapter, "3", adapter.getAsString(f));
        f = 0.5f;
        assertEquals(adapter, "0.5", adapter.getAsString(f));
        f = Float.MAX_VALUE;
        assertEquals(adapter, String.format("%.0f",f), adapter.getAsString(f));
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testAsString_Double(JsonAdapter<?,?,?> adapter) {
        double d = 3.0d;
        assertEquals(adapter, "3", adapter.getAsString(d));
        d = 0.5f;
        assertEquals(adapter, "0.5", adapter.getAsString(d));
        d = Double.MAX_VALUE;
        assertEquals(adapter, String.format("%.0f",d), adapter.getAsString(d));
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMergeIntoGivenMutuallyExclusiveKeysWithDot(JsonAdapter<?,?,?> adapter) {
        var root = adapter.parse("""
{
    "numbers.roman": { "I": 1, "II": 2 }
}
""");
        var mergee = adapter.parse("""
{
    "numbers.exist": true
}
""");
        var expected = adapter.parse("""
{
    "numbers.roman": { "I": 1, "II": 2 },
    "numbers.exist": true
}
""");
        assertEquals(adapter, expected, adapter.mergeInto(root, mergee, null));
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMergeIntoGivenNoPath(JsonAdapter<?,?,?> adapter) {
        var root = adapter.parse("""
{
    "a.b.c[0]": "foovalue"
}
""");
        var mergee = adapter.parse("""
{
    "a": { "z": "barvalue" }
}
""");
        var expected = adapter.parse("""
{
    "a": { "z": "barvalue" }, "a.b.c[0]": "foovalue"
}
""");
        assertEquals(adapter, expected, adapter.mergeInto(root, mergee, null));
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMergeIntoGivenMutuallyExclusiveKeysAndDollarPath(JsonAdapter<?,?,?> adapter) {
        var root = adapter.parse("""
{
    "roman": { "I": 1, "II": 2 }
}
""");
        var mergee = adapter.parse("""
{
    "arithmetics": { "exist": true },
    "symbols": ["I", "V", "X", "L", "C", "D", "M"]
}
""");
        var expected = adapter.parse("""
{
    "roman": { "I": 1, "II": 2 },
    "arithmetics": { "exist": true },
    "symbols": ["I", "V", "X", "L", "C", "D", "M"]
}
""");
        assertEquals(adapter, expected, adapter.mergeInto(root, mergee, "$"));
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testArraySetOnOutOfBoundsIndex(JsonAdapter<?,?,?> adapter) {
        var array = adapter.createArray(4);
        var el = adapter.wrap("string");
        adapter.set(array, 3, el);
        assertEquals(adapter, el, adapter.get(array, 3));
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMergeAllNew(JsonAdapter<?,?,?> adapter) {
        assertEquals(adapter, adapter.parse("""
{
  "a": "A",
  "b": "B"
}
"""), adapter.merge(adapter.parse("""
{
  "a": "A"
}
"""), adapter.parse("""
{
  "b": "B"
}
""")));
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMergeOverrideExisting(JsonAdapter<?,?,?> adapter) {
        assertEquals(adapter, adapter.parse("""
{
    "a": "A",
    "b": "BB"
}
"""), adapter.merge(adapter.parse("""
{
  "a": "A",
  "b": "B"
}
"""), adapter.parse("""
{
  "b": "BB"
}
""")));
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMergeOverrideWithNull(JsonAdapter<?,?,?> adapter) {
        assertEquals(adapter, adapter.parse("""
{
  "a": "A",
  "b": null
}
"""), adapter.merge(adapter.parse("""
{
  "a": "A",
  "b": "B"
}
"""), adapter.parse("""
{
  "b": null
}
""")));
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMergeShallow(JsonAdapter<?,?,?> adapter) {
        assertEquals(adapter, adapter.parse("""
{
  "a": {
    "aaa": "AAA"
  },
  "b": "B"
}
"""), adapter.merge(adapter.parse("""
{
  "a": {
    "aa": "AA"
  },
  "b": "B"
}
"""), adapter.parse("""
{
  "a": {
    "aaa": "AAA"
  }
}
""")));
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMergeDeep(JsonAdapter<?,?,?> adapter) {
        assertEquals(adapter, adapter.parse("""
{
  "a": {
    "aa": "AA",
    "aaa": "AAA"
  },
  "b": "B"
}
"""), adapter.merge(adapter.parse("""
{
  "a": {
    "aa": "AA"
  },
  "b": "B"
}
"""), adapter.parse("""
{
  "a": {
    "aaa": "AAA"
  }
}
"""), new JsonAdapter.JsonMergeOptions(true, false)));
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMergeDeepAndConcatArrays(JsonAdapter<?,?,?> adapter) {
        assertEquals(adapter, adapter.parse("""
{
  "a": {
    "aa": "AA",
    "aaa": "AAA"
  },
  "c": [1, 2, 3, 4]
}
"""), adapter.merge(adapter.parse("""
{
  "a": {
    "aa": "AA"
  },
  "c": [1, 2]
}
"""), adapter.parse("""
{
  "a": {
    "aaa": "AAA"
  },
  "c": [3, 4]
}
"""), new JsonAdapter.JsonMergeOptions(true, true)));
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMergeConcatArrays(JsonAdapter<?,?,?> adapter) {
        assertEquals(adapter, adapter.parse("""
{
  "a": {
    "aaa": "AAA"
  },
  "c": [1, 2, 3, 4]
}
"""), adapter.merge(adapter.parse("""
{
  "a": {
    "aa": "AA"
  },
  "c": [1, 2]
}
"""), adapter.parse("""
{
  "a": {
    "aaa": "AAA"
  },
  "c": [3, 4]
}
"""), new JsonAdapter.JsonMergeOptions(false, true)));
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testObjectComparability(JsonAdapter<?,?,?> adapter) {
        var sampleJson = """
{
  "a": {
    "aaa": "AAA"
  },
  "c": [1, 2, 3, 4]
}
""";
        var instance1 = adapter.parse(sampleJson);
        var instance2 = adapter.parse(sampleJson);
        Assertions.assertTrue(adapter.areEqual(instance1, instance2));
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testArrayComparability(JsonAdapter<?,?,?> adapter) {
        var sampleJson = "[\"a\",1,true]";
        var instance1 = adapter.parse(sampleJson);
        var instance2 = adapter.parse(sampleJson);
        Assertions.assertTrue(adapter.areEqual(instance1, instance2));
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testNonExistingKeyInObject(JsonAdapter<?,?,?> adapter) {
        // should not fail and return `null`
        var instance = adapter.parse("{}");
        Assertions.assertNull(adapter.get(instance, ""));
    }

    public static class TestDeserialize {
        public boolean bool;
        public int num;
        public String str;
    }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testDeserialize(JsonAdapter<?,?,?> adapter) {
        var adapterPayload = adapter.parse("""
        {
            "bool": true,
            "num": 1,
            "str": "foo"
        }""");
        var deserialized = adapter.deserialize(adapterPayload, TestDeserialize.class);
        Assertions.assertTrue(deserialized.bool);
        Assertions.assertEquals(1, deserialized.num);
        Assertions.assertEquals("foo", deserialized.str);
    }

    public class ListOfTestDeserialize extends ArrayList<Object> { }

    @ParameterizedTest()
    @MethodSource("ai.bizone.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testDeserializeList(JsonAdapter<?,?,?> adapter) {
        var adapterPayload = adapter.parse("""
        [{
            "bool": true,
            "num": 1,
            "str": "foo"
        }]""");
        var deserialized = adapter.deserialize(adapterPayload, List.class);
        Assertions.assertEquals(1, deserialized.size());
        var expected = Map.of("bool", true, "num", 1, "str", "foo");
        Assertions.assertEquals(expected, deserialized.get(0));
    }
}
