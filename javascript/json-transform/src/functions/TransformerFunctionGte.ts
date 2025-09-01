import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isEqual, isNumberType } from "../JsonHelpers";

class TransformerFunctionGte extends TransformerFunction {
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
    var comparison = context.compareTo(value, compareValue);
    return comparison != null && comparison >= 0;
  }
}

export default TransformerFunctionGte;
