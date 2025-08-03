# @bizone-ai/json-transform-utils

![](https://img.shields.io/npm/v/@bizone-ai/json-transform-utils.svg)

Core types and utilities for handling JSON transformers

# Installation

`npm install @bizone-ai/json-transform-utils`

# API

```typescript
{
  ContextVariablesSchemas: Record<string, TypeSchema>,
  getFunctionInlineSignature:  (name: string, func: FunctionDescriptor, requiredOnly?: boolean) => string,
  getFunctionObjectSignature:  (name: string, func: FunctionDescriptor) => string,
  functionsParser:  {
    get(name: string) => FunctionDescriptor,
    getNames() => Set<string>,
    resolveDocsUrl(funcName: string, functionDescriptor ? : FunctionDescriptor) => string,
    matchInline(
      data: any,
      callback?: (
        funcName: EmbeddedTransformerFunction,
        func: FunctionDescriptor,
        value: any,
        args: Record<string, any>,
      ) => any): FunctionDescriptor | null,
    matchObject(data: any, extractOutputTypeRecursively ? : boolean):ObjectFunctionMatchResult | undefined,
    matchAllObjectFunctionsInLine(line: string) => IterableIterator,
    matchAllInlineFunctionsInLine(line: string) => IterableIterator,
  },
  parseArgs: (func: FunctionDescriptor, args?: string) => {},
  type Argument,
  type ArgumentCondition,
  type ConditionalSubFunction,
  type FunctionDefinition,
  type FunctionDescriptor,
  EmbeddedTransformerFunction, // enum
  EmbeddedTransformerFunctions: EmbeddedTransformerFunction[],
  type JsonTransformExample,
  jsonpathJoin: (...args: (string | null | undefined)[]): string,
  JsonPathFunctionRegex: RegExp,
  parseTransformer: (
    definition: any,
    targetPath: string,
    previousPaths: string[],
    paths: string[] = [],
    typesMap? : Record<string, TypeSchema>,
    additionalContext? : Record<string, TypeSchema>,
  ) => void,
  ParseContext: {
    resolve(name: string) => TypeSchema,
  },
  type ParseMethod,
  type HandleFunctionMethod,
  definitions: Record<EmbeddedTransformerFunction, FunctionDefinition>,
  examples: Record<EmbeddedTransformerFunction, JsonTransformExample>,
  functions: Record<EmbeddedTransformerFunction, FunctionDescriptor>, 
  transformUtils: { //TransformUtils
    setAdditionalContext: (additionalContext: Set<string>) => void,
    getAdditionalContext: () => Set<string>,
    setSpecialKeys: (specialKeys: Set<string>) => void,
    getSpecialKeys: () => Set<string>,
    setContextVariablesSchemas: (contextVariablesSchemas: Record<string, TypeSchema>) => void,
    getContextVariablesSchemas: () => Record<string, TypeSchema>,
    setScopedContextVariablesSchema: (scopedContextVariablesSchema: Record<string, TypeSchema | null>) => void,
    getScopedContextVariablesSchema: () => Record<string, TypeSchema | null>,
    matchesAnyOfContextVariables: (variableName: string) => boolean,
    matchesAnyOfAdditionalContext: (variableName: string) => boolean,
    matchesAnyOfSpecialKeys: (variableName: string) => boolean,
    variableDetectionRegExpFactory: (flags = "g") => RegExp,
  }
}
;

```

# License
[MIT](./LICENSE)