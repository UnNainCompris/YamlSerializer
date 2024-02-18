package fr.evermon.yaml.oldobject.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface YamlSaving {
    String savingPath();
}
