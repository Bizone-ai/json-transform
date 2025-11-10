import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { unwrapNumber } from "../JsonHelpers";

class TransformerFunctionLong extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    return unwrapNumber(await context.getLong(null));
  }
}

export default TransformerFunctionLong;
