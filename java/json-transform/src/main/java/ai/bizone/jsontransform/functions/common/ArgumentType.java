package ai.bizone.jsontransform.functions.common;

public class ArgumentType {
    public ArgType type;
    public int position = -1;
    public Object defaultValue = null;

    public static ArgumentType of(ArgType type) {
        var arg = new ArgumentType();
        arg.type = type;
        return arg;
    }
    public ArgumentType position(int position) {
        this.position = position;
        return this;
    }
    public ArgumentType defaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
}
