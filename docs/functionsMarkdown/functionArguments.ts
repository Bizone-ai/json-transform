import { Argument, FunctionDescriptor } from "@bizone-ai/json-transform-utils";
import getSubfunctionOrFunction from "./getSubfunctionOrFunction";
import htmlEscape from "./htmlEscape";

const getTypeHTML = (arg: Argument) => {
  if (arg.type === "transformer") {
    return `<span>Transformer(${arg.transformerArguments
        ?.map(v => `<code>${htmlEscape(v.name)}</code>`).join(",")
    })</span>`;
  }
  return `<code>${htmlEscape(arg.type)}</code>`;
};

const getValuesHTML = (arg: Argument) => {
  if (arg.type === "const") {
    return `<code>${htmlEscape(`${arg.const}`)}</code>`;
  }
  if (arg.type === "boolean") {
    return `<code>false</code>/<code>true</code>`;
  }
  if (arg.type === "enum" && arg.enum) {
    return arg.enum.map((v, i, a) => (
            `<code>${htmlEscape(v)}</code>${i < a.length - 1 ? "/" + (i % 5 === 0 ? " " : "") : ""}`
        )).join("");
  }
  return "";
};

const getRequiredOrDefaultHTML = (arg: Argument) => {
  if (arg.required) return `<strong>Yes</strong>`;
  if (typeof arg.default !== "undefined") {
    return `<code>${htmlEscape(
        arg.type === "string" || arg.type === "array" || arg.type.endsWith("[]")
          ? JSON.stringify(arg.default)
          : `${arg.default}`)
    }</code>`;
  }
  return null;
};

const argumentRowHTML = (arg?: Argument) => `
    <tr>
      <td>${arg?.name ? `<code>${htmlEscape(arg.name)}</code>` : "Primary"}</td>
      <td>${arg ? getTypeHTML(arg) : ""}</td>
      <td>${arg ? getValuesHTML(arg) : ""}</td>
      <td>${arg ? getRequiredOrDefaultHTML(arg) : ""}</td>
      <td>${htmlEscape(arg?.description)}${arg?.type === "transformer" ? `<ul>${arg.transformerArguments?.map(v => `<li><code>${htmlEscape(v.name)}</code> - ${htmlEscape(v.description)}</li>`).join("")}</ul>` : ""}</td>
    </tr>`;

const generateTable = (inArg: Omit<Argument, "name">, args?: Argument[]) => `
<table>
  <thead>
    <tr>
      <th>Argument</th>
      <th>Type</th>
      <th>Values</th>
      <th>Required / Default&nbsp;Value</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>${argumentRowHTML(inArg as Argument)}${args?.map(argumentRowHTML).join("") ?? ""}
  </tbody>
</table>
`;

export default function functionArguments(func: FunctionDescriptor, sub?: number) {
  const definition: FunctionDescriptor = getSubfunctionOrFunction(func, sub);

  const inArg = definition.inputSchema;

  const tables: string[] = [generateTable(inArg, definition.arguments)];

  if (definition.argumentsNotes) {
    tables.push(definition.argumentsNotes);
  }

  return tables.join("\n");
}
