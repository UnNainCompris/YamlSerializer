package fr.eris.yaml.object.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use to be combined with YamlExpose
 * On the serialization every field that is serialized and have this field
 * will have a comment place at the targeted position
 * <p>
 * If its same line, yaml break line ({@value fr.eris.yaml.object.comment.YamlComment#YAML_SPLIT_LINE_CHAR}) will not be taken in count !
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface YamlSerializationComment {
    String comment();
    YamlCommentPlacement placement();
}
