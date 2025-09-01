import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isEqual, isNumberType } from "../JsonHelpers";

class TransformerFunctionNeq extends TransformerFunction {
  constructor() {
    super({
      argsSet: [
        { name: "value", type: ArgType.Any },
        { name: "strict", type: ArgType.Boolean, defaultValue: false },
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getJsonElement("value");
    const strict = context.has("strict") && (await context.getBoolean("strict"));
    const compareValue = await (!strict && isNumberType(value)
      ? context.getBigDecimal(null)
      : context.getJsonElement(null));
    return !isEqual(value, compareValue);
  }
}

export default TransformerFunctionNeq;
