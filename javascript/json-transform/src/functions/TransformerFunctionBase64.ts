import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import TextEncoding from "./common/TextEncoding";
import Base64 from "./utils/Base64";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionBase64 extends TransformerFunction {
  constructor() {
    super({
      argsSets: [
        [
          { name: "action", type: ArgType.String, defaultValue: "ENCODE" },
          { name: "rfc", type: ArgType.String, defaultValue: "BASIC" },
          { name: "without_padding", type: ArgType.Boolean, defaultValue: false },
          { name: "charset", type: ArgType.String, defaultValue: "UTF-8" },
        ],
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    // parse arguments
    const encode = (await context.getEnum("action"))?.charAt(0) == "E"; // E / Enc / Encode (anything else like d / dec / decode is for decoding)
    const rfc = (await context.getEnum("rfc")) ?? "B"; // B = basic / U = url / M = mime
    const withoutPadding = await context.getBoolean("without_padding", false);
    const charset = await context.getEnum("charset");
    const rfcType = rfc.charAt(0) === "U" ? "url" : rfc.charAt(0) === "M" ? "mime" : "basic";

    try {
      if (encode) {
        let input = TextEncoding.encode(str, charset);
        return Base64.encode(input, rfcType, withoutPadding);
      } else {
        const buffer = Base64.decode(str, rfcType);
        return TextEncoding.decode(buffer, charset);
      }
    } catch (e: any) {
      console.warn(context.getAlias() + " failed", e);
      return null;
    }
  }
}

export default TransformerFunctionBase64;
