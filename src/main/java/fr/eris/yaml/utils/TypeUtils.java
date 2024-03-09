package fr.eris.yaml.utils;

import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.IYamlObject;
import fr.eris.yaml.object.node.YamlNode;
import fr.eris.yaml.object.node.iterable.list.YamlListNode;
import fr.eris.yaml.object.node.iterable.set.YamlSetNode;
import fr.eris.yaml.utils.reflection.ReflectionHelper;
import javafx.print.Collation;
import sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.*;
import java.util.*;

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

    public static Class<?> convertIfPrimitiveToObject(Class<?> type) {
        if(type.isPrimitive()) {
            Class<?> primitiveToObject = TypeUtils.primitiveToObject(type);
            if (primitiveToObject != null)
                type = primitiveToObject;
        }
        return type;
    }

    public static Class<? extends IYamlObject> getYamlClassFromNativeType(Class<?> clazz) {
        if(List.class.isAssignableFrom(clazz)) {
            return YamlListNode.class;
        } else if(Set.class.isAssignableFrom(clazz)) {
            return YamlSetNode.class;
        } else if(Map.class.isAssignableFrom(clazz)) {
            return null; //return YamlMap.class;
        } else if(TypeUtils.isNativeClass(clazz)) {
            return YamlNode.class;
        }
        return null;
    }

    public static Object buildObjectFromField(Field field) {
        if(List.class.isAssignableFrom(field.getType()))
            return new ArrayList<>();
        else if(Set.class.isAssignableFrom(field.getType()))
            return new HashSet<>();
        else if(Map.class.isAssignableFrom(field.getType()))
            throw new ErisYamlException("Map is not handled yet !");
        else return new ReflectionHelper<>(field.getType()).buildClass();
    }
}
