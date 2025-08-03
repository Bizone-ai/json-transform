import { FunctionDescriptor } from "@bizone-ai/json-transform-utils";
import { TypeSchema } from "@bizone-ai/json-schema-utils";
import getSubfunctionOrFunction from "./getSubfunctionOrFunction";

const renderType = (schema: TypeSchema) => {
  if (schema.type === "array" && schema.items) {
    if (Array.isArray(schema.items)) {
      return "[" + schema.items.map(x => renderType(x)) + "]";
    } else {
      return renderType(schema.items) + "[]";
    }
  }
  if (schema.type === "string" && schema.format) {
    return "string (" + schema.format + ")";
  }
  return schema.type ?? "any";
};

export default function functionReturns(func: FunctionDescriptor, sub?: number) {
  const definition: FunctionDescriptor = getSubfunctionOrFunction(func, sub);

  let returns = "Any";
  const schema: TypeSchema | undefined = definition.outputSchemaTemplate ?? definition.outputSchema;
  if (schema) {
    if (schema.type === "object" && schema.description) {
      returns = `_${schema.description}_`;
    } else {
      returns = `\`${renderType(schema)}\``;
      if (schema.$comment) {
        returns += ` (\`${schema.$comment}\`)`;
      }
      if (schema.description) {
        returns += ` - ${schema.description}`;
      }
    }
  }

  return returns;
}
