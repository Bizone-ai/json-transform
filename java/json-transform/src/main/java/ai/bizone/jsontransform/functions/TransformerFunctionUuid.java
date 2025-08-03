package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.jsontransform.functions.common.*;
import ai.bizone.shortuuid.ShortUuid;
import ai.bizone.shortuuid.UuidConverter;

import java.util.Map;
import java.util.UUID;

public class TransformerFunctionUuid extends TransformerFunction {
    public TransformerFunctionUuid() {
        super(FunctionDescription.of(
                Map.of(
                        "format", ArgumentType.of(ArgType.String).position(0).defaultValue("CANONICAL"),
                        "namespace", ArgumentType.of(ArgType.String).position(1)
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var value = context.getUnwrapped(null);
        if (value == null) {
            return null;
        }
        var format = context.getEnum("format");
        if ("V3".equals(format) || "V5".equals(format)) {
            var name = context.getAsString(value);
            var namespaceValue = context.get("namespace");
            var namespace = namespaceValue == null
                            ? null
                            : namespaceValue instanceof UUID uu
                              ? uu
                              : UUID.fromString(context.getAsString(namespaceValue));
            return UuidConverter.namedByVersion("V3".equals(format) ? 3 : 5, namespace, name).toString();
        }
        var uuid = value instanceof UUID uu ? uu : UUID.fromString(context.getAsString(value));
        return switch (format) {
            case "N", "NO_HYPHENS" -> uuid.toString().replace("-", "");
            case "B62", "BASE62" -> ShortUuid.toShortUuid(uuid, true);
            case "B64", "BASE64" -> UuidConverter.toBase64(uuid);
            case "B36", "BASE36" -> ShortUuid.toShortUuid(uuid, false);
            default -> uuid.toString();
        };
    }
}
