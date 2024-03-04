package fr.eris.yaml.utils;

import javafx.print.Collation;
import sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TypeUtils {

    public static boolean isNativeClass(Class<?> clazz) {
        Class<?> objectToPrimitive = objectToPrimitive(clazz);
        return clazz.isPrimitive() || String.class.isAssignableFrom(clazz) ||
                (objectToPrimitive != null && objectToPrimitive.isPrimitive());
    }

    public static boolean isNativeObject(Object object) {
        return isNativeClass(object.getClass());
    }

    public static boolean isAnYamlSupportedType(Class<?> clazz) {
        return isNativeClass(clazz) || clazz.isArray() ||
                Collection.class.isAssignableFrom(clazz);
    }

    public static Class<?> objectToPrimitive(Class<?> clazz) {
        if(Boolean.class.isAssignableFrom(clazz)) return boolean.class;
        if(Double.class.isAssignableFrom(clazz)) return double.class;
        if(Float.class.isAssignableFrom(clazz)) return float.class;
        if(Long.class.isAssignableFrom(clazz)) return long.class;
        if(Integer.class.isAssignableFrom(clazz)) return int.class;
        if(Short.class.isAssignableFrom(clazz)) return short.class;
        if(Byte.class.isAssignableFrom(clazz)) return byte.class;
        return null;
    }

    public static Class<?> primitiveToObject(Class<?> clazz) {
        if(!clazz.isPrimitive())
            throw new IllegalArgumentException("Need a primitive class type !");
        if(boolean.class.isAssignableFrom(clazz)) return Boolean.class;
        if(double.class.isAssignableFrom(clazz)) return Double.class;
        if(float.class.isAssignableFrom(clazz)) return Float.class;
        if(long.class.isAssignableFrom(clazz)) return Long.class;
        if(int.class.isAssignableFrom(clazz)) return Integer.class;
        if(short.class.isAssignableFrom(clazz)) return Short.class;
        if(byte.class.isAssignableFrom(clazz)) return Byte.class;
        return null;
    }
    public static boolean isArrayOrCollection(Class<?> clazz) {
        return clazz.isArray() || Collection.class.isAssignableFrom(clazz);
    }
}
