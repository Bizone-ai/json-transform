import BigNumber from "bignumber.js";
import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isNullOrUndefined } from "../JsonHelpers";
import { BigDecimal_ZERO, BigDecimal } from "./common/FunctionHelpers";

class TransformerFunctionStddev extends TransformerFunction {
  constructor() {
    super({
      argsSets: [
        [
          { name: "default", type: ArgType.Number, defaultValue: BigDecimal_ZERO },
          { name: "by", type: ArgType.Any },
          { name: "population", type: ArgType.Boolean, defaultValue: false },
        ],
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getJsonElementStreamer(null);
    if (value == null || value.knownAsEmpty()) {
      return null;
    }
    const hasBy = context.has("by");
    const by = await context.getJsonElement("by", false);
    const population = await context.getBoolean("population");
    const _default = (await context.getBigDecimal("default")) ?? BigDecimal(0);
    let size = 0;
    const values = await value
      .stream()
      .map(async t => {
        size++;
        const res = !hasBy ? t : await context.transformItem(by, t);
        return isNullOrUndefined(res) ? _default : BigDecimal(res);
      })
      .toList();
    const avg = values.reduce((a: BigNumber, c) => a.plus(c)).dividedBy(size);
    const sumOfSquares = values.map((a: BigNumber, c) => a.minus(avg).pow(2)).reduce((a: BigNumber, c) => a.plus(c));
    const variance = sumOfSquares.dividedBy(size - (population ? 0 : 1));
    return variance.sqrt();
  }
}

export default TransformerFunctionStddev;
