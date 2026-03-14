import XMLBuilder from "fast-xml-builder";
import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { ArgType } from "./common/ArgType";

class TransformerFunctionXml extends TransformerFunction {
  constructor() {
    super({
      argsSet: [
        { name: "root", type: ArgType.String },
        { name: "indent", type: ArgType.Boolean, defaultValue: false },
        { name: "attr_prefix", type: ArgType.String, defaultValue: "@" },
        { name: "cdata_prop_name", type: ArgType.String, defaultValue: "#text" },
      ],
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const obj = await context.getJsonElement(null);
    if (obj == null) return null;
    try {
      const rootName = (await context.getString("root")) ?? undefined;
      const indent = (await context.getBoolean("indent")) ?? undefined;
      const attrPrefix = (await context.getString("attr_prefix")) ?? undefined;
      const cDataPropName = (await context.getString("cdata_prop_name")) ?? undefined;
      const builder = new XMLBuilder({
        arrayNodeName: rootName,
        attributeNamePrefix: attrPrefix,
        ignoreAttributes: false,
        cdataPropName: cDataPropName,
        format: indent,
        suppressEmptyNode: true,
      });
      return builder.build(rootName ? [obj] : obj);
    } catch (e: any) {
      console.warn(context.getAlias() + " failed", e);
      return null;
    }
  }
}

export default TransformerFunctionXml;
