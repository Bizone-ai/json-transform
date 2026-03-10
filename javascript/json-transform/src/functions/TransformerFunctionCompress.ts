import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import TextEncoding from "./common/TextEncoding";

class TransformerFunctionCompress extends TransformerFunction {
  constructor() {
    super({
      argsSet: [{ name: "charset", type: ArgType.String, defaultValue: "UTF-8" }],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    const charset = await context.getEnum("charset");
    if (!charset) return null;

    const bytes = TextEncoding.encode(str, charset);
    const stream = new Blob([bytes], { type: "text/plain" }).stream();
    const compressedStream = stream.pipeThrough(new CompressionStream("gzip"));
    const response = new Response(compressedStream);
    const outBytes = await response.blob().then(b => b.arrayBuffer());
    const array = new Uint8Array(outBytes);
    array[9] = 0xff; // override the OS header (hide OS; align with Java)
    return TextEncoding.decode(array, "ISO-8859-1");
  }
}

export default TransformerFunctionCompress;
