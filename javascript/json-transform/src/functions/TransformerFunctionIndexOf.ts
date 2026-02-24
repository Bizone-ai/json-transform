import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isEqual } from "../JsonHelpers";
import JsonElementStreamer from "../JsonElementStreamer";

class TransformerFunctionIndexOf extends TransformerFunction {
  constructor() {
    super({
      argsSet: [
        { name: "substring", type: ArgType.String },
        { name: "from", type: ArgType.Number },
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    const of = await context.getString("substring");
    const from = await context.getInteger("from");
    if (str == null || of == null) {
      return -1;
    }
    return from != null ? str.indexOf(of, from) : str.indexOf(of);
  }
}

export default TransformerFunctionIndexOf;
