import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { getAsString, isMap } from "../JsonHelpers";
import { ArgType } from "./common/ArgType";

class TransformerFunctionSwitch extends TransformerFunction {
  constructor() {
    super({
      argsSets: [
        [
          { name: "cases", type: ArgType.Object },
          { name: "default", type: ArgType.Any },
        ],
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getString(null);
    const caseMap = await context.getJsonElement("cases");
    if (!isMap(caseMap)) {
      console.warn("{}.cases was not specified with an object as case map", context.getAlias());
      return null;
    }
    return Object.prototype.hasOwnProperty.call(caseMap, value ?? "null")
      ? caseMap[value ?? "null"]
      : context.getJsonElement("default");
  }
}

export default TransformerFunctionSwitch;
