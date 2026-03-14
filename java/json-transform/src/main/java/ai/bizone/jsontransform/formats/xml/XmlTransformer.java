package ai.bizone.jsontransform.formats.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlTransformer {
    static final Logger log = LoggerFactory.getLogger(XmlTransformer.class);

    public static javax.xml.transform.Transformer createXSLTTransformer(String xslt) {
        if (xslt == null) return null;
        javax.xml.transform.Transformer transformer = null;
        try {
            // prepare xslt source
            var xsltReader = new StringReader(xslt);
            var xsltSource = new StreamSource(xsltReader);
            transformer = TransformerFactory.newInstance().newTransformer(xsltSource);
        } catch (Throwable te) {
            log.error("Failed parsing XSLT", te);
        }
        return transformer;
    }

    public static String xmlTransform(String input, javax.xml.transform.Transformer transformer) {
        try {
            var xmlReader = new StringReader(input);
            var xmlSource = new StreamSource(xmlReader);

            // prepare output stream
            var outWriter = new StringWriter();
            var result = new StreamResult(outWriter);

            // transform
            transformer.transform(xmlSource, result);

            // return result
            return outWriter.getBuffer().toString();
        }
        catch (Throwable te) {
            log.warn("Failed formatting to XML using provided input and XSLT", te);
            throw new RuntimeException(te);
        }
    }
}
