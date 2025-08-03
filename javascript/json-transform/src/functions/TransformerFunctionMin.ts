import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { createComparator, isNullOrUndefined, isNumberType } from "../JsonHelpers";
import CompareBy from "./common/CompareBy";

class TransformerFunctionMin extends TransformerFunction {
  constructor() {
    super({
      argsSets: [
        [
          { name: "default", type: ArgType.Any },
          { name: "type", type: ArgType.String, defaultValue: "AUTO" },
          { name: "by", type: ArgType.Any, defaultValue: "##current" },
        ],
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null || streamer.knownAsEmpty()) return null;
    const hasBy = context.has("by");
    const type = await context.getEnum("type");
    const def = await (type === "NUMBER" ? context.getBigDecimal("default") : context.getJsonElement("default"));

    if (!hasBy) {
      const comparator = createComparator(type);
      return streamer
        .stream()
        .map(async t => {
          return isNullOrUndefined(t) ? def : t;
        })
        .minWith(comparator.compare);
    } else {
      const by = await context.getJsonElement("by", false);
      const comparator = CompareBy.createByComparator(0, type);
      return streamer
        .stream()
        .map(async item => {
          const cb = new CompareBy(item);
          const t = await context.transformItem(by, item);
          cb.by = [isNullOrUndefined(t) ? def : t];
          return cb;
        })
        .minWith(comparator.compare)
        .then(x => x?.value);
    }
  }
}

export default TransformerFunctionMin;
