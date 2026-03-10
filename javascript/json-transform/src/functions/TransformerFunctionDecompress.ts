import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import TextEncoding from "./common/TextEncoding";

class TransformerFunctionDecompress extends TransformerFunction {
  constructor() {
    super({
      argsSet: [
        { name: "charset", type: ArgType.String, defaultValue: "ISO-8859-1" },
        { name: "format", type: ArgType.String, defaultValue: "UTF-8" },
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    const charset = await context.getEnum("charset");
    if (!charset) return null;
    const format = await context.getEnum("format");
    if (!format) return null;

    const bytes = TextEncoding.encode(str, charset);
    const stream = new Blob([bytes], { type: "text/plain" }).stream();
    const decompressedStream = stream.pipeThrough(new DecompressionStream("gzip"));
    const response = new Response(decompressedStream);
    const outBytes = await response.blob().then(b => b.arrayBuffer());
    return TextEncoding.decode(new Uint8Array(outBytes), format);
  }
}

export default TransformerFunctionDecompress;
