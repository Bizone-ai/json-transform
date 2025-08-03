package transformers;

import ai.bizone.jsontransform.adapters.gson.GsonJsonAdapter;
import ai.bizone.jsontransform.adapters.jackson.JacksonJsonAdapter;
import ai.bizone.jsontransform.adapters.jsonorg.JsonOrgJsonAdapter;
import ai.bizone.jsontransform.adapters.jsonsmart.JsonSmartJsonAdapter;
import ai.bizone.jsontransform.adapters.pojo.PojoJsonAdapter;
import ai.bizone.jsontransform.JsonTransformerTest;
import ai.bizone.jsontransform.adapters.JsonAdapter;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 50, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
@State(value = Scope.Benchmark)
public class TransformersFunctionsBenchmarksTests {

    @Param({"gson", "jackson", "pojo", "jsonOrg", "jsonSmart"})
    public String adapterType;

    public JsonAdapter<?, ?, ?> getAdapterByType(String adapterType) {
        switch (adapterType) {
            case "gson":
                return new GsonJsonAdapter();
            case "jackson":
                return new JacksonJsonAdapter();
            case "pojo":
                return new PojoJsonAdapter();
            case "jsonOrg":
                return new JsonOrgJsonAdapter();
            case "jsonSmart":
                return new JsonSmartJsonAdapter();
            default:
                throw new IllegalArgumentException("Invalid adapter type: " + adapterType);
        }
    }

    @Benchmark
    @Measurement(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    public void measureInputExtractorSpreadArray2() {
        var test = new JsonTransformerTest();
        test.testInputExtractorSpreadArray2(getAdapterByType(adapterType));
    }
}
