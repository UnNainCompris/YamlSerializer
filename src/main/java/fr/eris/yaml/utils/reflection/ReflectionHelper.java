package fr.eris.yaml.utils.reflection;

import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.utils.TypeUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionHelper<T> {

    static {
        defaultConfiguration = new ReflectionConfiguration();
    }

    public static final ReflectionConfiguration defaultConfiguration;
    private ReflectionConfiguration currentConfiguration;
    private final Class<T> targetClass;
    private final Object targetObject;

    public ReflectionHelper(Class<T> targetClass) {
        this(targetClass, null);
    }

    public ReflectionHelper(Class<T> targetClass, Object targetObject) {
        this.targetClass = targetClass;
        this.targetObject = targetObject;
    }

    public List<Field> findFieldWithAnnotation(Class<? extends Annotation> annotationType) {
        List<Field> foundedField = new ArrayList<>();
        for(Field field : targetClass.getDeclaredFields()) {
            if(field.isAnnotationPresent(annotationType))
                foundedField.add(field);
        }
        return foundedField;
    }

    public T buildClass(Object... constructorParameter) {
        if(TypeUtils.isNativeClass(targetClass)) {
            throw new ErisYamlException("Cannot create a new instance of primitive or native class ! {type:" + targetClass + "}");
        }

        List<Class<?>> constructorParamList = new ArrayList<>();
        for(Object object : constructorParameter)
            constructorParamList.add(object.getClass());
        Class<?>[] constructorParameterType = constructorParamList.toArray(new Class[0]);
        try {
            Constructor<T> constructor = targetClass.getConstructor(constructorParameterType);
            return constructor.newInstance(constructorParameter);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
