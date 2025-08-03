import {generateFunctionsMarkdowns} from "./functionsMarkdown/generateFunctionsMarkdowns";

generateFunctionsMarkdowns(__dirname).catch(e => {
    console.error(e);
    throw e;
});
