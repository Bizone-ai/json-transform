import TransformerFunctionAnd from "./TransformerFunctionAnd";
import TransformerFunctionAt from "./TransformerFunctionAt";
import TransformerFunctionAvg from "./TransformerFunctionAvg";
import TransformerFunctionBase64 from "./TransformerFunctionBase64";
import TransformerFunctionBoolean from "./TransformerFunctionBoolean";
import TransformerFunctionCidrTest from "./TransformerFunctionCidrTest";
import TransformerFunctionCoalesce from "./TransformerFunctionCoalesce";
import TransformerFunctionCompress from "./TransformerFunctionCompress";
import TransformerFunctionConcat from "./TransformerFunctionConcat";
import TransformerFunctionIn from "./TransformerFunctionIn";
import TransformerFunctionCsv from "./TransformerFunctionCsv";
import TransformerFunctionCsvParse from "./TransformerFunctionCsvParse";
import TransformerFunctionDate from "./TransformerFunctionDate";
import TransformerFunctionDecimal from "./TransformerFunctionDecimal";
import TransformerFunctionDecompress from "./TransformerFunctionDecompress";
import TransformerFunctionDigest from "./TransformerFunctionDigest";
import TransformerFunctionDistinct from "./TransformerFunctionDistinct";
import TransformerFunctionEntries from "./TransformerFunctionEntries";
import TransformerFunctionEq from "./TransformerFunctionEq";
import TransformerFunctionEval from "./TransformerFunctionEval";
import TransformerFunctionEvery from "./TransformerFunctionEvery";
import TransformerFunctionFilter from "./TransformerFunctionFilter";
import TransformerFunctionFind from "./TransformerFunctionFind";
import TransformerFunctionFindIndex from "./TransformerFunctionFindIndex";
import TransformerFunctionFlat from "./TransformerFunctionFlat";
import TransformerFunctionFlatten from "./TransformerFunctionFlatten";
import TransformerFunctionForm from "./TransformerFunctionForm";
import TransformerFunctionFormParse from "./TransformerFunctionFormParse";
import TransformerFunctionGroup from "./TransformerFunctionGroup";
import TransformerFunctionGt from "./TransformerFunctionGt";
import TransformerFunctionGte from "./TransformerFunctionGte";
import TransformerFunctionIf from "./TransformerFunctionIf";
import TransformerFunctionIndexOf from "./TransformerFunctionIndexOf";
import TransformerFunctionIs from "./TransformerFunctionIs";
import TransformerFunctionIsNull from "./TransformerFunctionIsNull";
import TransformerFunctionJoin from "./TransformerFunctionJoin";
import TransformerFunctionJsonParse from "./TransformerFunctionJsonParse";
import TransformerFunctionJsonPatch from "./TransformerFunctionJsonPatch";
import TransformerFunctionJsonPath from "./TransformerFunctionJsonPath";
import TransformerFunctionJsonPointer from "./TransformerFunctionJsonPointer";
import TransformerFunctionJwtParse from "./TransformerFunctionJwtParse";
import TransformerFunctionLength from "./TransformerFunctionLength";
import TransformerFunctionLong from "./TransformerFunctionLong";
import TransformerFunctionLookup from "./TransformerFunctionLookup";
import TransformerFunctionLower from "./TransformerFunctionLower";
import TransformerFunctionLt from "./TransformerFunctionLt";
import TransformerFunctionLte from "./TransformerFunctionLte";
import TransformerFunctionMap from "./TransformerFunctionMap";
import TransformerFunctionMatch from "./TransformerFunctionMatch";
import TransformerFunctionMatchAll from "./TransformerFunctionMatchAll";
import TransformerFunctionMath from "./TransformerFunctionMath";
import TransformerFunctionMax from "./TransformerFunctionMax";
import TransformerFunctionMerge from "./TransformerFunctionMerge";
import TransformerFunctionMin from "./TransformerFunctionMin";
import TransformerFunctionNeq from "./TransformerFunctionNeq";
import TransformerFunctionNin from "./TransformerFunctionNin";
import TransformerFunctionNormalize from "./TransformerFunctionNormalize";
import TransformerFunctionNot from "./TransformerFunctionNot";
import TransformerFunctionNumberFormat from "./TransformerFunctionNumberFormat";
import TransformerFunctionNumberParse from "./TransformerFunctionNumberParse";
import TransformerFunctionObject from "./TransformerFunctionObject";
import TransformerFunctionOr from "./TransformerFunctionOr";
import TransformerFunctionPad from "./TransformerFunctionPad";
import TransformerFunctionPartition from "./TransformerFunctionPartition";
import TransformerFunctionPathJoin from "./TransformerFunctionPathJoin";
import TransformerFunctionRandom from "./TransformerFunctionRandom";
import TransformerFunctionRange from "./TransformerFunctionRange";
import TransformerFunctionRaw from "./TransformerFunctionRaw";
import TransformerFunctionReduce from "./TransformerFunctionReduce";
import TransformerFunctionRepeat from "./TransformerFunctionRepeat";
import TransformerFunctionReplace from "./TransformerFunctionReplace";
import TransformerFunctionReverse from "./TransformerFunctionReverse";
import TransformerFunctionSlice from "./TransformerFunctionSlice";
import TransformerFunctionSome from "./TransformerFunctionSome";
import TransformerFunctionSort from "./TransformerFunctionSort";
import TransformerFunctionSplit from "./TransformerFunctionSplit";
import TransformerFunctionStddev from "./TransformerFunctionStddev";
import TransformerFunctionString from "./TransformerFunctionString";
import TransformerFunctionSubstring from "./TransformerFunctionSubstring";
import TransformerFunctionSum from "./TransformerFunctionSum";
import TransformerFunctionSwitch from "./TransformerFunctionSwitch";
import TransformerFunctionTemplate from "./TransformerFunctionTemplate";
import TransformerFunctionTest from "./TransformerFunctionTest";
import TransformerFunctionTransform from "./TransformerFunctionTransform";
import TransformerFunctionTrim from "./TransformerFunctionTrim";
import TransformerFunctionTypeOf from "./TransformerFunctionTypeOf";
import TransformerFunctionUnflatten from "./TransformerFunctionUnflatten";
import TransformerFunctionUpper from "./TransformerFunctionUpper";
import TransformerFunctionUriParse from "./TransformerFunctionUriParse";
import TransformerFunctionUrlDecode from "./TransformerFunctionUrlDecode";
import TransformerFunctionUrlEncode from "./TransformerFunctionUrlEncode";
import TransformerFunctionUuid from "./TransformerFunctionUuid";
import TransformerFunctionValue from "./TransformerFunctionValue";
import TransformerFunctionWrap from "./TransformerFunctionWrap";
import TransformerFunctionXml from "./TransformerFunctionXml";
import TransformerFunctionXmlParse from "./TransformerFunctionXmlParse";
import TransformerFunctionXor from "./TransformerFunctionXor";
import TransformerFunctionYaml from "./TransformerFunctionYaml";
import TransformerFunctionYamlParse from "./TransformerFunctionYamlParse";

export default {
  all: new TransformerFunctionEvery(), // * alias for every
  and: new TransformerFunctionAnd(),
  any: new TransformerFunctionSome(), // * alias for some
  at: new TransformerFunctionAt(),
  avg: new TransformerFunctionAvg(),
  base64: new TransformerFunctionBase64(),
  boolean: new TransformerFunctionBoolean(),
  cidrtest: new TransformerFunctionCidrTest(),
  coalesce: new TransformerFunctionCoalesce(),
  compress: new TransformerFunctionCompress(),
  concat: new TransformerFunctionConcat(),
  csv: new TransformerFunctionCsv(),
  csvparse: new TransformerFunctionCsvParse(),
  date: new TransformerFunctionDate(),
  decimal: new TransformerFunctionDecimal(),
  decompress: new TransformerFunctionDecompress(),
  digest: new TransformerFunctionDigest(),
  distinct: new TransformerFunctionDistinct(),
  eq: new TransformerFunctionEq(),
  entries: new TransformerFunctionEntries(),
  eval: new TransformerFunctionEval(),
  every: new TransformerFunctionEvery(),
  filter: new TransformerFunctionFilter(),
  find: new TransformerFunctionFind(),
  findindex: new TransformerFunctionFindIndex(),
  first: new TransformerFunctionCoalesce(), // * alias for coalesce
  flat: new TransformerFunctionFlat(),
  flatten: new TransformerFunctionFlatten(),
  form: new TransformerFunctionForm(),
  formparse: new TransformerFunctionFormParse(),
  group: new TransformerFunctionGroup(),
  gt: new TransformerFunctionGt(),
  gte: new TransformerFunctionGte(),
  if: new TransformerFunctionIf(),
  in: new TransformerFunctionIn(),
  indexof: new TransformerFunctionIndexOf(),
  is: new TransformerFunctionIs(),
  isnull: new TransformerFunctionIsNull(),
  join: new TransformerFunctionJoin(),
  jsonparse: new TransformerFunctionJsonParse(),
  jsonpatch: new TransformerFunctionJsonPatch(),
  jsonpath: new TransformerFunctionJsonPath(),
  jsonpointer: new TransformerFunctionJsonPointer(),
  jwtparse: new TransformerFunctionJwtParse(),
  length: new TransformerFunctionLength(),
  long: new TransformerFunctionLong(),
  lookup: new TransformerFunctionLookup(),
  lower: new TransformerFunctionLower(),
  lt: new TransformerFunctionLt(),
  lte: new TransformerFunctionLte(),
  map: new TransformerFunctionMap(),
  match: new TransformerFunctionMatch(),
  matchall: new TransformerFunctionMatchAll(),
  math: new TransformerFunctionMath(),
  max: new TransformerFunctionMax(),
  merge: new TransformerFunctionMerge(),
  min: new TransformerFunctionMin(),
  neq: new TransformerFunctionNeq(),
  nin: new TransformerFunctionNin(),
  normalize: new TransformerFunctionNormalize(),
  not: new TransformerFunctionNot(),
  numberformat: new TransformerFunctionNumberFormat(),
  numberparse: new TransformerFunctionNumberParse(),
  object: new TransformerFunctionObject(),
  or: new TransformerFunctionOr(),
  pad: new TransformerFunctionPad(),
  partition: new TransformerFunctionPartition(),
  pathjoin: new TransformerFunctionPathJoin(),
  random: new TransformerFunctionRandom(),
  range: new TransformerFunctionRange(),
  raw: new TransformerFunctionRaw(),
  reduce: new TransformerFunctionReduce(),
  repeat: new TransformerFunctionRepeat(),
  replace: new TransformerFunctionReplace(),
  reverse: new TransformerFunctionReverse(),
  slice: new TransformerFunctionSlice(),
  some: new TransformerFunctionSome(),
  sort: new TransformerFunctionSort(),
  split: new TransformerFunctionSplit(),
  stddev: new TransformerFunctionStddev(),
  string: new TransformerFunctionString(),
  substring: new TransformerFunctionSubstring(),
  sum: new TransformerFunctionSum(),
  switch: new TransformerFunctionSwitch(),
  template: new TransformerFunctionTemplate(),
  test: new TransformerFunctionTest(),
  transform: new TransformerFunctionTransform(),
  trim: new TransformerFunctionTrim(),
  typeof: new TransformerFunctionTypeOf(),
  unflatten: new TransformerFunctionUnflatten(),
  upper: new TransformerFunctionUpper(),
  uriparse: new TransformerFunctionUriParse(),
  urldecode: new TransformerFunctionUrlDecode(),
  urlencode: new TransformerFunctionUrlEncode(),
  uuid: new TransformerFunctionUuid(),
  value: new TransformerFunctionValue(),
  wrap: new TransformerFunctionWrap(),
  xml: new TransformerFunctionXml(),
  xmlparse: new TransformerFunctionXmlParse(),
  xor: new TransformerFunctionXor(),
  yaml: new TransformerFunctionYaml(),
  yamlparse: new TransformerFunctionYamlParse(),
};
