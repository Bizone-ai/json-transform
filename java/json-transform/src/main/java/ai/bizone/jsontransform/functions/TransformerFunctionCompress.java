package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class TransformerFunctionCompress extends TransformerFunction {
    static final Logger log = LoggerFactory.getLogger(TransformerFunctionCompress.class);

    public TransformerFunctionCompress() {
        super(FunctionDescription.of(
                Map.of(
                        "charset", ArgumentType.of(ArgType.String).position(0).defaultValue("UTF-8")
                )
        ));
    }

    @Override
    public Object apply(FunctionContext context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        var charset = context.getEnum("charset");
        if (charset == null) {
            return null;
        }

        try {
            var out = new ByteArrayOutputStream();
            var gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(charset));
            gzip.close();
            return out.toString(StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
