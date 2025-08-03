package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.FunctionContext;
import ai.bizone.jsontransform.functions.common.TransformerFunction;
import ai.bizone.jsontransform.formats.yaml.YamlFormat;

public class TransformerFunctionYaml extends TransformerFunction {

    public TransformerFunctionYaml() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        return new YamlFormat(context.getAdapter()).serialize(context.getUnwrapped(null, true));
    }
}
