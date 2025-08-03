import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { getAsString } from "../JsonHelpers";
import { ArgType } from "./common/ArgType";
import { JSONBig } from "./common/FunctionHelpers";

class TransformerFunctionString extends TransformerFunction {
  constructor() {
    super({
      argsSets: [[{ name: "json", type: ArgType.Boolean, defaultValue: false }]],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getUnwrapped(null);
    if (await context.getBoolean("json")) {
      return JSONBig.stringify(value);
    }
    return getAsString(value);
  }
}

export default TransformerFunctionString;
