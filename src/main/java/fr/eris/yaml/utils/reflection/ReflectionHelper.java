package fr.eris.yaml.utils.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectionHelper {

    static {
        defaultConfiguration = new ReflectionConfiguration();
    }

    public static final ReflectionConfiguration defaultConfiguration;
    private ReflectionConfiguration currentConfiguration;
    private final Class<?> targetClass;
    private final Object targetObject;

    public ReflectionHelper(Class<?> targetClass) {
        this(targetClass, null);
    }

    public ReflectionHelper(Object targetObject) {
        this(targetObject.getClass(), targetObject);
    }

    private ReflectionHelper(Class<?> targetClass, Object targetObject) {
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

}
