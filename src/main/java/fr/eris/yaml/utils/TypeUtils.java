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

    public static Type getCollectionType(Collection<?> collection) {
        Type genericType = collection.getClass().getGenericSuperclass();

        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            if (typeArguments.length > 0) {
                return typeArguments[0];
            }
        }

        throw new IllegalArgumentException("Unable to determine the element type of the collection.");
    }

    public static Type canonicalize(Type type) {
        if (type instanceof Class) {
            Class<?> c = (Class<?>) type;
            return c.isArray() ? GenericArrayTypeImpl.make(canonicalize(c.getComponentType())) : c;

        } else if (type instanceof ParameterizedType) {
            ParameterizedTypeImpl p = (ParameterizedTypeImpl) type;
            return ParameterizedTypeImpl.make(
                    p.getRawType(), p.getActualTypeArguments(), p.getOwnerType());

        } else if (type instanceof GenericArrayType) {
            GenericArrayType g = (GenericArrayType) type;
            return GenericArrayTypeImpl.make(g.getGenericComponentType());

        } else {
            // type is either serializable as-is or unsupported
            return type;
        }
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class<?>) {
            // type is a normal class.
            return (Class<?>) type;

        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            // getRawType() returns Type instead of Class; that seems to be an API mistake,
            // see https://bugs.openjdk.org/browse/JDK-8250659
            Type rawType = parameterizedType.getRawType();
            //checkArgument(rawType instanceof Class);
            return (Class<?>) rawType;

        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();

        } else if (type instanceof TypeVariable) {
            // we could use the variable's bounds, but that won't work if there are multiple.
            // having a raw type that's more general than necessary is okay
            return Object.class;

        } else if (type instanceof WildcardType) {
            Type[] bounds = ((WildcardType) type).getUpperBounds();
            // Currently the JLS only permits one bound for wildcards so using first bound is safe
            assert bounds.length == 1;
            return getRawType(bounds[0]);

        } else {
            String className = type == null ? "null" : type.getClass().getName();
            throw new IllegalArgumentException(
                    "Expected a Class, ParameterizedType, or GenericArrayType, but <"
                            + type
                            + "> is of type "
                            + className);
        }

    }
}
