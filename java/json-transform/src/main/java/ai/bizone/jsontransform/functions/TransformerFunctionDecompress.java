package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;

import java.io.*;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class TransformerFunctionDecompress extends TransformerFunction {
    public TransformerFunctionDecompress() {
        super(FunctionDescription.of(
                Map.of(
                        "charset", ArgumentType.of(ArgType.String).position(0).defaultValue("ISO-8859-1"),
                        "format", ArgumentType.of(ArgType.String).position(1).defaultValue("UTF-8")
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
        var format = context.getEnum("format");
        if (format == null) {
            return null;
        }

        try (var bais = new ByteArrayInputStream(str.getBytes(charset));
             var gzip = new GZIPInputStream(bais);
             var isr = new InputStreamReader(gzip, format);
             var sw = new StringWriter()) {

            char[] buffer = new char[8192];
            int len;
            while ((len = isr.read(buffer)) > 0) {
                sw.write(buffer, 0, len);
            }
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
