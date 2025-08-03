export default function htmlEscape (str?: string){
    return str?.replace(/&/g, "&amp;")
        .replace(/{/g, "\\{") // MDX escape
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/\n/g, "&nbsp;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;")
    ?? "";
};