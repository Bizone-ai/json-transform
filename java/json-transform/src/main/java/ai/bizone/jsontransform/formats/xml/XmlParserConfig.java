package ai.bizone.jsontransform.formats.xml;

import java.util.Set;

public record XmlParserConfig(
        boolean keepStrings,
        String attributePrefix,
        String cDataPropName,
        Set<String> arrayTags
) {
    public boolean convertNilAttributeToNull() {
        return false;
    }

    public int getMaxNestingDepth() {
        return 256;
    }

    public boolean shouldTrimWhiteSpace() {
        return true;
    }
}
