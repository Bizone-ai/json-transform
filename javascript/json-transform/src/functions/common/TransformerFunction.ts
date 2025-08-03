import FunctionContext from "./FunctionContext";
import { ArgumentsSet, FunctionDescription } from "./FunctionDescription";

function areSame(a: any, b: any): boolean {
  if (a === b) return true;
  if (typeof a !== typeof b) return false;
  if (typeof a === "string" && typeof b === "string") {
    return a.toLowerCase() === b.toLowerCase();
  }
  return false;
}
/**
 * Base class for all transformer functions.
 */
abstract class TransformerFunction {
  protected readonly description: FunctionDescription;

  protected constructor(description: FunctionDescription) {
    this.description = description;
  }

  public parseArguments(args?: Record<string, any> | (string | null)[]): ArgumentsSet | undefined {
    const argsSets = this.description.argsSets;
    if (!argsSets) return undefined;
    if (argsSets.length === 1) return argsSets[0];
    if (!args) return argsSets.at(-1);
    const argSet = Array.isArray(args)
      ? argsSets.find(x =>
          x.every(
            (arg, i) => (!arg.const || areSame(arg.const, args[i])) && (!arg.exists || typeof args[i] !== "undefined"),
          ),
        )
      : argsSets.find(x =>
          x.every(
            arg =>
              (!arg.const || areSame(arg.const, args[arg.name])) &&
              (!arg.exists || typeof args[arg.name] !== "undefined"),
          ),
        );
    return argSet ?? argsSets.at(-1);
  }

  /**
   * Apply the function to the given context.
   * @param context the context
   * @return the result of the function
   */
  public abstract apply(context: FunctionContext): Promise<any>;

  /**
   * Check if the function allows arguments to be used as input.
   * @return true if arguments can be used as input, false otherwise
   */
  public allowsArgumentsAsInput() {
    return this.description.allowsArgumentsAsInput;
  }

  /**
   * Check if the function allows arguments to be used as input.
   * @return true if arguments can be used as input, false otherwise
   */
  public inputIsRaw() {
    return this.description.inputIsRaw;
  }
}

export default TransformerFunction;
