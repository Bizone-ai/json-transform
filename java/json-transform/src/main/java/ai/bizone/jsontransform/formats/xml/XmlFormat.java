package ai.bizone.jsontransform.formats.xml;

import ai.bizone.jsontransform.adapters.JsonAdapter;
import ai.bizone.jsontransform.formats.FormatDeserializer;
import ai.bizone.jsontransform.formats.FormatSerializer;

import java.util.Collections;
import java.util.Set;

public class XmlFormat implements FormatSerializer, FormatDeserializer {

    static private final boolean DEFAULT_KEEP_STRINGS = false;

    static private final String DEFAULT_ATTR_NAME_PREFIX = "@";
    static private final String DEFAULT_CDATA_PROP_NAME = "#text";
    static private final Set<String> DEFAULT_FORCE_LIST = Collections.emptySet();

    private final javax.xml.transform.Transformer xslt; // function of 'xslt' field

    private final XmlBuilder xmlBuilder;
    private final XmlParser xmlParser;

    public XmlFormat(JsonAdapter<?, ?, ?> adapter,
                     String xslt,
                     final String rootTagName,
                     final Boolean indent,
                     final String attrPrefix,
                     final String cDataPropName,
                     final Boolean keepStrings,
                     final Set<String> array_tags) {
        this.xslt = XmlTransformer.createXSLTTransformer(xslt);
        this.xmlParser = new XmlParser(adapter, new XmlParserConfig(
                keepStrings != null ? keepStrings : DEFAULT_KEEP_STRINGS,
                attrPrefix != null ? attrPrefix : DEFAULT_ATTR_NAME_PREFIX,
                cDataPropName != null ? cDataPropName : DEFAULT_CDATA_PROP_NAME,
                array_tags != null ? array_tags : DEFAULT_FORCE_LIST));
        this.xmlBuilder = new XmlBuilder(adapter, new XmlBuilderConfig(
                rootTagName,
                indent != null && indent,
                attrPrefix != null ? attrPrefix : DEFAULT_ATTR_NAME_PREFIX,
                cDataPropName != null ? cDataPropName : DEFAULT_CDATA_PROP_NAME,
                false
        ));
    }
    public XmlFormat(JsonAdapter<?, ?, ?> adapter, String rootTagName, String xslt) {
        this(adapter, xslt, rootTagName, null, null, null, null, null);
    }

    public XmlFormat(JsonAdapter<?, ?, ?> adapter) {
        this(adapter, null, null, null, null, null, null, null);
    }


    @Override
    public String serialize(Object payload) {
        var outputXml = xmlBuilder.build(payload);
        return xslt == null || outputXml == null ? outputXml : XmlTransformer.xmlTransform(outputXml, xslt);
    }

    @Override
    public Object deserialize(String input) {
        if (input == null) return null;
        return xmlParser.parse(input);
    }
}
