const u8Encoder = new TextEncoder();
const u16Encoder = {
  encode: function (input: string) {
    // BE with BOM (like Java)
    const length = input.length;
    const u8 = new Uint8Array(length * 2 + 2);
    u8[0] = 0xfe;
    u8[1] = 0xff;
    const offset = 2;
    for (let i = 0; i < length; i++) {
      const code = input.charCodeAt(i);
      u8[offset + i * 2] = (code & 0xff00) >> 8;
      u8[offset + i * 2 + 1] = code & 0xff;
    }
    return u8;
  },
};

const iso88591Supported =
  new TextDecoder("iso_8859-1", { ignoreBOM: false }).decode(new Uint8Array([139]).buffer).charCodeAt(0) == 139;
const iso88591 = {
  encode: function (input: string) {
    const u8 = new Uint8Array(input.length);
    for (let i = 0; i < input.length; i++) {
      u8[i] = input.charCodeAt(i) & 0xff;
    }
    return u8;
  },
  decode: function (input: Uint8Array) {
    let result = "";
    for (let i = 0; i < input.length; i++) {
      result += String.fromCharCode(input[i]);
    }
    return result;
  },
};

const TextEncoding = {
  encode: function (input: string, charset: string | null = "UTF-8") {
    if (charset === "ISO-8859-1") {
      return iso88591.encode(input);
    }
    if (charset === "UTF-16") {
      return u16Encoder.encode(input);
    }
    return u8Encoder.encode(input);
  },
  decode: function (input: Uint8Array, charset: string | null = "UTF-8") {
    let cs = charset?.toLowerCase();
    if (cs === "iso-8859-1" && !iso88591Supported) {
      // browsers treat iso-8859-1 as windows-1252, so we need to decode it ourselves
      return iso88591.decode(input);
    }
    if (cs === "utf-16" && !(input[0] === 0xff && input[1] === 0xfe)) {
      // if BOM is missing, assume BE
      cs = "utf-16be";
    }
    return new TextDecoder(cs, {
      ignoreBOM: false /* don't include BOM in output */,
    }).decode(input.buffer);
  },
};

export default TextEncoding;
