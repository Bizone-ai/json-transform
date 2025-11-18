import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { isNullOrUndefined } from "../JsonHelpers";
import BigNumber from "bignumber.js";

const ACCEPTED_TYPES = new Set(["string", "boolean", "number"]);

class TransformerFunctionTypeOf extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getJsonElement(null);
    if (isNullOrUndefined(value)) {
      return "null";
    }
    const type = typeof value;
    if (ACCEPTED_TYPES.has(type)) {
      return type;
    }
    if (BigNumber.isBigNumber(value) || type === "bigint") {
      return "number";
    }
    if (Array.isArray(value)) {
      return "array";
    }
    return "object";
  }
}

export default TransformerFunctionTypeOf;
