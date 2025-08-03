import { add } from "date-fns/add";
import { addMilliseconds } from "date-fns/addMilliseconds";
import { format } from "date-fns/format";
import { formatISO } from "date-fns/formatISO";
import { fromUnixTime } from "date-fns/fromUnixTime";
import { sub } from "date-fns/sub";
import { subMilliseconds } from "date-fns/subMilliseconds";
import { parseJSON } from "date-fns/parseJSON";
import { differenceInMilliseconds } from "date-fns/differenceInMilliseconds";
import { differenceInSeconds } from "date-fns/differenceInSeconds";
import { differenceInMinutes } from "date-fns/differenceInMinutes";
import { differenceInHours } from "date-fns/differenceInHours";
import { differenceInDays } from "date-fns/differenceInDays";
import { differenceInMonths } from "date-fns/differenceInMonths";
import { differenceInYears } from "date-fns/differenceInYears";
import { TZDate } from "@date-fns/tz/date";
import { tz } from "@date-fns/tz/tz";
import BigNumber from "bignumber.js";
import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { BigDecimal } from "./common/FunctionHelpers";

const ChronoUnitToDuration: Record<string, string> = {
  SECONDS: "seconds",
  MINUTES: "minutes",
  HOURS: "hours",
  DAYS: "days",
  MONTHS: "months",
  YEARS: "years",
};

const ISO_TRIM = /\.000(Z|[+-]\d\d:)/;
const MAX_TRIM = /\.?0+(Z|[+-]\d\d:)/;
/**
 *
 * @param date date to format
 * @param maxTrimmed if true, trim with min width of 0, otherwise, render milliseconds in 3 digits as usual unless it's 0
 */
const toISOString = (date: Date, maxTrimmed?: boolean) => {
  const iso = date.toISOString();
  return iso.replace(maxTrimmed ? MAX_TRIM : ISO_TRIM, "$1");
};

class TransformerFunctionDate extends TransformerFunction {
  constructor() {
    super({
      argsSets: [
        [
          { name: "format", type: ArgType.String, const: "ADD" },
          { name: "units", type: ArgType.String },
          { name: "amount", type: ArgType.Number, defaultValue: 0 },
        ],
        [
          { name: "format", type: ArgType.String, const: "SUB" },
          { name: "units", type: ArgType.String },
          { name: "amount", type: ArgType.Number, defaultValue: 0 },
        ],
        [{ name: "format", type: ArgType.String, const: "GMT" }],
        [{ name: "format", type: ArgType.String, const: "DATE" }],
        [
          { name: "format", type: ArgType.String, const: "DIFF" },
          { name: "units", type: ArgType.String },
          { name: "end", type: ArgType.Any },
        ],
        [
          { name: "format", type: ArgType.String, const: "EPOCH" },
          { name: "resolution", type: ArgType.String, defaultValue: "UNIX" },
        ],
        [
          { name: "format", type: ArgType.String, const: "FORMAT" },
          { name: "pattern", type: ArgType.String },
          { name: "timezone", type: ArgType.String, defaultValue: "UTC" },
        ],
        [
          { name: "format", type: ArgType.String, const: "ZONE" },
          { name: "timezone", type: ArgType.String, defaultValue: "UTC" },
        ],
        // default
        [
          { name: "format", type: ArgType.String, const: "ISO", defaultValue: "ISO" },
          { name: "digits", type: ArgType.Number, defaultValue: -1 },
        ],
      ],
    });
  }

  private static parseInstant(value: any): Date {
    if (value instanceof Date) return value;
    if (typeof value === "string") {
      if (value.includes("T")) {
        return parseJSON(value);
      }
      if (value.length === 10 && !value.includes(":")) {
        // assuming just date
        return new Date(value);
      }
      if (value.includes(":")) return parseJSON(`1970-01-01T${value}`);
      value = parseInt(value);
    }
    if (typeof value === "number") {
      if (value < 2671726769) {
        return fromUnixTime(value);
      }
      return new Date(value);
    }
    return parseJSON(value);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const unwrapped = await context.getUnwrapped(null);
    if (unwrapped == null) {
      return null;
    }
    const instant = TransformerFunctionDate.parseInstant(unwrapped);
    switch (await context.getEnum("format")) {
      case "ISO": {
        switch (await context.getInteger("digits")) {
          case 0:
            return instant.toISOString().replace(/\.\d+/, ""); // no second fractions
          case 3:
            return instant.toISOString(); // milliseconds
          case 6:
            return instant.toISOString().replace("Z", "000Z"); // microseconds
          case 9:
            return instant.toISOString().replace("Z", "000000Z"); // nanoseconds
          default:
            return toISOString(instant);
        }
      }
      case "GMT":
        return instant.toUTCString();
      case "ADD": {
        const units = await context.getEnum("units");
        if (units && ChronoUnitToDuration[units]) {
          return toISOString(add(instant, { [ChronoUnitToDuration[units]]: await context.getInteger("amount") }));
        }
        switch (units) {
          case "HALF_DAYS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return toISOString(add(instant, { hours: amount * 12 }));
          }
          case "MILLIS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return toISOString(addMilliseconds(instant, amount));
          }
          case "NANOS":
          case "MICROS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return toISOString(addMilliseconds(instant, amount / (units === "NANOS" ? 1e6 : 1e3)));
          }
        }
        return null;
      }
      case "SUB": {
        const units = await context.getEnum("units");
        if (units && ChronoUnitToDuration[units]) {
          return toISOString(sub(instant, { [ChronoUnitToDuration[units]]: await context.getInteger("amount") }));
        }
        switch (units) {
          case "HALF_DAYS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return toISOString(sub(instant, { hours: amount * 12 }));
          }
          case "MILLIS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return toISOString(subMilliseconds(instant, amount));
          }
          case "NANOS":
          case "MICROS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return toISOString(subMilliseconds(instant, amount / (units === "NANOS" ? 1e6 : 1e3)));
          }
        }
        return null;
      }
      case "DATE": {
        return formatISO(instant, { representation: "date" });
      }
      case "DIFF": {
        const units = await context.getEnum("units");
        const end = TransformerFunctionDate.parseInstant(await context.get("end"));
        switch (units) {
          case "NANOS":
            return differenceInMilliseconds(end, instant) * 1e6;
          case "MICROS":
            return differenceInMilliseconds(end, instant) * 1e3;
          case "MILLIS":
            return differenceInMilliseconds(end, instant);
          case "SECONDS":
            return differenceInSeconds(end, instant);
          case "MINUTES":
            return differenceInMinutes(end, instant);
          case "HOURS":
            return differenceInHours(end, instant);
          case "HALF_DAYS":
            return Math.trunc(differenceInHours(end, instant) / 12);
          case "DAYS":
            return differenceInDays(end, instant);
          case "MONTHS":
            return differenceInMonths(end, instant);
          case "YEARS":
            return differenceInYears(end, instant);
        }
        return null;
      }
      case "EPOCH": {
        switch (await context.getEnum("resolution")) {
          case "MS":
            return BigDecimal(instant.getTime());
          default:
            return BigDecimal(instant.getTime()).dividedBy(1000).integerValue(BigNumber.ROUND_FLOOR);
        }
      }
      case "FORMAT": {
        const timeZone = await context.getString("timezone");
        const pattern = (await context.getString("pattern")) ?? "";
        if (timeZone) {
          return format(tz(timeZone)(instant), pattern); //(instant) formatInTimeZone(instant, timeZone, pattern);
        }
        return format(instant, pattern);
      }
      case "ZONE": {
        const zone = await context.getString("timezone");
        const iso = toISOString(instant);
        if (!zone) return iso;
        return toISOString(TZDate.tz(zone, instant), true);
      }
      default:
        return null;
    }
  }
}

export default TransformerFunctionDate;
