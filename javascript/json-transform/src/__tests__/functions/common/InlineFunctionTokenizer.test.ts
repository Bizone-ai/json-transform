import { describe, expect, test } from "vitest";
import InlineFunctionTokenizer from "../../../functions/common/InlineFunctionTokenizer";

describe("InlineFunctionTokenizer", () => {
  test("tokenize - test 1", () => {
    expect(InlineFunctionTokenizer.tokenize("$$foo")).toEqual({
      name: "foo",
      keyLength: 5,
    });
  });
  test("tokenize - test 2", () => {
    expect(InlineFunctionTokenizer.tokenize("$$foo(arg1,'arg2')")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 4,
          value: "arg1",
        },
        {
          index: 11,
          length: 6,
          value: "arg2",
        },
      ],
    });
  });
  test("tokenize - test 3", () => {
    expect(InlineFunctionTokenizer.tokenize("$$foo:some input ")).toEqual({
      name: "foo",
      input: {
        index: 6,
        length: 11,
        value: "some input ",
      },
      keyLength: 5,
    });
  });
  test("tokenize - test 4", () => {
    expect(InlineFunctionTokenizer.tokenize("$$foo(arg1,'arg2'):some input ")).toEqual({
      name: "foo",
      args: [
        {
          index: 6,
          length: 4,
          value: "arg1",
        },
        {
          index: 11,
          length: 6,
          value: "arg2",
        },
      ],
      input: {
        index: 19,
        length: 11,
        value: "some input ",
      },
      keyLength: 5,
    });
  });
  test("tokenize - test 5", () => {
    expect(InlineFunctionTokenizer.tokenize("$$foo(arg1,'a(rg2'):some input ")).toEqual({
      name: "foo",
      args: [
        {
          index: 6,
          length: 4,
          value: "arg1",
        },
        {
          index: 11,
          length: 7,
          value: "a(rg2",
        },
      ],
      input: {
        index: 20,
        length: 11,
        value: "some input ",
      },
      keyLength: 5,
    });
  });
  test("tokenize - test 6", () => {
    expect(InlineFunctionTokenizer.tokenize("$$foo(a(rg1,'a(rg2'):some input ")).toEqual({
      name: "foo",
      args: [
        {
          index: 6,
          length: 5,
          value: "a(rg1",
        },
        {
          index: 12,
          length: 7,
          value: "a(rg2",
        },
      ],
      input: {
        index: 21,
        length: 11,
        value: "some input ",
      },
      keyLength: 5,
    });
  });
  test("tokenize - test 7", () => {
    expect(InlineFunctionTokenizer.tokenize("$$foo(a()rg1,'a(rg2'):some input ")).toBeUndefined();
  });
  test("tokenize - test 8", () => {
    expect(InlineFunctionTokenizer.tokenize("$$foo('a()rg1','a(rg2'):some input ")).toEqual({
      name: "foo",
      args: [
        {
          index: 6,
          length: 8,
          value: "a()rg1",
        },
        {
          index: 15,
          length: 7,
          value: "a(rg2",
        },
      ],
      input: {
        index: 24,
        length: 11,
        value: "some input ",
      },
      keyLength: 5,
    });
  });

  test("tokenize - test 9", () => {
    expect(InlineFunctionTokenizer.tokenize("$$map('##current.name'):$$flat:$.names")).toEqual({
      name: "map",
      args: [
        {
          index: 6,
          length: 16,
          value: "##current.name",
        },
      ],
      input: {
        index: 24,
        length: 14,
        value: "$$flat:$.names",
      },
      keyLength: 5,
    });
  });
  test("tokenize - test 10", () => {
    expect(InlineFunctionTokenizer.tokenize("$$foo()")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 0,
          value: null,
        },
      ],
    });
  });
  test("tokenize - test 11", () => {
    expect(InlineFunctionTokenizer.tokenize("$$foo(\\\\$)")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 3,
          value: "\\\\$",
        },
      ],
    });
  });
  test("tokenize - test 12", () => {
    expect(InlineFunctionTokenizer.tokenize("$$foo('\\\\$')")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 5,
          value: "\\$",
        },
      ],
    });
  });
  test("tokenize - test 13", () => {
    expect(InlineFunctionTokenizer.tokenize("$$foo(' ')")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 3,
          value: " ",
        },
      ],
    });
  });
  test("tokenize - test 14", () => {
    expect(InlineFunctionTokenizer.tokenize("$$foo( )")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 1,
          value: " ",
        },
      ],
    });
  });
  test("tokenize - test 15", () => {
    expect(InlineFunctionTokenizer.tokenize("$$foo(  '  a ' ,b)")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 9,
          value: "  a ",
        },
        {
          index: 16,
          length: 1,
          value: "b",
        },
      ],
    });
  });
  test("tokenize - test 16", () => {
    //await assertTransformation(null, "$$argstest('\\n\\r\\t\\u0f0f'):", "\n\r\t\u0f0f");
    // not detected
    //await assertTransformation(null, "$$argstest(\n\r\t\u0f0f)", "$$argstest(\n\r\t\u0f0f)");
    expect(InlineFunctionTokenizer.tokenize("$$foo('\\n\\r\\t\\u0f0f')")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 14,
          value: "\n\r\t\u0f0f",
        },
      ],
    });
  });
  test("tokenize - test 17", () => {
    expect(InlineFunctionTokenizer.tokenize("$$foo(\n\r\t\u0f0f)")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 4,
          value: "\n\r\t\u0f0f",
        },
      ],
    });
  });
});
