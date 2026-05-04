package ai.bizone.jsontransform.formats.xml;

import ai.bizone.jsontransform.adapters.JsonAdapter;

import java.util.Iterator;

/**
 * Most of the code was taken from org.json.XML
 */
public class XmlBuilder {
    private final XmlBuilderConfig config;
    private final JsonAdapter<?, ?, ?> adapter;

    public XmlBuilder(JsonAdapter<?, ?, ?> adapter, XmlBuilderConfig config) {
        this.config = config;
        this.adapter = adapter;
    }

    public String build(final Object object) {
        if (object == null) return null;
        var payload = object;
        if (!adapter.is(payload)) {
            payload = adapter.wrap(payload);
        }
        return toString(payload, config.rootTagName(), config.indent() ? 2 : 0, 0);
    }

    /**
     * Convert a JSONObject into a well-formed, element-normal XML string,
     * either pretty print or single-lined depending on indent factor.
     *
     * @param object
     *            A JSONObject.
     * @param tagName
     *            The optional name of the enclosing tag.
     * @param indentFactor
     *            The number of spaces to add to each level of indentation.
     * @param indent
     *            The current ident level in spaces.
     */
    private String toString(final Object object, final String tagName, int indentFactor, int indent) {
        StringBuilder sb = new StringBuilder();
        String string;

        if (adapter.isJsonObject(object)) {
            if (tagName != null && !tagName.isEmpty()) {
                sb.append(indent(indent));
                sb.append('<');
                sb.append(tagName);

                for (final String key : adapter.keySet(object)) {
                    if (!key.startsWith(config.attributePrefix()) || key.equals(config.attributePrefix())) continue;

                    Object value = adapter.get(object, key);
                    sb.append(" ").append(key.substring(1)).append("=\"").append(escape(adapter.getAsString(value))).append("\"");
                }

                sb.append('>');
                if(indentFactor > 0){
                    sb.append("\n");
                    indent += indentFactor;
                }
            }

            for (final String key : adapter.keySet(object)) {
                if (key.startsWith(config.attributePrefix()) && !key.equals(config.attributePrefix())) continue;

                Object value = adapter.get(object, key);
                if (value == null) {
                    value = "";
                }

                if (key.equals(config.cDataPropName())) {
                    if (adapter.isJsonArray(value)) {
                        int jaLength = adapter.size(object);
                        // don't use the new iterator API to maintain support for Android
                        for (int i = 0; i < jaLength; i++) {
                            if (i > 0) {
                                sb.append('\n');
                            }
                            Object val = adapter.get(object, i);
                            sb.append(escape(adapter.getAsString(val)));
                        }
                    } else {
                        sb.append(escape(adapter.getAsString(value)));
                    }
                } else if (adapter.isJsonArray(value)) {
                    int jaLength = adapter.size(value);
                    for (int i = 0; i < jaLength; i++) {
                        Object val = adapter.get(value, i);
                        if (adapter.isJsonArray(val)) {
                            sb.append('<');
                            sb.append(key);
                            sb.append('>');
                            sb.append(toString(val, null, indentFactor, indent));
                            sb.append("</");
                            sb.append(key);
                            sb.append('>');
                        } else {
                            sb.append(toString(val, key, indentFactor, indent));
                        }
                    }
                } else if ((value instanceof String s && s.isEmpty()) || (adapter.isJsonString(value) && adapter.getAsString(value).isEmpty())) {
                    if (config.closeEmptyTag()){
                        sb.append(indent(indent));
                        sb.append('<');
                        sb.append(key);
                        sb.append(">");
                        sb.append("</");
                        sb.append(key);
                        sb.append(">");
                        if (indentFactor > 0) {
                            sb.append("\n");
                        }
                    }else {
                        sb.append(indent(indent));
                        sb.append('<');
                        sb.append(key);
                        sb.append("/>");
                        if (indentFactor > 0) {
                            sb.append("\n");
                        }
                    }
                } else {
                    sb.append(toString(value, key, indentFactor, indent));
                }
            }
            if (tagName != null) {
                sb.append(indent(indent - indentFactor));
                sb.append("</");
                sb.append(tagName);
                sb.append('>');
                if(indentFactor > 0){
                    sb.append("\n");
                }
            }
            return sb.toString();
        }

        if (object != null && adapter.isJsonArray(object)) {
            int jaLength = adapter.size(object);
            for (int i = 0; i < jaLength; i++) {
                Object val = adapter.get(object, i);
                // XML does not have good support for arrays. If an array
                // appears in a place where XML is lacking, synthesize an
                // <array> element.
                sb.append(toString(val, tagName == null ? "array" : tagName, indentFactor, indent));
            }
            return sb.toString();
        }

        string = (object == null) ? "null" : escape(adapter.getAsString(object));
        String indentationSuffix = (indentFactor > 0) ? "\n" : "";
        if(tagName == null){
            return indent(indent) + "\"" + string + "\"" + indentationSuffix;
        } else if(string.isEmpty()){
            return indent(indent) + "<" + tagName + "/>" + indentationSuffix;
        } else {
            return indent(indent) + "<" + tagName + ">" + string + "</" + tagName + ">" + indentationSuffix;
        }
    }

    /**
     * Creates an iterator for navigating Code Points in a string instead of
     * characters. Once Java7 support is dropped, this can be replaced with
     * <code>
     * string.codePoints()
     * </code>
     * which is available in Java8 and above.
     *
     * @see <a href=
     *      "http://stackoverflow.com/a/21791059/6030888">http://stackoverflow.com/a/21791059/6030888</a>
     */
    private static Iterable<Integer> codePointIterator(final String string) {
        return new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    private int nextIndex = 0;
                    private final int length = string.length();

                    @Override
                    public boolean hasNext() {
                        return this.nextIndex < this.length;
                    }

                    @Override
                    public Integer next() {
                        int result = string.codePointAt(this.nextIndex);
                        this.nextIndex += Character.charCount(result);
                        return result;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    private static String indent(int indent) {
        return " ".repeat(Math.max(0, indent));
    }

    /**
     * Replace special characters with XML escapes:
     *
     * <pre>{@code
     * &amp; (ampersand) is replaced by &amp;amp;
     * &lt; (less than) is replaced by &amp;lt;
     * &gt; (greater than) is replaced by &amp;gt;
     * &quot; (double quote) is replaced by &amp;quot;
     * &apos; (single quote / apostrophe) is replaced by &amp;apos;
     * }</pre>
     *
     * @param string
     *            The string to be escaped.
     * @return The escaped string.
     */
    public static String escape(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        for (final int cp : codePointIterator(string)) {
            switch (cp) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                default:
                    if (mustEscape(cp)) {
                        sb.append("&#x");
                        sb.append(Integer.toHexString(cp));
                        sb.append(';');
                    } else {
                        sb.appendCodePoint(cp);
                    }
            }
        }
        return sb.toString();
    }

    /**
     * @param cp code point to test
     * @return true if the code point is not valid for an XML
     */
    private static boolean mustEscape(int cp) {
        /* Valid range from https://www.w3.org/TR/REC-xml/#charsets
         *
         * #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
         *
         * any Unicode character, excluding the surrogate blocks, FFFE, and FFFF.
         */
        // isISOControl is true when (cp >= 0 && cp <= 0x1F) || (cp >= 0x7F && cp <= 0x9F)
        // all ISO control characters are out of range except tabs and new lines
        return (Character.isISOControl(cp)
                && cp != 0x9
                && cp != 0xA
                && cp != 0xD
        ) || !(
                // valid the range of acceptable characters that aren't control
                (cp >= 0x20 && cp <= 0xD7FF)
                        || (cp >= 0xE000 && cp <= 0xFFFD)
                        || (cp >= 0x10000 && cp <= 0x10FFFF)
        )
                ;
    }

}
