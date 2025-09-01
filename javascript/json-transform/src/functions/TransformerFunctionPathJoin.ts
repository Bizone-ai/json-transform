import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { getAsString, isNullOrUndefined } from "../JsonHelpers";
import { AsyncSequence } from "@wortise/sequency";

const POSIX_SEPARATOR = "/";
const WINDOWS_SEPARATOR = "\\";
const WINDOWS_SEPARATOR_ESC = "\\\\";

function NormalizePatternFactory(s: string) {
  return new RegExp("([^." + s + "]+\\.|[^" + s + "]*[^." + s + "]|[^" + s + "]{3,})" + s + "\\.\\.($|" + s + ")", "g");
}
const POSIX_DEDUPE_PATTERN = new RegExp(POSIX_SEPARATOR + "{2,}", "g");
const POSIX_DIRUP_PATTERN = NormalizePatternFactory(POSIX_SEPARATOR);
const POSIX_TRAILING_PATTERN = new RegExp(POSIX_SEPARATOR + "+$");
const POSIX_SAMEDIR_PATTERN = new RegExp(POSIX_SEPARATOR + "([.]" + POSIX_SEPARATOR + ")+", "g");
const POSIX_SAMEDIR_START_PATTERN = new RegExp("^[.]" + POSIX_SEPARATOR);
const WINDOWS_DEDUPE_PATTERN = new RegExp(WINDOWS_SEPARATOR_ESC + "{2,}", "g");
const WINDOWS_DIRUP_PATTERN = NormalizePatternFactory(WINDOWS_SEPARATOR_ESC);
const WINDOWS_TRAILING_PATTERN = new RegExp(WINDOWS_SEPARATOR_ESC + "+$");
const WINDOWS_SAMEDIR_PATTERN = new RegExp(WINDOWS_SEPARATOR_ESC + "([.]" + WINDOWS_SEPARATOR_ESC + ")+", "g");
const WINDOWS_SAMEDIR_START_PATTERN = new RegExp("^[.]" + WINDOWS_SEPARATOR_ESC);

class TransformerFunctionPathJoin extends TransformerFunction {
  constructor() {
    super({
      argsSet: [{ name: "type", type: ArgType.String, defaultValue: "POSIX" }],
    });
  }

  private static normalizePath(rawPath: string, separator: string, isURL: boolean): string {
    let path = separator + rawPath;
    const isWin = separator === WINDOWS_SEPARATOR;
    path = path.replace(isWin ? WINDOWS_DEDUPE_PATTERN : POSIX_DEDUPE_PATTERN, separator); // dedupe all repeating separators

    // normalize dir-up
    const normalizer = isWin ? WINDOWS_DIRUP_PATTERN : POSIX_DIRUP_PATTERN;
    while (path.includes("..") && path.indexOf(separator + "..") != 0) {
      const lengthBefore = path.length;
      path = path.replace(normalizer, "");
      if (lengthBefore == path.length) {
        break; // did nothing then break
      }
    }
    if (path.indexOf(separator + ".." + separator) == 0 || path === separator + "..") {
      throw new Error('Invalid path "' + rawPath + '"');
    }
    path = path.replace(isWin ? WINDOWS_TRAILING_PATTERN : POSIX_TRAILING_PATTERN, ""); // remove trailing separators
    path = path.replace(isWin ? WINDOWS_SAMEDIR_PATTERN : POSIX_SAMEDIR_PATTERN, separator);
    path = path.replace(isWin ? WINDOWS_SAMEDIR_START_PATTERN : POSIX_SAMEDIR_START_PATTERN, separator);
    return isURL ? path : path.substring(1);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const arr = await context.getJsonElementStreamer(null);
    if (arr == null) {
      return null;
    }
    const type = await context.getEnum("type"); // should be either "/" or "\"
    const separator = type === "WINDOWS" ? WINDOWS_SEPARATOR : POSIX_SEPARATOR;

    let stream = (arr.stream().map(getAsString) as unknown as AsyncSequence<string>).filter(
      el => !isNullOrUndefined(el) && el !== "",
    );
    const isURL = type === "URL";
    if (isURL) {
      stream = stream.map(x => encodeURIComponent(x));
    }
    const path = await stream.joinToString({ separator });
    return TransformerFunctionPathJoin.normalizePath(path, separator, isURL);
  }
}

export default TransformerFunctionPathJoin;
