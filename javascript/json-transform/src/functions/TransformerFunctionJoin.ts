import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { getAsString, isNullOrUndefined } from "../JsonHelpers";

class TransformerFunctionJoin extends TransformerFunction {
  constructor() {
    super({
      argsSets: [
        [
          { name: "delimiter", type: ArgType.String, defaultValue: "", aliases: ["$$delimiter"] },
          { name: "prefix", type: ArgType.String, defaultValue: "" },
          { name: "suffix", type: ArgType.String, defaultValue: "" },
          { name: "keep_nulls", type: ArgType.Boolean, defaultValue: false },
        ],
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null) return null;

    let delimiter = await context.getString("$$delimiter"); // backwards compat.
    if (delimiter == null) {
      delimiter = await context.getString("delimiter");
    }
    const prefix = (await context.getString("prefix")) ?? undefined;
    const suffix = (await context.getString("suffix")) ?? undefined;
    let stream = streamer.stream().map(getAsString);
    if (!(await context.getBoolean("keep_nulls"))) {
      stream = stream.filter(el => !isNullOrUndefined(el));
    }
    return stream.joinToString({
      prefix: prefix,
      postfix: suffix,
      separator: delimiter ?? "",
    });
  }
}

export default TransformerFunctionJoin;
