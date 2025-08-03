import { ArgumentType } from "./ArgumentType";

type FixedArgumentType = Omit<ArgumentType, "position"> & { name: string };

export type ArgumentsSet = FixedArgumentType[];

export type FunctionDescription = {
  argsSets?: ArgumentsSet[];
  arguments?: Record<string, ArgumentType>;
  allowsArgumentsAsInput?: boolean; // if true, the function can accept arguments as input
  inputIsRaw?: boolean; // if true, the function input should not be transformed
};
