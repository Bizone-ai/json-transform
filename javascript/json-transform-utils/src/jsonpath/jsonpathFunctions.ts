import { TypeSchema } from "@bizone-ai/json-schema-utils";

const JsonPathFunctionsResultSchemas: Record<string, TypeSchema> = {
  // fallback type is string (no format)
  min: { type: "number" },
  max: { type: "number" },
  avg: { type: "number" },
  stddev: { type: "number" },
  length: { type: "integer" },
  sum: { type: "number" },
  keys: { type: "array", items: { type: "string" } },
  concat: { type: "string" },
  append: { type: "array" },
};

export const JsonPathFunctionRegex = new RegExp(`\\.(${Object.keys(JsonPathFunctionsResultSchemas).join("|")})\\(\\)?`);
