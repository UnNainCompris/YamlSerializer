package fr.eris.yaml.object.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use to let know the serializer and the deserializer
 * that a field is a savable field.
 * For supported field see TypeUtils.java
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface YamlExpose {
    String yamlSaveName = null;
}
