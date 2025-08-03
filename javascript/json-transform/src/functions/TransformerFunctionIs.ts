import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isEqual, isNumberType } from "../JsonHelpers";

class TransformerFunctionIs extends TransformerFunction {
  constructor() {
    super({
      argsSets: [
        [
          { name: "op", type: ArgType.String, exists: true },
          { name: "that", type: ArgType.Any },
        ],
        [
          { name: "in", type: ArgType.Array },
          { name: "nin", type: ArgType.Array },
          { name: "eq", type: ArgType.Any },
          { name: "neq", type: ArgType.Any },
          { name: "gt", type: ArgType.Any },
          { name: "gte", type: ArgType.Any },
          { name: "lt", type: ArgType.Any },
          { name: "lte", type: ArgType.Any },
        ],
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getJsonElement(null);
    if (context.has("op")) {
      const op = await context.getEnum("op");
      let that: any = null;
      // if operator is not in/nin then prepare the "that" argument which is a JsonElement
      if (op !== "IN" && op !== "NIN") {
        that = await (isNumberType(value) ? context.getBigDecimal("that") : context.getJsonElement("that"));
      }
      switch (op) {
        case "IN": {
          const _in = await context.getJsonElementStreamer("that");
          return _in != null && (await _in.stream().any(item => isEqual(item, value)));
        }
        case "NIN": {
          var nin = await context.getJsonElementStreamer("that");
          return nin != null && (await nin.stream().none(item => isEqual(item, value)));
        }
        case "GT":
        case ">": {
          const comparison = context.compareTo(value, that);
          return comparison != null && comparison > 0;
        }
        case "GTE":
        case ">=": {
          const comparison = context.compareTo(value, that);
          return comparison != null && comparison >= 0;
        }
        case "LT":
        case "<": {
          const comparison = context.compareTo(value, that);
          return comparison != null && comparison < 0;
        }
        case "LTE":
        case "<=": {
          const comparison = context.compareTo(value, that);
          return comparison != null && comparison <= 0;
        }
        case "EQ":
        case "=":
        case "==": {
          return isEqual(value, that);
        }
        case "NEQ":
        case "!=":
        case "<>": {
          return !isEqual(value, that);
        }
        default: {
          return false;
        }
      }
    }
    let result = true;
    if (context.has("in")) {
      const _in = await context.getJsonElementStreamer("in");
      result = _in != null && (await _in.stream().any(item => isEqual(item, value)));
    }
    if (result && context.has("nin")) {
      const nin = await context.getJsonElementStreamer("nin");
      result = nin != null && (await nin.stream().none(item => isEqual(item, value)));
    }
    if (result && context.has("gt")) {
      const gt = await context.getJsonElement("gt");
      const comparison = context.compareTo(value, gt);
      result = comparison != null && comparison > 0;
    }
    if (result && context.has("gte")) {
      const gte = await context.getJsonElement("gte");
      const comparison = context.compareTo(value, gte);
      result = comparison != null && comparison >= 0;
    }
    if (result && context.has("lt")) {
      const lt = await context.getJsonElement("lt");
      const comparison = context.compareTo(value, lt);
      result = comparison != null && comparison < 0;
    }
    if (result && context.has("lte")) {
      const lte = await context.getJsonElement("lte");
      const comparison = context.compareTo(value, lte);
      result = comparison != null && comparison <= 0;
    }
    if (result && context.has("eq")) {
      const eq = await context.getJsonElement("eq");
      result = isEqual(value, eq);
    }
    if (result && context.has("neq")) {
      const neq = await context.getJsonElement("neq");
      result = !isEqual(value, neq);
    }
    return result;
  }
}

export default TransformerFunctionIs;
