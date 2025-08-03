package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.formats.xml.XmlFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TransformerFunctionXml extends TransformerFunction {
    static final Logger logger = LoggerFactory.getLogger(TransformerFunctionXml.class);

    public TransformerFunctionXml() {
        super(FunctionDescription.of(
                Map.of(
                        "root", ArgumentType.of(ArgType.String).position(0),
                        "xslt", ArgumentType.of(ArgType.String).position(1),
                        "indent", ArgumentType.of(ArgType.Boolean).position(2).defaultValue(false)
                )
        ));
    }

    @Override
    public Object apply(FunctionContext context) {
        var obj = context.getJsonElement(null);
        if (obj == null)
            return null;
        try {
            var rootName = context.getString("root");
            // TODO: how to create the format once?
            var xml = new XmlFormat(context.getAdapter()).obj2xml(obj, rootName);
            var xslt = context.getString("xslt");
            if (xslt != null && !xslt.isBlank()) {
                var transformer = XmlFormat.createXSLTTransformer(xslt);
                xml = XmlFormat.xmlTransform(xml, transformer);
            }
            if (context.getBoolean("indent")) {
                xml = XmlFormat.indentXml(xml);
            }
            return xml;
        } catch (Exception e) {
            logger.warn(context.getAlias() + " failed", e);
            return null;
        }
    }
}
