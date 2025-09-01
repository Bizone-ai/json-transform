import path from "node:path";
import fs from "node:fs";
import { definitions, examples } from "@bizone-ai/json-transform-utils";

export default function () {
  const language: any = {};
  // add all markdown files
  const docsDir = path.join(__dirname, "..", "..", "docs");
  fs.readdirSync(docsDir)
    .filter(f => f.endsWith(".md"))
    .forEach(filename => {
      let doc = fs.readFileSync(path.join(docsDir, filename), "utf8");
      doc = doc.replace(/^---.*---\n/s, ""); // remove front matter
      const docName = doc.substring(2, doc.indexOf("\n"));
      language[docName] = doc;
    });
  // add functions and examples
  language.functions = structuredClone(definitions);
  const keys = Object.keys(language.functions);
  for (const key of keys) {
    try {
      if (language.functions[key].deprecated) {
        delete language.functions[key];
        continue;
      }
      language.functions[key].examples = examples[key];
    } catch (e: any) {
      console.error(`Error with func ${key}`, e);
      throw e;
    }
  }
  return language;
}
