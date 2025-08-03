import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionMatch extends TransformerFunction {
  constructor() {
    super({
      argsSets: [
        [
          { name: "pattern", type: ArgType.String },
          { name: "group", type: ArgType.Number, defaultValue: 0 },
        ],
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    const patternString = await context.getString("pattern");
    if (patternString == null) {
      return null;
    }
    const matcher = new RegExp(patternString);
    const result = str.match(matcher);
    if (!result) return null; // not found
    const group = await context.getInteger("group");
    return result[group ?? 0];
  }
}

export default TransformerFunctionMatch;
