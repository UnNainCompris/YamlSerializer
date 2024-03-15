package fr.eris.yaml.api.object.annotation;

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
    String yamlSaveName() default "";
    boolean serializeEvenIfNull() default false;

    /**
     * If true on deserialization if the field is not found inside the serialized data
     * we use the default value of the field if set to false we set it to null.
     * @return
     */
    boolean useDefaultValue() default false;
}
