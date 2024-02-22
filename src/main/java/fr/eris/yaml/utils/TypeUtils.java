package fr.eris.yaml.utils;

import javafx.print.Collation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TypeUtils {

    public static boolean isNativeClass(Class<?> clazz) {
        return clazz.isPrimitive();
    }

    public static boolean isNativeObject(Object object) {
        return isNativeClass(object.getClass());
    }

    public static boolean isAnYamlSupportedType(Class<?> clazz) {
        return isNativeClass(clazz) || clazz.isArray() ||
                clazz.isAssignableFrom(Collection.class);
    }
}