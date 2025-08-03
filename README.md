# json-transform

- [Documentation](https://bizone-ai.github.io/json-transform/)

## Packages

| Language   | Name                                                                   | Description                                   | License            | Status                                                                                    |
|------------|------------------------------------------------------------------------|-----------------------------------------------|--------------------|-------------------------------------------------------------------------------------------|
| Java       | [ai.bizone.json-transform](./java/json-transform)                      | Java library for transforming JSON objects    | Apache License 2.0 | ![Maven Central Version](https://img.shields.io/maven-central/v/ai.bizone/json-transform) |
| JavaScript | [@bizone-ai/json-transform](./javascript/json-transform)               | JSON transformers JavaScript implementation   | MIT                | ![npm](https://img.shields.io/npm/v/@bizone-ai/json-transform)                            |
| JavaScript | [@bizone-ai/json-transform-utils](javascript/json-transform-utils)     | Utilities for handling JSON transformers      | MIT                | ![npm](https://img.shields.io/npm/v/@bizone-ai/json-transform-utils)                      |
| JavaScript | [@bizone-ai/monaco-json-transform](./javascript/monaco-json-transform) | Monaco editor extension for JSON transformers | MIT                | ![npm](https://img.shields.io/npm/v/@bizone-ai/monaco-json-transform)                     |

## Getting Started

### Java

- In your application initialization code set the JsonTransformerConfiguration with your preferred provider:

```java
	public static void main(String[] args) {
        JsonTransformerConfiguration.set(new GsonJsonTransformerConfiguration());
        // ...
    }
```

- See available adapters [here](https://github.com/Bizone-ai/json-transform/tree/main/java/json-transform/src/main/java/ai/bizone/jsontransform/adapters)

Then in the code where you want to transform JSON objects:

```java
    // 'definition' is a JSON (in your selected provider's structure) 
    // defining the spec of the transformer    
    JsonTransformer transformer = new JsonTransformer(definition);
    // 'input' - The input of the transformer (referred as '$')
    // 'additionalContext' - (optional) Map of additional inputs to refer during transformation 
    Object transformed = transformer.transform(input, additionalContext);
```

### JavaScript

```js
    // 'definition' is the spec of the transformer    
    const transformer = new JsonTransformer(definition);
    // 'input' - The input of the transformer (referred as '$')
    // 'additionalContext' - (optional) Map of additional inputs to refer during transformation 
    const result = await transformer.transform(input, additionalContext);
```