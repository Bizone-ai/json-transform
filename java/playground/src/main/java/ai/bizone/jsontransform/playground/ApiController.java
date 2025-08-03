package ai.bizone.jsontransform.playground;

import ai.bizone.jsontransform.DebuggableTransformerFunctions;
import ai.bizone.jsontransform.adapters.gson.GsonJsonAdapter;
import ai.bizone.jsontransform.adapters.gson.GsonJsonTransformer;
import ai.bizone.jsontransform.adapters.jackson.JacksonJsonTransformer;
import ai.bizone.jsontransform.adapters.jsonorg.JsonOrgJsonTransformer;
import ai.bizone.jsontransform.adapters.jsonsmart.JsonSmartJsonTransformer;
import ai.bizone.jsontransform.adapters.pojo.PojoJsonTransformer;
import com.google.gson.GsonBuilder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class ApiController {

    public ApiController() {
        var builder = new GsonBuilder();
        GsonConfiguration.setupBuilder(builder);
        GsonJsonTransformer.DEFAULT_ADAPTER = new GsonJsonAdapter(builder::create);
    }


    @PostMapping("/v1/transform/gson")
    public TransformTestResponse v1TransformGson(@RequestBody TransformTestRequest request){
        var adapter = new DebuggableTransformerFunctions();
        var transformer = new GsonJsonTransformer(request.definition, adapter);
        var result = transformer.transform(request.input, request.additionalContext, true);
        return new TransformTestResponse(result, request.debug ? adapter.getDebugResults() : null);
    }

    @PostMapping("/v1/transform/jackson")
    public TransformTestResponse v1TransformJackson(@RequestBody TransformTestRequest request){
        var adapter = new DebuggableTransformerFunctions();
        var transformer = new JacksonJsonTransformer(request.definition, adapter);
        var result = transformer.transform(request.input, request.additionalContext, true);
        return new TransformTestResponse(result, request.debug ? adapter.getDebugResults() : null);
    }

    @PostMapping("/v1/transform/pojo")
    public TransformTestResponse v1TransformPojo(@RequestBody TransformTestRequest request){
        var adapter = new DebuggableTransformerFunctions();
        var transformer = new PojoJsonTransformer(request.definition, adapter);
        var result = transformer.transform(request.input, request.additionalContext, true);
        return new TransformTestResponse(result, request.debug ? adapter.getDebugResults() : null);
    }

    @PostMapping("/v1/transform/jsonorg")
    public TransformTestResponse v1TransformJsonOrg(@RequestBody TransformTestRequest request){
        var adapter = new DebuggableTransformerFunctions();
        var transformer = new JsonOrgJsonTransformer(request.definition, adapter);
        var result = transformer.transform(request.input, request.additionalContext, true);
        return new TransformTestResponse(result, request.debug ? adapter.getDebugResults() : null);
    }

    @PostMapping("/v1/transform/jsonsmart")
    public TransformTestResponse v1TransformJsonSmart(@RequestBody TransformTestRequest request){
        var adapter = new DebuggableTransformerFunctions();
        var transformer = new JsonSmartJsonTransformer(request.definition, adapter);
        var result = transformer.transform(request.input, request.additionalContext, true);
        return new TransformTestResponse(result, request.debug ? adapter.getDebugResults() : null);
    }
}