import { createAsyncSequence } from "@wortise/sequency";
import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import JsonElementStreamer from "../JsonElementStreamer";

class TransformerFunctionRepeat extends TransformerFunction {
  constructor() {
    super({
      argsSets: [[{ name: "count", type: ArgType.Number }]],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getJsonElement(null);
    if (value === null) {
      return null;
    }
    const count = await context.getInteger("count");
    if (count == null || count < 0) {
      return null;
    }

    return JsonElementStreamer.fromTransformedStream(
      context,
      createAsyncSequence({
        next: () => Promise.resolve({ value }),
      }).take(count),
    );
  }
}

export default TransformerFunctionRepeat;
