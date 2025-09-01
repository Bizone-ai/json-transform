import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isEqual } from "../JsonHelpers";

class TransformerFunctionIn extends TransformerFunction {
  constructor() {
    super({
      argsSet: [{ name: "value", type: ArgType.Any }],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getJsonElement("value");
    const collection = await context.getJsonElementStreamer(null);
    return collection !== null && collection.stream().any(other => isEqual(value, other));
  }
}

export default TransformerFunctionIn;
