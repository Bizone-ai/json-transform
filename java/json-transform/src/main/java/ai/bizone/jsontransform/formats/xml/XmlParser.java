package ai.bizone.jsontransform.formats.xml;

import ai.bizone.jsontransform.adapters.JsonAdapter;
import org.json.*;

import java.io.Reader;
import java.io.StringReader;

/**
 * Most of the code was taken from org.json.XML
 */
public class XmlParser {
    private final XmlParserConfig config;
    private final JsonAdapter<?, ?, ?> adapter;

    public XmlParser(JsonAdapter<?, ?, ?> adapter, XmlParserConfig config) {
        this.config = config;
        this.adapter = adapter;
    }

    public Object parse(String input) {
        return toJSONObject(new StringReader(input));
    }

    /**
     * Convert a well-formed (but not necessarily valid) XML into a
     * JSONObject. Some information may be lost in this transformation because
     * JSON is a data format and XML is a document format. XML uses elements,
     * attributes, and content text, while JSON uses unordered collections of
     * name/value pairs and arrays of values. JSON does not does not like to
     * distinguish between elements and attributes. Sequences of similar
     * elements are represented as JSONArrays. Content text may be placed in a
     * "content" member. Comments, prologs, DTDs, and <pre>{@code
     * &lt;[ [ ]]>}</pre>
     * are ignored.
     *
     * All values are converted as strings, for 1, 01, 29.0 will not be coerced to
     * numbers but will instead be the exact value as seen in the XML document.
     *
     * @param reader The XML source reader.
     * @return A JSONObject containing the structured data from the XML string.
     */
    private Object toJSONObject(Reader reader) {
        Object jo = adapter.createObject();
        var options = new XMLParserConfiguration()
                .withKeepStrings(config.keepStrings())
                .withcDataTagName(config.cDataPropName())
                .withForceList(config.arrayTags());
        XMLTokener x = new XMLTokener(reader, options);
        while (x.more()) {
            x.skipPast("<");
            if(x.more()) {
                parse(x, jo, null, 0);
            }
        }
        return jo;
    }

    private Object stringToValue(String string) {
        try {
            return adapter.parse(string);
        } catch (Throwable ignored) {

        }
        return string;
    }

    private boolean parse(XMLTokener x, Object context, String name, int currentNestingDepth) {
        char c;
        int i;
        Object jsonObject;
        String string;
        String tagName;
        Object token;

        // Test for and skip past these forms:
        // <!-- ... -->
        // <! ... >
        // <![ ... ]]>
        // <? ... ?>
        // Report errors for these forms:
        // <>
        // <=
        // <<

        token = x.nextToken();

        // <!

        if (token == XML.BANG) {
            c = x.next();
            if (c == '-') {
                if (x.next() == '-') {
                    x.skipPast("-->");
                    return false;
                }
                x.back();
            } else if (c == '[') {
                token = x.nextToken();
                if ("CDATA".equals(token)) {
                    if (x.next() == '[') {
                        string = x.nextCDATA();
                        if (!string.isEmpty()) {
                            adapter.add(config.cDataPropName(), string);
                        }
                        return false;
                    }
                }
                throw x.syntaxError("Expected 'CDATA['");
            }
            i = 1;
            do {
                token = x.nextMeta();
                if (token == null) {
                    throw x.syntaxError("Missing '>' after '<!'.");
                } else if (token == XML.LT) {
                    i += 1;
                } else if (token == XML.GT) {
                    i -= 1;
                }
            } while (i > 0);
            return false;
        } else if (token == XML.QUEST) {

            // <?
            x.skipPast("?>");
            return false;
        } else if (token == XML.SLASH) {

            // Close tag </

            token = x.nextToken();
            if (name == null) {
                throw x.syntaxError("Mismatched close tag " + token);
            }
            if (!token.equals(name)) {
                throw x.syntaxError("Mismatched " + name + " and " + token);
            }
            if (x.nextToken() != XML.GT) {
                throw x.syntaxError("Misshaped close tag");
            }
            return true;

        } else if (token instanceof Character) {
            throw x.syntaxError("Misshaped tag");

            // Open tag <

        } else {
            tagName = (String) token;
            token = null;
            jsonObject = adapter.createObject();
            boolean nilAttributeFound = false;
            for (;;) {
                if (token == null) {
                    token = x.nextToken();
                }
                // attribute = value
                if (token instanceof String) {
                    string = (String) token;
                    token = x.nextToken();
                    if (token == XML.EQ) {
                        token = x.nextToken();
                        if (!(token instanceof String)) {
                            throw x.syntaxError("Missing value");
                        }

                        if (config.convertNilAttributeToNull()
                                && XML.NULL_ATTR.equals(string)
                                && Boolean.parseBoolean((String) token)) {
                            nilAttributeFound = true;
                        } else if (!nilAttributeFound) {
                            accumulate(jsonObject, config.attributePrefix() + string,
                                    config.keepStrings()
                                            ? token
                                            : stringToValue((String) token));
                        }
                        token = null;
                    } else {
                        accumulate(jsonObject, config.attributePrefix() + string, true);
                    }

                } else if (token == XML.SLASH) {
                    // Empty tag <.../>
                    if (x.nextToken() != XML.GT) {
                        throw x.syntaxError("Misshaped tag");
                    }
                    if (config.arrayTags().contains(tagName)) {
                        // Force the value to be an array
                        if (nilAttributeFound) {
                            append(context, tagName, adapter.jsonNull());
                        } else if (adapter.size(jsonObject) > 0) {
                            append(context, tagName, jsonObject);
                        } else {
                            adapter.add(context, tagName, adapter.createArray());
                        }
                    } else {
                        if (nilAttributeFound) {
                            accumulate(context, tagName, adapter.jsonNull());
                        } else if (adapter.size(jsonObject) > 0) {
                            accumulate(context, tagName, jsonObject);
                        } else {
                            accumulate(context, tagName, "");
                        }
                    }
                    return false;

                } else if (token == XML.GT) {
                    // Content, between <...> and </...>
                    for (;;) {
                        token = x.nextContent();
                        if (token == null) {
                            if (tagName != null) {
                                throw x.syntaxError("Unclosed tag " + tagName);
                            }
                            return false;
                        } else if (token instanceof String) {
                            string = (String) token;
                            if (!string.isEmpty()) {
                                accumulate(jsonObject, config.cDataPropName(),
                                        config.keepStrings() ? string : stringToValue(string));
                            }

                        } else if (token == XML.LT) {
                            // Nested element
                            if (currentNestingDepth == config.getMaxNestingDepth()) {
                                throw x.syntaxError("Maximum nesting depth of " + config.getMaxNestingDepth() + " reached");
                            }

                            if (parse(x, jsonObject, tagName, currentNestingDepth + 1)) {
                                if (config.arrayTags().contains(tagName)) {
                                    // Force the value to be an array
                                    if (adapter.size(jsonObject) == 0) {
                                        adapter.add(context, tagName, adapter.createArray());
                                    } else if (adapter.size(jsonObject) == 1
                                            && adapter.get(jsonObject, config.cDataPropName()) != null) {
                                        append(context, tagName, adapter.get(jsonObject, config.cDataPropName()));
                                    } else {
                                        append(context, tagName, jsonObject);
                                    }
                                } else {
                                    if (adapter.size(jsonObject) == 0) {
                                        accumulate(context, tagName, "");
                                    } else if (adapter.size(jsonObject) == 1
                                            && adapter.get(jsonObject, config.cDataPropName()) != null) {
                                        accumulate(context, tagName, adapter.get(jsonObject, config.cDataPropName()));
                                    } else {
                                        if (!config.shouldTrimWhiteSpace()) {
                                            removeEmpty(jsonObject);
                                        }
                                        accumulate(context, tagName, jsonObject);
                                    }
                                }

                                return false;
                            }
                        }
                    }
                } else {
                    throw x.syntaxError("Misshaped tag");
                }
            }
        }
    }

    /**
     * Append values to the array under a key.
     * If the key does not exist in the JSONObject,
     * then the key is put in the JSONObject with its value being a JSONArray containing the value parameter.
     * If the key was already associated with a JSONArray, then the value parameter is appended to it.
     */
    private void append(Object obj, String s, Object value) {
        if (!adapter.has(obj, s)) {
            var arr = adapter.createArray();
            adapter.add(arr, value);
            adapter.add(obj, s, arr);
        } else {
            var x = adapter.get(obj, s);
            if (adapter.isJsonArray(x)) {
                adapter.add(x, value);
            }
        }
    }

    private void accumulate(Object obj, String s, Object value) {
        if (adapter.has(obj, s)) {
            var existingValue = adapter.get(obj, s);
            var newValue = adapter.createArray();
            adapter.add(newValue, existingValue);
            adapter.add(newValue, value);
            adapter.add(obj, s, newValue);
        } else {
            adapter.add(obj, s, value);
        }
    }

    /**
     * This method removes any JSON entry which has the key set by XMLParserConfiguration.cDataTagName
     * and contains whitespace as this is caused by whitespace between tags. See test XMLTest.testNestedWithWhitespaceTrimmingDisabled.
     * @param jsonObject JSONObject which may require deletion
     */
    private void removeEmpty(final Object jsonObject) {
        if (adapter.has(jsonObject, config.cDataPropName()))  {
            final Object s = adapter.get(jsonObject, config.cDataPropName());
            if (adapter.isJsonString(s)) {
                if (isStringAllWhiteSpace(adapter.getAsString(s))) {
                    adapter.remove(jsonObject, config.cDataPropName());
                }
            }
            else if (adapter.isJsonArray(s)) {
                for (int k = adapter.size(s) - 1; k >= 0; k--){
                    final Object eachString = adapter.get(s, k);
                    if (adapter.isJsonString(eachString)) {
                        String s1 = adapter.getAsString(eachString);
                        if (isStringAllWhiteSpace(s1)) {
                            adapter.remove(s, k);
                        }
                    }
                }
                if (adapter.size(s) == 0) {
                    adapter.remove(jsonObject, config.cDataPropName());
                }
            }
        }
    }

    private static boolean isStringAllWhiteSpace(final String s) {
        for (int k = 0; k<s.length(); k++){
            final char eachChar = s.charAt(k);
            if (!Character.isWhitespace(eachChar)) {
                return false;
            }
        }
        return true;
    }

}
