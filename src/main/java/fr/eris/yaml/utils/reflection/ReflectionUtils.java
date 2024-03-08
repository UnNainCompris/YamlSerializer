package fr.eris.yaml.utils.reflection;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static void setAllFieldValueNull(Object object) {
        for(Field field : object.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                field.set(object, null);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }
}
