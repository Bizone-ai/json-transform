import { describe, expect, test } from "vitest";
import { mergeInto, merge, unwrapNumber } from "../JsonHelpers";
import BigNumber from "bignumber.js";

describe("JsonHelpers", () => {
  test("mergeInto - GivenMutuallyExclusiveKeysWithDot", () => {
    var root = {
      "numbers.roman": { I: 1, II: 2 },
    };
    var mergee = {
      "numbers.exist": true,
    };
    var expected = {
      "numbers.roman": { I: 1, II: 2 },
      "numbers.exist": true,
    };
    expect(expected).toEqual(mergeInto(root, mergee, null));
  });

  test("mergeInto - GivenNoPath", () => {
    var root = {
      "a.b.c[0]": "foovalue",
    };
    var mergee = {
      a: { z: "barvalue" },
    };
    var expected = {
      a: { z: "barvalue" },
      "a.b.c[0]": "foovalue",
    };
    expect(expected).toEqual(mergeInto(root, mergee, null));
  });

  test("mergeInto - GivenMutuallyExclusiveKeysAndDollarPath", () => {
    var root = {
      roman: { I: 1, II: 2 },
    };
    var mergee = {
      arithmetics: { exist: true },
      symbols: ["I", "V", "X", "L", "C", "D", "M"],
    };
    var expected = {
      roman: { I: 1, II: 2 },
      arithmetics: { exist: true },
      symbols: ["I", "V", "X", "L", "C", "D", "M"],
    };
    expect(expected).toEqual(mergeInto(root, mergee, "$"));
  });

  test("merge - all new", () => {
    expect(
      merge(
        {
          a: "A",
        },
        {
          b: "B",
        },
      ),
    ).toEqual({
      a: "A",
      b: "B",
    });
  });

  test("merge - override existing", () => {
    expect(
      merge(
        {
          a: "A",
          b: "B",
        },
        {
          b: "BB",
        },
      ),
    ).toEqual({
      a: "A",
      b: "BB",
    });
  });

  test("merge - override with null", () => {
    expect(
      merge(
        {
          a: "A",
          b: "B",
        },
        {
          b: null,
        },
      ),
    ).toEqual({
      a: "A",
      b: null,
    });
  });

  test("merge - shallow", () => {
    expect(
      merge(
        {
          a: {
            aa: "AA",
          },
          b: "B",
        },
        {
          a: {
            aaa: "AAA",
          },
        },
      ),
    ).toEqual({
      a: {
        aaa: "AAA",
      },
      b: "B",
    });
  });

  test("merge - deep", () => {
    expect(
      merge(
        {
          a: {
            aa: "AA",
          },
          b: "B",
        },
        {
          a: {
            aaa: "AAA",
          },
        },
        { deep: true },
      ),
    ).toEqual({
      a: {
        aa: "AA",
        aaa: "AAA",
      },
      b: "B",
    });
  });

  test("merge - deep and concatArray", () => {
    expect(
      merge(
        {
          a: {
            aa: "AA",
          },
          c: [1, 2],
        },
        {
          a: {
            aaa: "AAA",
          },
          c: [3, 4],
        },
        { deep: true, concatArrays: true },
      ),
    ).toEqual({
      a: {
        aa: "AA",
        aaa: "AAA",
      },
      c: [1, 2, 3, 4],
    });
  });

  test("merge - concatArray", () => {
    expect(
      merge(
        {
          a: {
            aa: "AA",
          },
          c: [1, 2],
        },
        {
          a: {
            aaa: "AAA",
          },
          c: [3, 4],
        },
        { concatArrays: true },
      ),
    ).toEqual({
      a: {
        aaa: "AAA",
      },
      c: [1, 2, 3, 4],
    });
  });

  test("unwrapNumber - numbers", () => {
    expect(unwrapNumber(1)).toEqual(1);
    expect(unwrapNumber(Number.MIN_SAFE_INTEGER)).toEqual(Number.MIN_SAFE_INTEGER);
    expect(unwrapNumber(Number.MIN_VALUE)).toEqual(Number.MIN_VALUE);
    expect(unwrapNumber(Number.MAX_SAFE_INTEGER)).toEqual(Number.MAX_SAFE_INTEGER);
    expect(unwrapNumber(Number.MAX_VALUE)).toEqual(Number.MAX_VALUE);
  });

  test("unwrapNumber - bigint", () => {
    expect(unwrapNumber(BigInt(1))).toEqual(1);
    expect(unwrapNumber(BigInt(Number.MAX_SAFE_INTEGER))).toEqual(Number.MAX_SAFE_INTEGER);
  });

  test("unwrapNumber - bigint (cant unwrap)", () => {
    expect(unwrapNumber(BigInt(Number.MAX_VALUE))).not.toEqual(Number.MAX_VALUE);
    const ban = BigInt("123456789012345678901234567890");
    expect(unwrapNumber(ban)).toEqual(ban);
  });

  test("unwrapNumber - bignumber", () => {
    const ban = BigInt("123456789012345678901234567890");
    expect(unwrapNumber(BigNumber(1))).toEqual(1);
    expect(unwrapNumber(BigNumber(Number.MAX_SAFE_INTEGER))).toEqual(Number.MAX_SAFE_INTEGER);
    expect(unwrapNumber(BigNumber(1.456e-324))).toEqual(1.456e-324);
    expect(unwrapNumber(BigNumber(ban.toString()))).toEqual(ban);
  });

  test("unwrapNumber - bignumber (cant unwrap)", () => {
    const ban = BigNumber("987654321.23456789012345678901234567890");
    expect(unwrapNumber(ban)).toEqual(ban);
  });
});
