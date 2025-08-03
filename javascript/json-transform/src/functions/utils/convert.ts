import { ArgumentType } from "../common/ArgumentType";
import { ArgType } from "../common/ArgType";
import { TransformerFunctions } from "../../TransformerFunctions";
import { isMap } from "../../JsonHelpers";

function parseByType(arg: ArgumentType, value: any) {
  if (typeof arg.const !== "undefined") {
    return arg.const;
  }
  if (
    typeof value === "undefined" ||
    typeof value === "object" ||
    // if the value is a path/function, return it as is
    (typeof value === "string" && (value.startsWith("$") || value.startsWith("#")))
  ) {
    return value;
  }
  switch (arg.type) {
    case ArgType.Boolean:
      return typeof value === "boolean" ? value : value === "true";
    case ArgType.Number:
      return typeof value === "object" ? value : Number(value);
    default:
      return value;
  }
}

const removeTrailingNulls = (arr: any[]) => {
  for (let i = arr.length - 1; i >= 0; i--) {
    if (arr[i] !== null && typeof arr[i] !== "undefined") {
      break;
    }
    arr.pop();
  }
  return arr;
};

const DEFAULT_PRIMARY_VALUE = null;

export const convertFunctionsToObjects = (definition: any): any => {
  if (!definition) {
    return definition;
  }
  if (typeof definition === "string") {
    const ctx = TransformerFunctions.tryGetInlineFunctionContext("", definition, undefined as any, undefined as any);
    if (!ctx) {
      return definition;
    }
    const func = ctx.getFunction();
    const funcArgs = ctx.getArgumentSet();
    let primaryValue: any;
    if (func.allowsArgumentsAsInput()) {
      const arr = [];
      for (const arg of funcArgs ?? []) {
        const argValue = ctx.getRaw(arg.name);
        arr.push(parseByType(arg, convertFunctionsToObjects(argValue)));
        // pop undefined
        for (let i = arr.length - 1; i >= 0; i--) {
          if (typeof arr[i] !== "undefined") {
            break;
          }
          arr.pop();
        }
      }
      primaryValue = arr;
    } else {
      const argValue = ctx.getRaw(null);
      if (!func.inputIsRaw()) {
        primaryValue = convertFunctionsToObjects(argValue) ?? DEFAULT_PRIMARY_VALUE;
      }
    }
    const result = { [ctx.getAlias()]: primaryValue };
    if (funcArgs && !func.allowsArgumentsAsInput()) {
      for (const arg of funcArgs) {
        const argValue = ctx.getRaw(arg.name);
        if (typeof argValue !== "undefined") {
          result[arg.name] = parseByType(arg, convertFunctionsToObjects(argValue));
        }
      }
    }
    return result;
  }
  if (Array.isArray(definition)) {
    const result = [];
    for (const item of definition) {
      result.push(convertFunctionsToObjects(item));
    }
    return result;
  }
  if (typeof definition === "object") {
    const result: Record<string, any> = {};
    const entries = Object.entries(definition);
    for (const [key, value] of entries) {
      result[key] = convertFunctionsToObjects(value);
    }
    return result;
  }
  return definition;
};

const escapeString = (input: string) => {
  return `'${input.replace(/\\/g, "\\\\").replace(/'/g, "\\'")}'`;
};

export const tryConvertFunctionsToInline = (definition: any): any => {
  const type = typeof definition;
  if (!definition || type !== "object") {
    return definition;
  }
  // must be an object / array
  const ctx = TransformerFunctions.tryGetObjectFunctionContext("", definition, undefined as any, undefined as any);
  const func = ctx?.getFunction();
  const funcKey = ctx?.getAlias();

  if (!ctx && (Array.isArray(definition) || isMap(definition))) {
    if (Array.isArray(definition)) {
      return definition.map(tryConvertFunctionsToInline);
    } else if (isMap(definition)) {
      // if it's a map, we need to convert it to an object
      const result: Record<string, any> = {};
      for (const [key, value] of Object.entries(definition)) {
        result[key] = tryConvertFunctionsToInline(value);
      }
      return result;
    }
  }
  const funcInput = isMap(definition) && funcKey ? definition[funcKey] : undefined;
  if (
    !funcKey ||
    !func ||
    !ctx ||
    // arrays and non-function objects can't be converted to string values
    (!func.allowsArgumentsAsInput() && Array.isArray(funcInput)) ||
    (func.inputIsRaw() && typeof funcInput !== "string") ||
    (isMap(funcInput) &&
      !TransformerFunctions.tryGetObjectFunctionContext("", funcInput, undefined as any, undefined as any))
  ) {
    return definition;
  }
  // match.value is either primitive or an object function
  let result = funcKey;
  const spec = structuredClone(definition); // clone the object to avoid modifying the original
  const argList = Object.keys(spec).filter(key => key !== funcKey);
  let possible = true;
  const argValues: string[] = [];
  if (argList.length && ctx.getArgumentSet()?.length) {
    const funcArgs = ctx.getArgumentSet();
    for (const arg of funcArgs ?? []) {
      let argValue = tryConvertFunctionsToInline(spec[arg.name]);
      if (argValue !== null && typeof argValue !== "undefined") {
        spec[arg.name] = argValue; // store it back for output
      }
      if (argValue && (Array.isArray(argValue) || typeof argValue === "object")) {
        possible = false;
      } else if (possible) {
        if (
          typeof argValue === "string" &&
          (argValue.startsWith("$") || argValue.startsWith("#") || argValue.includes(","))
        ) {
          argValue = escapeString(argValue);
        }
        argValues.push(argValue);
      }
    }
  } else if (Array.isArray(funcInput) && func.allowsArgumentsAsInput()) {
    for (let i = 0; i < funcInput.length; i++) {
      let argValue = tryConvertFunctionsToInline(funcInput[i]);
      const argName = ctx.getArgumentSet()?.[i]?.name;
      if (argName && argValue !== null && typeof argValue !== "undefined") {
        spec[funcKey][i] = argValue; // store it back for output
      }
      if (argValue && (Array.isArray(argValue) || typeof argValue === "object")) {
        possible = false;
      } else if (possible) {
        if (
          typeof argValue === "string" &&
          (argValue.startsWith("$") || argValue.startsWith("#") || argValue.includes(","))
        ) {
          argValue = escapeString(argValue);
        }
        argValues.push(argValue);
      }
    }
  }
  if (possible && argValues?.length) {
    removeTrailingNulls(argValues);
    if (argValues.length > 0) {
      result += "(" + argValues.map(x => x ?? "") + ")";
    }
  }
  if (funcKey && funcInput && !func.allowsArgumentsAsInput()) {
    const argValue = tryConvertFunctionsToInline(funcInput);
    spec[funcKey] = argValue; // store it back for output
    result += ":";
    if (argValue && (Array.isArray(argValue) || typeof argValue === "object")) {
      possible = false; // the primary argument is not primitive
    } else if (possible) {
      result += argValue;
    }
  }
  return possible ? result : spec;
};
