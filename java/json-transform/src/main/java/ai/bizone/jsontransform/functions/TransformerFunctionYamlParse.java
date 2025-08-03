package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.FunctionContext;
import ai.bizone.jsontransform.functions.common.TransformerFunction;
import ai.bizone.jsontransform.formats.yaml.YamlFormat;

public class TransformerFunctionYamlParse extends TransformerFunction {

    public TransformerFunctionYamlParse() {
        super();
    }

    @Override
    public Object apply(FunctionContext context) {
        return new YamlFormat(context.getAdapter()).deserialize(context.getString(null));
    }
}
