import xml2js from "xml2js";
import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { ArgType } from "./common/ArgType";

class TransformerFunctionXmlParse extends TransformerFunction {
  constructor() {
    super({
      argsSets: [
        [
          { name: "keep_strings", type: ArgType.Boolean, defaultValue: false },
          { name: "cdata_tag_name", type: ArgType.String, defaultValue: "$content" },
          { name: "convert_nil_to_null", type: ArgType.Boolean, defaultValue: false },
          { name: "force_list", type: ArgType.Array },
        ],
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const xml = await context.getString(null);
    if (xml == null) return null;
    try {
      const keepStrings = (await context.getBoolean("keep_strings")) ?? undefined;
      const cDataTagName = (await context.getString("cdata_tag_name")) ?? undefined;
      const convertNilAttributeToNull = (await context.getBoolean("convert_nil_to_null")) ?? undefined;
      const forceList = (await context.getJsonArray("force_list")) ?? undefined;

      const parser = new xml2js.Parser({
        charkey: cDataTagName,
        ignoreAttrs: convertNilAttributeToNull,
        explicitArray: Boolean(forceList?.length),
        attrValueProcessors: keepStrings
          ? undefined
          : [xml2js.processors.parseNumbers, xml2js.processors.parseBooleans],
      });
      return parser.parseStringPromise(xml);
    } catch (e: any) {
      console.warn(context.getAlias() + " failed", e);
      return null;
    }
  }
}

export default TransformerFunctionXmlParse;
