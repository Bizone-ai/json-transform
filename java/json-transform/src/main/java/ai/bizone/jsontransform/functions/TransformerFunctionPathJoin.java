package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TransformerFunctionPathJoin extends TransformerFunction {
    private static final String POSIX_SEPARATOR = "/";
    private static final String WINDOWS_SEPARATOR = "\\";
    private static final String WINDOWS_SEPARATOR_ESC = "\\\\";

    private static Pattern normalizePatternFactory(String s) {
        return Pattern.compile("([^." + s + "]+\\.|[^" + s + "]*[^." + s + "]|[^" + s + "]{3,})" + s + "\\.\\.($|" + s + ")");
    }
    private static final Pattern POSIX_DEDUPE_PATTERN = Pattern.compile(POSIX_SEPARATOR + "{2,}");
    private static final Pattern POSIX_DIRUP_PATTERN = normalizePatternFactory(POSIX_SEPARATOR);
    private static final Pattern POSIX_TRAILING_PATTERN = Pattern.compile(POSIX_SEPARATOR + "+$");
    private static final Pattern POSIX_SAMEDIR_PATTERN = Pattern.compile(POSIX_SEPARATOR + "([.]" + POSIX_SEPARATOR + ")+");
    private static final Pattern POSIX_SAMEDIR_START_PATTERN = Pattern.compile("^[.]" + POSIX_SEPARATOR);
    private static final Pattern WINDOWS_DEDUPE_PATTERN = Pattern.compile(WINDOWS_SEPARATOR_ESC + "{2,}");
    private static final Pattern WINDOWS_DIRUP_PATTERN = normalizePatternFactory(WINDOWS_SEPARATOR_ESC);
    private static final Pattern WINDOWS_TRAILING_PATTERN = Pattern.compile(WINDOWS_SEPARATOR_ESC + "+$");
    private static final Pattern WINDOWS_SAMEDIR_PATTERN = Pattern.compile(WINDOWS_SEPARATOR_ESC + "([.]" + WINDOWS_SEPARATOR_ESC + ")+");
    private static final Pattern WINDOWS_SAMEDIR_START_PATTERN = Pattern.compile("^[.]" + WINDOWS_SEPARATOR_ESC);

    public TransformerFunctionPathJoin() {
        super(FunctionDescription.of(
            Map.of(
            "type", ArgumentType.of(ArgType.String).position(0).defaultValue("POSIX")
            )
        ));
    }

    private static String normalizePath(String rawPath, String separator, boolean isURL) {
        var path = separator + rawPath;
        var isWin = separator.equals(WINDOWS_SEPARATOR);
        var escSep = isWin ? WINDOWS_SEPARATOR_ESC : separator; // need to escape for patterns replacement
        path = (isWin ? WINDOWS_DEDUPE_PATTERN : POSIX_DEDUPE_PATTERN).matcher(path).replaceAll(escSep); // dedupe all repeating separators

        // normalize dir-up
        var normalizer = isWin ? WINDOWS_DIRUP_PATTERN : POSIX_DIRUP_PATTERN;
        while (path.contains("..") && path.indexOf(separator + "..") != 0) {
            var lengthBefore = path.length();
            path = normalizer.matcher(path).replaceAll("");
            if (lengthBefore == path.length()) {
                break; // did nothing then break
            }
        }
        if (path.indexOf(separator + ".." + separator) == 0 || path.equals(separator + "..")) {
            throw new Error("Invalid path \"" + rawPath + "\"");
        }
        path = (isWin ? WINDOWS_TRAILING_PATTERN : POSIX_TRAILING_PATTERN).matcher(path).replaceAll(""); // remove trailing separators
        path = (isWin ? WINDOWS_SAMEDIR_PATTERN : POSIX_SAMEDIR_PATTERN).matcher(path).replaceAll(escSep);
        path = (isWin ? WINDOWS_SAMEDIR_START_PATTERN : POSIX_SAMEDIR_START_PATTERN).matcher(path).replaceAll(escSep);
        return isURL ? path : path.substring(1);
    }

    @Override
    public Object apply(FunctionContext context) {
        var arr = context.getJsonElementStreamer(null);
        if (arr == null) {
            return null;
        }
        var type = context.getEnum("type"); // should be either "/" or "\"
        var separator = type.equals("WINDOWS") ? WINDOWS_SEPARATOR : POSIX_SEPARATOR;

        var stream = arr.stream()
                .map(context::getAsString)
                .filter(x -> x != null && !x.isEmpty());
        var isURL = "URL".equals(type);
        if (isURL) {
            stream = stream.map(x -> URLEncoder.encode(x, StandardCharsets.UTF_8));
        }
        var path = stream.collect(Collectors.joining(separator));
        return normalizePath(path, separator, isURL);
    }
}
