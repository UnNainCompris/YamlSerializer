package fr.evermon.yaml.utils;

import java.util.HashSet;
import java.util.Set;

public class TypeUtils {

    private static final Set<Class<?>> NATIVE_CLASSES = new HashSet<>();

    static {
        NATIVE_CLASSES.add(String.class);
        NATIVE_CLASSES.add(Integer.class);
        NATIVE_CLASSES.add(Double.class);
        NATIVE_CLASSES.add(Float.class);
        NATIVE_CLASSES.add(Byte.class);
        NATIVE_CLASSES.add(Short.class);
        NATIVE_CLASSES.add(Character.class);
        NATIVE_CLASSES.add(Boolean.class);
    }

    public static boolean isNativeClass(Class<?> clazz) {
        return NATIVE_CLASSES.contains(clazz);
    }

    public static boolean isNativeObject(Object object) {
        return isNativeClass(object.getClass());
    }

    public static Set<Class<?>> getNativeType() {
        return new HashSet<>(NATIVE_CLASSES); // create a copy so it doesn't get edited.
    }
}
