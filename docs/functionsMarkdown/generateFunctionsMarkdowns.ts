import fs from "node:fs";
import path from "node:path";
import {definitions, FunctionDefinition} from "@bizone-ai/json-transform-utils";
import functionIntro from "./functionIntro";
import functionUsage from "./functionUsage";
import functionReturns from "./functionReturns";
import functionArguments from "./functionArguments";
import functionExamples from "./functionExamples";
import htmlEscape from "./htmlEscape";

async function render(functionName: string, definition: FunctionDefinition, index?: number) {

    let subSections = "";
    const isSub = typeof index === "number";
    if (isSub && definition.subfunctions) {
        for (let i = 0; i < definition.subfunctions.length; i++) {
            const subDef = definition.subfunctions[i];
            subSections += `### ${htmlEscape(
                subDef.if.length === 1
                    ? (subDef.if[0].equals === "TRUE" ? "" : subDef.if[0].equals) + " " + subDef.if[0].argument
                    : `When ${subDef.if.map(c => `${c.argument} = ${c.equals}`).join(" and ")}`
)}${await render(functionName, definition, i)}
`;
        }
    }

    return (isSub ? "" : `---
title: ${JSON.stringify(`$$${functionName}${definition.aliases ? " / $$" + definition.aliases.join(" / $$") : ""}`)}
description: ${JSON.stringify(definition.description)}
---
`) + `
${functionIntro(definition, index)}

### Usage

${functionUsage(functionName, definition, index)}

### Returns

${functionReturns(definition, index)}

### Arguments

${functionArguments(definition, index)}

${subSections}${isSub ? "" : `

### Examples

${await functionExamples(functionName)}`}
`;
}

export async function generateFunctionsMarkdowns(configDir: string) {
  const functionsDir = path.join(configDir, "docs", "functions");

  for (const functionName in definitions) {
    try {
        const definition = definitions[functionName];
        const functionMarkdown = await render(functionName, definition);
        const filePath = path.join(functionsDir, `${functionName}.mdx`);
        fs.writeFileSync(filePath, functionMarkdown);
        console.log(`Generated function markdown for: ${functionName} (at: ${filePath})`);
    } catch (e: any) {
        console.error(`Failed generating markdown for: ${functionName}`)
        console.error(e);
    }
  }
}
