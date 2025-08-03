import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionAt extends TransformerFunction {
  constructor() {
    super({
      argsSets: [[{ name: "index", type: ArgType.Number }]],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getJsonElementStreamer(null);
    if (value == null || value.knownAsEmpty()) {
      return null;
    }
    const index = await context.getInteger("index");
    if (index == null) {
      return null;
    }
    if (index == 0) {
      return value.stream().firstOrNull();
    }
    if (index > 0) {
      return value.stream(index).firstOrNull();
    }
    // negative
    const arr = await value.toJsonArray();
    return arr.at(index) ?? null;
  }
}

export default TransformerFunctionAt;
