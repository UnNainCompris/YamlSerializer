package fr.evermon.yaml.object.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface YamlSaving {
    String savingPath();
}
