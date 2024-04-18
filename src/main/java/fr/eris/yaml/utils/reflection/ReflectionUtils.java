package fr.eris.yaml.utils.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtils {
    public static void setAllFieldValueNull(Object object) {
        for(Field field : getFieldOfClass(object.getClass(), true)) {
            try {
                field.setAccessible(true);
                field.set(object, null);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public static List<Field> getFieldOfClass(Class<?> classType, boolean withSuperClass) {
        List<Field> fields = new ArrayList<>(Arrays.asList(classType.getDeclaredFields()));

        Class<?> currentSuperClass = classType.getSuperclass();
        while(currentSuperClass != null && withSuperClass) {
            fields.addAll(Arrays.asList(currentSuperClass.getDeclaredFields()));
            currentSuperClass = currentSuperClass.getSuperclass();
        }

        return fields;
    }
}
