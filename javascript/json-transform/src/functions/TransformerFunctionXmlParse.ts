import { X2jOptions, XMLParser } from "fast-xml-parser";
import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { ArgType } from "./common/ArgType";

class TransformerFunctionXmlParse extends TransformerFunction {
  constructor() {
    super({
      argsSet: [
        { name: "keep_strings", type: ArgType.Boolean, defaultValue: false },
        { name: "attr_prefix", type: ArgType.String, defaultValue: "@" },
        { name: "cdata_prop_name", type: ArgType.String, defaultValue: "#text" },
        { name: "array_tags", type: ArgType.Array },
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const xml = await context.getString(null);
    if (xml == null) return null;
    try {
      const keepStrings = (await context.getBoolean("keep_strings")) ?? undefined;
      const attrPrefix = (await context.getString("attr_prefix")) ?? undefined;
      const cDataTagName = (await context.getString("cdata_prop_name")) ?? undefined;
      const arrayTags = (await context.getJsonArray("array_tags")) ?? undefined;

      const options = {
        attributeNamePrefix: attrPrefix,
        allowBooleanAttributes: true,
        ignoreAttributes: false,
        textNodeName: cDataTagName,
        parseTagValue: !keepStrings,
        parseAttributeValue: !keepStrings,
        maxNestedTags: 256,
      } as X2jOptions;
      if (arrayTags?.length) {
        const tagsSet = new Set(arrayTags);
        options.isArray = x => tagsSet.has(x);
      }
      // if (!keepStrings) {
      //   options.attributeValueProcessor = (_attrName, attrValue) => {
      //     console.log("attrValue", attrValue);
      //     if (attrValue === "true") return true;
      //     if (attrValue === "false") return false;
      //     return attrValue;
      //   };
      // }

      return new XMLParser(options).parse(xml);
    } catch (e: any) {
      console.warn(context.getAlias() + " failed", e);
      return null;
    }
  }
}

export default TransformerFunctionXmlParse;
