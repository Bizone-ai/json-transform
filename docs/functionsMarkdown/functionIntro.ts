import {FunctionDescriptor} from "@bizone-ai/json-transform-utils";
import getSubfunctionOrFunction from "./getSubfunctionOrFunction";

export default function functionIntro(func: FunctionDescriptor, sub?: number) {
    const definition = getSubfunctionOrFunction(func, sub);
    return `
${definition.description}

${definition.notes ?? ""}
`;
}
