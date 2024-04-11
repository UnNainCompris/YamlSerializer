package fr.eris.yaml.api.object.value;

import lombok.Getter;

public interface YamlValue<T> {

    Class<T> getHandledType();
    boolean validateValue(String value, Class<?> requestedType);
    boolean validateValue(String value);
    T parseValue(String value);
    T parseValue(String value, Class<?> requestedType);
}
