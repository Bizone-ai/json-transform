import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { ArgType } from "./common/ArgType";

class TransformerFunctionTransform extends TransformerFunction {
  constructor() {
    super({
      argsSets: [[{ name: "to", type: ArgType.Any }]],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const input = await context.getJsonElement(null);
    const to = await context.getJsonElement("to", false);
    return context.transformItem(to, input);
  }
}

export default TransformerFunctionTransform;
