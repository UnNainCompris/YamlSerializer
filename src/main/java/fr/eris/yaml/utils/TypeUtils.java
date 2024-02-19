package fr.eris.yaml.utils;

import java.util.HashSet;
import java.util.Set;

public class TypeUtils {

    public static boolean isNativeClass(Class<?> clazz) {
        return clazz.isPrimitive();
    }

    public static boolean isNativeObject(Object object) {
        return isNativeClass(object.getClass());
    }
}
