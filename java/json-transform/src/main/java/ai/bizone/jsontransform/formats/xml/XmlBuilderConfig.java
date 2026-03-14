package ai.bizone.jsontransform.formats.xml;

public record XmlBuilderConfig(
        String rootTagName,
        boolean indent,
        String attributePrefix,
        String cDataPropName,
        boolean closeEmptyTag
) {}
