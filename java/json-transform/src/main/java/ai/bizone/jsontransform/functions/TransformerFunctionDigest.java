package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;

public class TransformerFunctionDigest extends TransformerFunction {
    public TransformerFunctionDigest() {
        super(FunctionDescription.of(
            Map.of(
            "algorithm", ArgumentType.of(ArgType.String).position(0).defaultValue("SHA-1"),
            "format", ArgumentType.of(ArgType.String).position(1).defaultValue("BASE64"),
            "charset", ArgumentType.of(ArgType.String).position(2).defaultValue("UTF-8")
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        var algorithm = context.getEnum("algorithm");
        var charset = context.getEnum("charset");
        try {
            var digest = MessageDigest.getInstance(algorithm).digest(str.getBytes(charset));
            return switch (context.getEnum("format")) {
                case "BASE64" -> Base64.getEncoder().encodeToString(digest);
                case "BASE64URL" -> Base64.getUrlEncoder().encodeToString(digest);
                default -> HexFormat.of().formatHex(digest);
            };
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
