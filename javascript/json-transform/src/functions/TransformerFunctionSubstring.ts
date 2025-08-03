import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionSubstring extends TransformerFunction {
  constructor() {
    super({
      argsSets: [
        [
          { name: "begin", type: ArgType.Number, defaultValue: 0 },
          { name: "end", type: ArgType.Number },
        ],
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    let beginIndex = await context.getInteger("begin");
    if (beginIndex == null) {
      return str;
    }

    const endIndex = await context.getInteger("end");
    return str.slice(beginIndex, endIndex ?? undefined);
  }
}

export default TransformerFunctionSubstring;
