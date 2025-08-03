import TransformerFunction from "./TransformerFunction";
import { ParameterResolver } from "../../ParameterResolver";
import { JsonTransformerFunction } from "../../JsonTransformerFunction";
import {
  compareTo,
  isNullOrUndefined,
  getAsString,
  getDocumentContext,
  toObjectFieldPath,
  isMap,
} from "../../JsonHelpers";
import { BigDecimal } from "./FunctionHelpers";
import JsonElementStreamer from "../../JsonElementStreamer";
import BigNumber from "bignumber.js";
import DocumentContext from "../../DocumentContext";
import { ArgumentsSet } from "./FunctionDescription";

class FunctionContext {
  protected static readonly CONTEXT_KEY = "context";
  protected static readonly DOUBLE_HASH_CURRENT = "##current";
  protected static readonly DOUBLE_HASH_INDEX = "##index";
  protected static readonly DOLLAR = "$";

  protected readonly path: string;
  protected readonly alias: string;
  protected readonly function: TransformerFunction;
  protected readonly extractor: JsonTransformerFunction;
  private readonly argsSet: ArgumentsSet | null;
  private readonly isInline: boolean;
  private readonly args: Record<string, string | null>;
  private readonly defaultValues: Record<string, any>;
  private resolver: ParameterResolver | null;
  private readonly resolverFactory: (() => Promise<ParameterResolver>) | null;

  private constructor(
    isInline: boolean,
    path: string,
    alias: string,
    func: TransformerFunction,
    argsSet: ArgumentsSet | null,
    args: Record<string, string | null>,
    resolver: ParameterResolver | (() => Promise<ParameterResolver>),
    extractor: JsonTransformerFunction,
  ) {
    this.isInline = isInline;
    this.path = path;
    this.alias = alias;
    this.function = func;
    this.argsSet = argsSet;
    this.args = args;
    this.defaultValues =
      argsSet?.reduce(
        (a, c) => {
          a[c.name] = c.defaultValue ?? null;
          return a;
        },
        {} as Record<string, any>,
      ) ?? {};
    this.extractor = extractor;
    if (typeof resolver === "function") {
      this.resolverFactory = resolver;
      this.resolver = null;
    } else {
      this.resolverFactory = null;
      this.resolver = resolver;
    }
  }

  public static createInline(
    path: string,
    input: string | null,
    args: (string | null)[],
    functionKey: string,
    func: any,
    resolver: any,
    extractor: any,
  ) {
    const argsSet = func.parseArguments(args);
    const a = {
      [functionKey]: input,
    };
    const argsX = !argsSet
      ? a
      : args?.reduce((a, c, i) => {
          a[argsSet[i].name] = c;
          return a;
        }, a) ?? a;
    return new FunctionContext(true, path, functionKey, func, argsSet, argsX, resolver, extractor);
  }

  public static createObject(
    path: string,
    definition: any,
    functionKey: string,
    func: any,
    resolver: any,
    extractor: any,
  ) {
    const argsSet = func.parseArguments(definition);
    let objResolver: ParameterResolver | (() => Promise<ParameterResolver>) = resolver;
    if (definition?.[FunctionContext.CONTEXT_KEY]) {
      const contextElement = definition[FunctionContext.CONTEXT_KEY];
      if (isMap(contextElement)) {
        objResolver = () => FunctionContext.recalcResolver(path, contextElement, resolver, extractor);
      }
    }
    return new FunctionContext(false, path, functionKey, func, argsSet, definition, objResolver, extractor);
  }

  protected static async recalcResolver(
    path: string,
    contextElement: any,
    resolver: ParameterResolver,
    extractor: JsonTransformerFunction,
  ): Promise<ParameterResolver> {
    const addCtx: Record<string, DocumentContext> = {};
    for (const key in contextElement) {
      addCtx[key] = getDocumentContext(
        await extractor.transform(path + toObjectFieldPath(key), contextElement[key], resolver, false),
      );
    }
    return {
      get: name => {
        for (const key in addCtx) {
          if (FunctionContext.pathOfVar(key, name)) {
            return addCtx[key].read(FunctionContext.DOLLAR + name.substring(key.length));
          }
        }
        return resolver.get(name);
      },
    };
  }

  /**
   * Check if the specified path is of the specified variable
   * @param _var variable name
   * @param path  path to check
   * @return true if the path is of the variable
   */
  public static pathOfVar(_var: string, path: string) {
    return path === _var || path.startsWith(_var + ".") || path.startsWith(_var + "[");
  }

  public getAlias() {
    return this.alias;
  }

  public getPath() {
    return this.path;
  }

  public getFunction() {
    return this.function;
  }

  public getArgumentSet() {
    return this.argsSet;
  }

  public getDefaultValue(name: string) {
    return this.defaultValues[name] ?? null;
  }

  public apply() {
    return this.function.apply(this);
  }

  public async getResolver() {
    if (this.resolver) {
      return this.resolver;
    } else if (this.resolverFactory) {
      this.resolver = await this.resolverFactory();
      return this.resolver;
    }
    // no resolver, return empty resolver
    return {
      get: () => "",
    };
  }

  public has(name: string): boolean {
    return Object.prototype.hasOwnProperty.call(this.args, name);
  }

  public getRaw(name: string | null) {
    if (name != null && !Object.prototype.hasOwnProperty.call(this.args, name)) {
      return undefined;
    }
    return this.args[name == null ? this.alias : name];
  }

  public async get(name: string | null, transform: boolean = true): Promise<any> {
    if (name != null && !Object.prototype.hasOwnProperty.call(this.args, name)) {
      return this.getDefaultValue(name);
    }
    const argValue = this.args[name == null ? this.alias : name];
    const resolver = await this.getResolver();
    return !transform ? argValue : await this.extractor.transform(this.getPathFor(name), argValue, resolver, true);
  }

  public getPathFor(key: number | string | null) {
    if (this.isInline) {
      return this.path + (key == null ? "" : `(${key})`);
    } else {
      return this.path + (typeof key === "number" ? `[${key}]` : toObjectFieldPath(!key ? this.getAlias() : key));
    }
  }

  public isNull(value: any) {
    return isNullOrUndefined(value);
  }

  public async getUnwrapped(name: string | null, reduceBigDecimals?: boolean) {
    const value = await this.get(name, true);
    if (value instanceof JsonElementStreamer) {
      return await value.toJsonArray();
    }
    return value;
  }

  public compareTo(a: any, b: any) {
    return compareTo(a, b);
  }

  public async getJsonElement(name: string | null, transform: boolean = true) {
    const value = await this.get(name, transform);
    if (value instanceof JsonElementStreamer) {
      return await value.toJsonArray();
    }
    return value;
  }

  public async getBoolean(name: string | null, transform: boolean = true) {
    const value = await this.get(name, transform);
    if (value == null) {
      return null;
    }
    if (typeof value === "boolean") {
      return value;
    }
    const str = getAsString(value);
    if (str == null) return null;
    return str.trim().toLowerCase() === "true";
  }

  public async getString(name: string | null, transform: boolean = true): Promise<string | null> {
    let value = await this.get(name, transform);
    if (value == null) {
      return null;
    }
    if (value instanceof JsonElementStreamer) {
      value = await value.toJsonArray();
    }
    return getAsString(value);
  }

  public async getEnum(name: string | null, transform: boolean = true) {
    const value = await this.getString(name, transform);
    if (value == null) {
      return null;
    }
    return value.trim().toUpperCase();
  }

  public async getInteger(name: string | null, transform: boolean = true) {
    const value = await this.get(name, transform);
    if (value == null) {
      return null;
    }
    if (value instanceof BigDecimal) {
      return Math.floor(value.toNumber());
    }
    if (typeof value === "number") {
      return Math.floor(value);
    }
    if (typeof value === "bigint") {
      return Number(value);
    }
    let str = getAsString(value);
    if (str == null) return null;
    str = str.trim();
    if (str === "") return null;
    return parseInt(value);
  }

  public async getLong(name: string | null, transform: boolean = true) {
    const value = await this.get(name, transform);
    if (value == null) {
      return null;
    }
    if (typeof value === "bigint") {
      return value;
    }
    if (typeof value === "number" || value instanceof BigDecimal) {
      const fixed = BigDecimal(value).toFixed(0, BigNumber.ROUND_DOWN);
      return BigInt(fixed);
    }
    let str = getAsString(value);
    if (str == null) return null;
    str = str.trim();
    if (str === "") return null;
    const fixed = BigDecimal(value).toFixed(0, BigNumber.ROUND_DOWN);
    return BigInt(fixed);
  }

  public async getBigDecimal(name: string | null, transform: boolean = true) {
    const value = await this.get(name, transform);
    if (value == null) {
      return null;
    }
    if (value instanceof BigDecimal) {
      return value;
    }
    if (typeof value === "number") {
      return new BigDecimal(value);
    }
    let str = getAsString(value);
    if (str == null) return null;
    str = str.trim();
    if (str === "") return null;
    return new BigDecimal(value);
  }

  public async getJsonArray(name: string | null, transform: boolean = true) {
    const value = await this.get(name, transform);
    if (value instanceof JsonElementStreamer) {
      return await value.toJsonArray();
    }
    return Array.isArray(value) ? value : null;
  }

  /**
   * Use this method instead of getJsonArray if you plan on iterating over the array
   * The pros of using this method are
   * - That it will not transform all the array to a single (possibly huge) array in memory
   * - It lazy transforms the array elements, so if there is short-circuiting, some transformations might be prevented
   * @return JsonElementStreamer
   */
  public async getJsonElementStreamer(name: string | null) {
    let transformed = false;
    let value = await this.get(name, false);
    if (value instanceof JsonElementStreamer) {
      return value;
    }
    // in case val is already an array we don't transform it to prevent evaluation of its result values
    // so if is not an array, we must transform it and check after-wards (not lazy anymore)
    if (!Array.isArray(value)) {
      const resolver = await this.getResolver();
      value = await this.extractor.transform(this.getPathFor(name), value, resolver, true);
      if (value instanceof JsonElementStreamer) {
        return value;
      }
      transformed = true;
    }
    // check if initially or after transformation we got an array
    if (Array.isArray(value)) {
      return JsonElementStreamer.fromJsonArray(this, value, transformed);
    }
    return null;
  }

  public async transform(path: string | undefined, definition: any, allowReturningStreams: boolean = false) {
    const resolver = await this.getResolver();
    return await this.extractor.transform(path ?? this.path, definition, resolver, allowReturningStreams);
  }

  public async transformItem(
    definition: any,
    current: any,
    index?: number,
    additionalNameOrContext?: string | Record<string, any>,
    additional?: any,
  ) {
    const currentContext = getDocumentContext(current);
    const resolver = await this.getResolver();
    let itemResolver: ParameterResolver;
    if (typeof index !== "number") {
      itemResolver = {
        get: name =>
          FunctionContext.pathOfVar(FunctionContext.DOUBLE_HASH_CURRENT, name)
            ? currentContext.read(FunctionContext.DOLLAR + name.substring(9))
            : resolver.get(name),
      };
    } else if (!additionalNameOrContext) {
      itemResolver = {
        get: name =>
          name === FunctionContext.DOUBLE_HASH_INDEX
            ? index
            : FunctionContext.pathOfVar(FunctionContext.DOUBLE_HASH_CURRENT, name)
              ? currentContext.read(FunctionContext.DOLLAR + name.substring(9))
              : resolver.get(name),
      };
    } else if (typeof additionalNameOrContext === "string") {
      const additionalContext = getDocumentContext(additional);
      itemResolver = {
        get: name =>
          name === FunctionContext.DOUBLE_HASH_INDEX
            ? index
            : FunctionContext.pathOfVar(FunctionContext.DOUBLE_HASH_CURRENT, name)
              ? currentContext.read(FunctionContext.DOLLAR + name.substring(9))
              : FunctionContext.pathOfVar(additionalNameOrContext, name)
                ? additionalContext.read(FunctionContext.DOLLAR + name.substring(additionalNameOrContext.length))
                : resolver.get(name),
      };
    } else {
      const addCtx = Object.keys(additionalNameOrContext).reduce(
        (a, c) => {
          a[c] = getDocumentContext(additionalNameOrContext[c]);
          return a;
        },
        {} as Record<string, DocumentContext>,
      );
      itemResolver = {
        get: name => {
          if (name === FunctionContext.DOUBLE_HASH_INDEX) return index;
          if (FunctionContext.pathOfVar(FunctionContext.DOUBLE_HASH_CURRENT, name))
            return currentContext.read("$" + name.substring(9));
          for (let key in additionalNameOrContext) {
            if (FunctionContext.pathOfVar(key, name)) {
              return addCtx[key].read(FunctionContext.DOLLAR + name.substring(key.length));
            }
          }
          return resolver.get(name);
        },
      };
    }
    return this.extractor.transform("$", definition, itemResolver, false);
  }
}

export default FunctionContext;
