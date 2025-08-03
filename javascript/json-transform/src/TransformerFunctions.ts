import TransformerFunction from "./functions/common/TransformerFunction";
import { ParameterResolver } from "./ParameterResolver";
import { JsonTransformerFunction } from "./JsonTransformerFunction";
import { isMap } from "./JsonHelpers";
import embeddedFunctions from "./functions";
import InlineFunctionTokenizer from "./functions/common/InlineFunctionTokenizer";
import FunctionContext from "./functions/common/FunctionContext";

/**
 * The purpose of this class is to differentiate between null and null result
 */
export class FunctionMatchResult {
  private readonly result: any;
  private readonly resultPath: string;

  constructor(result: any, resultPath: string) {
    this.result = result;
    this.resultPath = resultPath;
  }

  getResult() {
    return this.result;
  }
  getResultPath() {
    return this.resultPath;
  }
}

export interface TransformerFunctionsAdapter {
  matchObject(
    path: string,
    definition: any,
    resolver: ParameterResolver,
    transformer: JsonTransformerFunction,
  ): Promise<FunctionMatchResult | null>;
  matchInline(
    path: string,
    value: string,
    resolver: ParameterResolver,
    transformer: JsonTransformerFunction,
  ): Promise<FunctionMatchResult | null>;
}

export class TransformerFunctions implements TransformerFunctionsAdapter {
  public static readonly FUNCTION_KEY_PREFIX = "$$";
  public static readonly QUOTE_APOS = "'";
  public static readonly ESCAPE_DOLLAR = "\\$";
  public static readonly ESCAPE_HASH = "\\#";

  private static readonly functions: Record<string, TransformerFunction> = {};

  static {
    TransformerFunctions.registerFunctions(embeddedFunctions);
  }

  public static registerFunctions(moreFunctions: Record<string, TransformerFunction>) {
    const additions = Object.entries(moreFunctions).filter(([key, value]) => {
      if (Object.prototype.hasOwnProperty.call(TransformerFunctions.functions, key)) {
        console.debug(`Skipping registering function $$${key} (already exists)`);
        return false;
      }
      return true;
    });
    if (additions.length) {
      console.debug(`Registering functions: $$${additions.map(([key, _]) => key).join(", $$")}`);
      additions.forEach(add => {
        TransformerFunctions.functions[add[0]] = add[1];
      });
    }
  }

  public static tryGetObjectFunctionContext(
    path: string,
    definition: any,
    resolver: ParameterResolver,
    transformer: JsonTransformerFunction,
  ) {
    if (!isMap(definition)) {
      return null;
    }
    // look for an object function
    // (precedence is all internal functions sorted alphabetically first, then client added ones second, by registration order)
    for (const key in TransformerFunctions.functions) {
      const funcKey = TransformerFunctions.FUNCTION_KEY_PREFIX + key;
      if (Object.prototype.hasOwnProperty.call(definition, funcKey)) {
        const func = TransformerFunctions.functions[key];
        return FunctionContext.createObject(path, definition, funcKey, func, resolver, transformer);
      }
    }
    return null;
  }

  async matchObject(path: string, definition: any, resolver: ParameterResolver, transformer: JsonTransformerFunction) {
    const context = TransformerFunctions.tryGetObjectFunctionContext(path, definition, resolver, transformer);
    if (!context) return null;

    const resolvedPath = context.getPathFor(null);
    try {
      const result = await context.apply();
      return new FunctionMatchResult(result, resolvedPath);
    } catch (ex) {
      console.warn(`Failed running object function (at ${resolvedPath})`, ex);
      return new FunctionMatchResult(null, resolvedPath);
    }
  }

  public static tryGetInlineFunctionContext(
    path: string,
    value: string,
    resolver: ParameterResolver,
    transformer: JsonTransformerFunction,
  ) {
    const match = InlineFunctionTokenizer.tokenize(value);
    if (match) {
      const func = TransformerFunctions.functions[match.name];
      if (func) {
        const args = match.args?.map(x => x.value) ?? [];
        // remove trailing nulls
        for (let i = args.length - 1; i >= 0; i--) {
          if (args[i] !== null) {
            break;
          }
          args.pop();
        }
        const functionKey = TransformerFunctions.FUNCTION_KEY_PREFIX + match.name;
        return FunctionContext.createInline(
          path + "/" + functionKey,
          match.input?.value ?? null,
          args,
          functionKey,
          func,
          resolver,
          transformer,
        );
      }
    }
    return null;
  }

  async matchInline(path: string, value: string, resolver: ParameterResolver, transformer: JsonTransformerFunction) {
    if (value == null) return null;
    const context = TransformerFunctions.tryGetInlineFunctionContext(path, value, resolver, transformer);
    if (context == null) {
      return null;
    }
    // at this point we detected an inline function, we must return a match result
    const resolvedPath = context.getPathFor(null);
    try {
      const result = await context.apply();
      return new FunctionMatchResult(result, resolvedPath);
    } catch (ex) {
      console.warn(`Failed running inline function  (at ${resolvedPath})`, ex);
    }
    return new FunctionMatchResult(null, resolvedPath);
  }
}

export default new TransformerFunctions();
