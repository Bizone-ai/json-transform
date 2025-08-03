import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isTruthy } from "../JsonHelpers";

class TransformerFunctionBoolean extends TransformerFunction {
  constructor() {
    super({
      argsSets: [[{ name: "style", type: ArgType.String, defaultValue: "JAVA" }]],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const jsStyle = (await context.getEnum("style")) === "JS";
    return isTruthy(await context.getUnwrapped(null), jsStyle);
  }
}

export default TransformerFunctionBoolean;
