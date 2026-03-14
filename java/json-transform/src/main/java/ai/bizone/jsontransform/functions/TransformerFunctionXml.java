package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.formats.xml.XmlFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class TransformerFunctionXml extends TransformerFunction {
    static final Logger logger = LoggerFactory.getLogger(TransformerFunctionXml.class);

    public TransformerFunctionXml() {
        super(FunctionDescription.of(
                Map.of(
                        "root", ArgumentType.of(ArgType.String).position(0),
                        "indent", ArgumentType.of(ArgType.Boolean).position(1).defaultValue(false),
                        "attr_prefix", ArgumentType.of(ArgType.String).position(2).defaultValue("@"),
                        "cdata_prop_name", ArgumentType.of(ArgType.String).position(3).defaultValue("#text")
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
            var indent = context.getBoolean("indent");
            var attrPrefix = context.getString("attr_prefix");
            var cDataPropName = context.getString("cdata_prop_name");
            return new XmlFormat(context.getAdapter(),
                null,
                rootName,
                indent,
                attrPrefix,
                cDataPropName,
                null,
                null
            ).serialize(obj);
        } catch (Exception e) {
            logger.warn(context.getAlias() + " failed", e);
            return null;
        }
    }
}
