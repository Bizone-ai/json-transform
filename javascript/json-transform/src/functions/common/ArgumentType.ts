import { ArgType } from "./ArgType";

export type ArgumentType = {
  type: ArgType;
  position?: number;
  defaultValue?: any;
  aliases?: string[];

  const?: any; // condition to choose that argument set
  exists?: boolean; // condition to choose that argument set
};
