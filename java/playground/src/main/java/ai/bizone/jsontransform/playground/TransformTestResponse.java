package ai.bizone.jsontransform.playground;

import ai.bizone.jsontransform.DebuggableTransformerFunctions;

import java.util.Map;

public class TransformTestResponse {
    Object result;
    Map<String, DebuggableTransformerFunctions.TransformerDebugInfo> debugInfo;
    public TransformTestResponse(Object result, Map<String, DebuggableTransformerFunctions.TransformerDebugInfo> debugInfo) {
        this.result = result;
        this.debugInfo = debugInfo;
    }
}
