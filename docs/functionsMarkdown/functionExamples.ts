import { examples, JsonTransformExample } from "@bizone-ai/json-transform-utils";
import { shareLink } from "./utils/shareLink";

const isDifferentLang = (format: string) => {
  return format === "xml" || format === "csv" || format === "yaml" || format === "big-decimal";
};

export default async function functionExamples(name: string) {
  const fExamples: JsonTransformExample[] = examples[name];

  const examplesToShow = fExamples.filter(x => typeof x.expect.equal !== "undefined" || x.expect.isNull);

  for (let i = 0; i < examplesToShow.length; i++) {
    (examplesToShow[i] as any).link = await shareLink(examplesToShow[i].given.input, examplesToShow[i].given.definition);
  }

  const markdown = `

**Input**

**Definition**

**Output**

${examplesToShow.map(
    x => `
\`\`\`${isDifferentLang(x.given.inputFormat) ? x.given.inputFormat : "json"}
${isDifferentLang(x.given.inputFormat) ? x.given.input : JSON.stringify(x.given.input, null, 2)}
\`\`\`
\`\`\`transformers 
${JSON.stringify(x.given.definition, null, 2)}
\`\`\`
\`\`\`${isDifferentLang(x.expect.format) ? x.expect.format : "json"}
${
  isDifferentLang(x.expect.format) ? x.expect.equal : x.expect.isNull ? "null" : JSON.stringify(x.expect.equal, null, 2)
}
\`\`\`

<div class="action" />

<div class="action ${!(x as any).link ? "action--hidden" : ""}">
    <a title="Play this example on the transformers playground" href="${
      (x as any).link
    }" class="button button--primary transform-button shadow--lw">Test</a>
</div>

<div class="action" />
`)
  .join("\n")}
`;

  return `
    <div class="examples_grid">
      ${markdown}
    </div>
  `;
}
