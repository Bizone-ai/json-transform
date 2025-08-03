import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import JsonElementStreamer from "../JsonElementStreamer";
import { createAsyncSequence } from "@wortise/sequency";
import { BigDecimal_ONE, RoundingModes } from "./common/FunctionHelpers";
import BigNumber from "bignumber.js";

class TransformerFunctionRange extends TransformerFunction {
  constructor() {
    super({
      allowsArgumentsAsInput: true,
      argsSets: [
        [
          { name: "start", type: ArgType.Number },
          { name: "end", type: ArgType.Number },
          { name: "step", type: ArgType.Number, defaultValue: BigDecimal_ONE },
        ],
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    let start: BigNumber | null;
    let end: BigNumber | null;
    let step: BigNumber;
    if (context.has("start")) {
      start = await context.getBigDecimal("start");
      end = await context.getBigDecimal("end");
      step = (await context.getBigDecimal("step")) ?? BigDecimal_ONE;
    } else {
      const arr = await context.getJsonArray(null);
      if (!arr) {
        return null;
      }
      start = arr[0];
      end = arr[1];
      step = arr[2] ?? BigDecimal_ONE;
    }
    // sanity check
    if (
      start === null ||
      end === null ||
      (end.lt(start) && step.isPositive()) ||
      (end.gt(start) && step.isNegative())
    ) {
      return null;
    }
    const size = end.minus(start).dividedToIntegerBy(step).plus(1).integerValue(RoundingModes.DOWN).toNumber();

    let value = start;
    return JsonElementStreamer.fromTransformedStream(
      context,
      createAsyncSequence({
        next: () => {
          const result = value;
          value = value.plus(step);
          return Promise.resolve({ value: result });
        },
      }).take(size),
    );
  }
}

export default TransformerFunctionRange;
