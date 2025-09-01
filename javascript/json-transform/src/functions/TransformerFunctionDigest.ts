import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import TextEncoding from "./common/TextEncoding";
import Base64 from "./utils/Base64";
import md5 from "./utils/md5";

const globalCrypto = typeof global.crypto !== "undefined" ? global.crypto : require("crypto");

function formatHex(a: ArrayBuffer) {
  return [...new Uint8Array(a)].map(x => x.toString(16).padStart(2, "0")).join("");
}

class TransformerFunctionDigest extends TransformerFunction {
  constructor() {
    super({
      argsSet: [
        { name: "algorithm", type: ArgType.String, defaultValue: "SHA-1" },
        { name: "format", type: ArgType.String, defaultValue: "BASE64" },
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    const algorithm = await context.getEnum("algorithm");
    if (!algorithm) return null;

    const digest =
      algorithm === "MD5"
        ? md5(str)
        : await globalCrypto.subtle.digest({ name: algorithm }, TextEncoding.encode(str, "ISO-8859-1"));
    switch (await context.getEnum("format")) {
      case "BASE64":
        return Base64.encode(new Uint8Array(digest), "basic");
      case "BASE64URL":
        return Base64.encode(new Uint8Array(digest), "url");
      default:
        return formatHex(digest);
    }
  }
}

export default TransformerFunctionDigest;
