import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionWrap extends TransformerFunction {
  constructor() {
    super({
      argsSets: [
        [
          { name: "prefix", type: ArgType.String, defaultValue: "" },
          { name: "suffix", type: ArgType.String, defaultValue: "" },
        ],
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const res = await context.getString(null);
    if (res == null) return null;
    return ((await context.getString("prefix")) ?? "") + res + ((await context.getString("suffix")) ?? "");
  }
}

export default TransformerFunctionWrap;
