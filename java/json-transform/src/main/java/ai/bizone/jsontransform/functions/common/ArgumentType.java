package ai.bizone.jsontransform.functions.common;

import java.util.List;

public class ArgumentType {
    public ArgType type;
    public int position = -1;
    public Object defaultValue = null;
    public List<String> aliases = null;

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
    public ArgumentType aliases(List<String> aliases) {
        this.aliases = aliases;
        return this;
    }
}
