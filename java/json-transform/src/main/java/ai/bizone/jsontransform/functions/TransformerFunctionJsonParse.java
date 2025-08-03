package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.FunctionContext;
import ai.bizone.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionJsonParse extends TransformerFunction {
    public TransformerFunctionJsonParse() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        return context.getAdapter().parse(str);
    }
}
