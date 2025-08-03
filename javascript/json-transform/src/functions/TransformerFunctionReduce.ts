import { AsyncSequence, asyncSequenceOf } from "@wortise/sequency";
import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { ArgType } from "./common/ArgType";

class TransformerFunctionReduce extends TransformerFunction {
  constructor() {
    super({
      argsSets: [
        [
          { name: "to", type: ArgType.Any },
          { name: "identity", type: ArgType.Any },
        ],
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null) return null;
    const identity = await context.getJsonElement("identity");
    const to = await context.getJsonElement("to", false);

    return (
      asyncSequenceOf(asyncSequenceOf(identity), streamer.stream()).flatten() as AsyncSequence<any>
    ).reduceIndexed(async (i, acc, x) => {
      return context.transformItem(to, x, i - 1, "##accumulator", acc);
    });
  }
}

export default TransformerFunctionReduce;
