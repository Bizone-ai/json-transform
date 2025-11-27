import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { ArgType } from "./common/ArgType";

class TransformerFunctionTrim extends TransformerFunction {
  constructor() {
    super({
      argsSet: [
        { name: "type", type: ArgType.String, defaultValue: "BOTH" },
        { name: "what", type: ArgType.String },
      ],
    });
  }

  static isJavaWhitespace(codePoint: number): boolean {
    return codePoint <= 0x0020;
  }

  /**
   * Java definition of White Space:
   * @param codePoint
   */
  static isWhitespace(codePoint: number): boolean {
    switch (codePoint) {
      case 0x0009: // '\t' HORIZONTAL TABULATION
      case 0x000a: // '\n' LINE FEED
      case 0x000b: // '\u000B' VERTICAL TABULATION
      case 0x000c: // '\f' FORM FEED
      case 0x000d: // '\r' CARRIAGE RETURN
      case 0x001c: // '\u001C' FILE SEPARATOR
      case 0x001d: // '\u001D' GROUP SEPARATOR
      case 0x001e: // '\u001E' RECORD SEPARATOR
      case 0x001f: // '\u001F' UNIT SEPARATOR
      case 0x0020: // ' ' SPACE
      case 0x2007: // '\u2007' FIGURE SPACE
      case 0x2028: // '\u2028' LINE SEPARATOR
      case 0x2029: // '\u2029' PARAGRAPH SEPARATOR
      case 0x202f: // '\u202F' NARROW NO-BREAK SPACE
        return true;
      default:
        return false;
    }
  }

  static indexOfJavaNonWhitespace(str: string) {
    for (let i = 0; i < str.length; i++) {
      if (!TransformerFunctionTrim.isJavaWhitespace(str.codePointAt(i) ?? 0)) {
        return i;
      }
    }
    return str.length;
  }

  static lastIndexOfJavaNonWhitespace(str: string) {
    for (let i = str.length - 1; i >= 0; i--) {
      if (!TransformerFunctionTrim.isJavaWhitespace(str.codePointAt(i) ?? 0)) {
        return i + 1;
      }
    }
    return 0;
  }

  static indexOfNonWhitespace(str: string) {
    for (let i = 0; i < str.length; i++) {
      if (!TransformerFunctionTrim.isWhitespace(str.codePointAt(i) ?? 0)) {
        return i;
      }
    }
    return str.length;
  }

  static lastIndexOfNonWhitespace(str: string) {
    for (let i = str.length - 1; i >= 0; i--) {
      if (!TransformerFunctionTrim.isWhitespace(str.codePointAt(i) ?? 0)) {
        return i + 1;
      }
    }
    return 0;
  }

  static outdent(lines: string[]) {
    let outdent = Infinity;
    for (const line of lines) {
      let leadingWhitespace = TransformerFunctionTrim.indexOfNonWhitespace(line);
      if (leadingWhitespace !== line.length) {
        outdent = Math.min(outdent, leadingWhitespace);
      }
    }
    const lastLine = lines.at(-1);
    if (lastLine === "") {
      outdent = Math.min(outdent, lastLine.length);
    }
    return outdent;
  }

  static stripIndent(str: string) {
    let length = str.length;
    if (length == 0) {
      return "";
    }
    const lastChar = str.charAt(length - 1);
    const optOut = lastChar === "\n" || lastChar === "\r";
    const lines: string[] = str.split(/[\n\r]/g);
    const outdent = optOut ? 0 : TransformerFunctionTrim.outdent(lines);
    return (
      lines
        .map(line => {
          let firstNonWhitespace = TransformerFunctionTrim.indexOfNonWhitespace(line);
          let lastNonWhitespace = TransformerFunctionTrim.lastIndexOfNonWhitespace(line);
          let incidentalWhitespace = Math.min(outdent, firstNonWhitespace);
          return firstNonWhitespace > lastNonWhitespace ? "" : line.substring(incidentalWhitespace, lastNonWhitespace);
        })
        .join("\n") + (optOut ? "\n" : "")
    );
  }

  /**
   * Finds the index of the first character in the input that is NOT present in badCharacters.
   * Handles Unicode surrogate pairs (e.g., emojis) correctly.
   */
  static indexOfFirstGood(input: string, badCharacters: string): number {
    let i = 0;
    while (i < input.length) {
      // Get the Unicode code point (integer value)
      const cp = input.codePointAt(i);

      // Safety check for undefined (though loop condition handles this)
      if (cp === undefined) break;

      // Convert code point back to string to check inclusion
      const charStr = String.fromCodePoint(cp);

      // If the character is NOT in the badCharacters string
      if (!badCharacters.includes(charStr)) {
        return i;
      }

      // Increment index by 2 for surrogate pairs (emojis), 1 otherwise
      i += charStr.length;
    }
    return i;
  }

  /**
   * Finds the index of the last character in the input that is NOT present in badCharacters.
   * Iterates backward handling Unicode surrogate pairs correctly.
   */
  static indexOfLastGood(input: string, badCharacters: string): number {
    // Array.from correctly splits a string by Code Points (keeping surrogates together)
    // This mimics the Java `input.codePoints().toArray()` logic
    const chars = Array.from(input);

    let currentIndex = input.length;

    // Iterate backwards through the actual characters
    for (let j = chars.length - 1; j >= 0; j--) {
      const charStr = chars[j];
      const charLen = charStr.length;

      // Check if current char is found in badCharacters
      if (!badCharacters.includes(charStr)) {
        return currentIndex - charLen;
      }

      // Decrement the actual string index
      currentIndex -= charLen;
    }

    return -1;
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    const type = await context.getEnum("type");
    if (type === "JAVA") {
      const firstIndex = TransformerFunctionTrim.indexOfJavaNonWhitespace(str);
      const lastIndex = TransformerFunctionTrim.lastIndexOfJavaNonWhitespace(str);
      return str.substring(firstIndex, lastIndex);
    }
    if (type === "INDENT") {
      return TransformerFunctionTrim.stripIndent(str);
    }

    const what = await context.getString("what");
    if (what) {
      switch (type) {
        case "START": {
          return str.substring(TransformerFunctionTrim.indexOfFirstGood(str, what));
        }
        case "END": {
          return str.substring(0, TransformerFunctionTrim.indexOfLastGood(str, what) + 1);
        }
        default: {
          const firstIndex = TransformerFunctionTrim.indexOfFirstGood(str, what);
          const lastIndex = TransformerFunctionTrim.indexOfLastGood(str, what) + 1;
          return str.substring(firstIndex, lastIndex);
        }
      }
    }
    switch (type) {
      case "START": {
        const index = TransformerFunctionTrim.indexOfNonWhitespace(str);
        return str.substring(index);
      }
      case "END": {
        const index = TransformerFunctionTrim.lastIndexOfNonWhitespace(str);
        return str.substring(0, index);
      }
      default: {
        const firstIndex = TransformerFunctionTrim.indexOfNonWhitespace(str);
        const lastIndex = TransformerFunctionTrim.lastIndexOfNonWhitespace(str);
        return str.substring(firstIndex, lastIndex);
      }
    }
  }
}

export default TransformerFunctionTrim;
