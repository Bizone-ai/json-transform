package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.FunctionContext;
import ai.bizone.jsontransform.functions.common.TransformerFunction;
import ai.bizone.jsontransform.formats.formurlencoded.FormUrlEncodedFormat;

public class TransformerFunctionFormParse extends TransformerFunction {

    public TransformerFunctionFormParse() {
        super();
    }

    @Override
    public Object apply(FunctionContext context) {
        // TODO: how to create the format once?
        return new FormUrlEncodedFormat(context.getAdapter()).deserialize(context.getString(null));
    }
}
