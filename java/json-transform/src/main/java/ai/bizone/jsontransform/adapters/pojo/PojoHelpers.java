package ai.bizone.jsontransform.adapters.pojo;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.internal.DefaultsImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

public class PojoHelpers {

    static com.jayway.jsonpath.Configuration getJsonPathConfig() {
        // the default implementation is bundled with the library
        // thus not requiring additional dependencies
        var defaults = DefaultsImpl.INSTANCE;
        return new Configuration.ConfigurationBuilder()
                .jsonProvider(defaults.jsonProvider())
                .mappingProvider(defaults.mappingProvider())
                .options(Set.of(
                        Option.SUPPRESS_EXCEPTIONS
                ))
                .build();
    }

    public static Stream<Field> getAllFields(Class<?> clazz) {
        if (clazz == null) return Stream.empty();
        return Stream.concat(
                getAllFields(clazz.getSuperclass()),
                Arrays.stream(clazz.getDeclaredFields())
                        .filter(f ->
                                !Modifier.isStatic(f.getModifiers()) && (
                                        Modifier.isPublic(f.getModifiers()) ||
                                                Modifier.isProtected(f.getModifiers())
                                )
                        )
        );
    }

    static Object parse(String value) {
        return DefaultsImpl.INSTANCE.jsonProvider().parse(value);
    }

    static String toJson(Object value) {
        return DefaultsImpl.INSTANCE.jsonProvider().toJson(value);
    }
}
