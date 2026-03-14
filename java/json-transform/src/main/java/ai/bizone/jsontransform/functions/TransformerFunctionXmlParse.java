package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.formats.xml.XmlFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class TransformerFunctionXmlParse extends TransformerFunction {
    static final Logger logger = LoggerFactory.getLogger(TransformerFunctionXmlParse.class);
    public TransformerFunctionXmlParse() {
        super(FunctionDescription.of(
            Map.of(
            "keep_strings", ArgumentType.of(ArgType.Boolean).position(0).defaultValue(false),
            "attr_prefix", ArgumentType.of(ArgType.String).position(1).defaultValue("@"),
            "cdata_prop_name", ArgumentType.of(ArgType.String).position(2).defaultValue("#text"),
            "array_tags", ArgumentType.of(ArgType.Array).position(3)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var xml = context.getString(null);
        if (xml == null)
            return null;
        try {
            var keepStrings = context.getBoolean("keep_strings");
            var attrPrefix = context.getString("attr_prefix");
            var cDataPropName = context.getString("cdata_prop_name");
            var arrayTags = context.getJsonArray("array_tags");
            var adapter = context.getAdapter();
            return new XmlFormat(adapter,
                null,
                null,
                null,
                attrPrefix,
                cDataPropName,
                keepStrings,
                arrayTags == null ? null : adapter.stream(arrayTags).map(context::getAsString).collect(Collectors.toSet())
            ).deserialize(xml);
        } catch (Exception e) {
            logger.warn(context.getAlias() + " failed", e);
            return null;
        }
    }
}
