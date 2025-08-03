package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.FunctionContext;
import ai.bizone.jsontransform.functions.common.TransformerFunction;
import ai.bizone.jsontransform.formats.formurlencoded.FormUrlEncodedFormat;

public class TransformerFunctionForm extends TransformerFunction {

    public TransformerFunctionForm() {
        super();
    }

    @Override
    public Object apply(FunctionContext context) {
        // TODO: how to create the format once?
        return new FormUrlEncodedFormat(context.getAdapter()).serialize(context.getUnwrapped(null));
    }
}
