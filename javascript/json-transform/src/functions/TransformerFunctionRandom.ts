import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { BigDecimal, BigDecimal_ONE, BigDecimal_ZERO, MAX_SCALE, MAX_SCALE_ROUNDING } from "./common/FunctionHelpers";
import BigNumber from "bignumber.js";
import { unwrapNumber } from "../JsonHelpers";

class TransformerFunctionRandom extends TransformerFunction {
  constructor() {
    super({
      allowsArgumentsAsInput: true,
      argsSet: [
        { name: "min", type: ArgType.Number, defaultValue: 0 },
        { name: "max", type: ArgType.Number, defaultValue: 1 },
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    let min: BigNumber | null;
    let max: BigNumber | null;
    if (context.has("min")) {
      min = (await context.getBigDecimal("min")) ?? BigDecimal_ZERO;
      max = (await context.getBigDecimal("max")) ?? BigDecimal_ONE;
    } else {
      const arr = await context.getJsonArray(null);
      if (!arr) {
        return null;
      }
      min = arr[0] ?? BigDecimal_ZERO;
      max = arr[1] ?? BigDecimal_ONE;
    }
    if (!BigDecimal.isBigNumber(min) && min !== null) min = BigDecimal(min);
    if (!BigDecimal.isBigNumber(max) && max !== null) max = BigDecimal(max);
    // sanity check
    if (min === null || max === null || max.lt(min)) {
      return null;
    }
    const rand = new BigDecimal(Math.random());
    const diff = max.minus(min);
    let result = min.plus(diff.multipliedBy(rand));
    // cap scale at max
    if ((result.decimalPlaces() ?? 0) > MAX_SCALE) {
      result = result.decimalPlaces(MAX_SCALE, MAX_SCALE_ROUNDING);
    }
    return unwrapNumber(result);
  }
}

export default TransformerFunctionRandom;
