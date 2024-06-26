package fr.eris.yaml.object.value;

import fr.eris.yaml.api.object.value.YamlValue;
import lombok.Getter;

@Getter
public abstract class IYamlValue<T> implements YamlValue<T> {

    protected final Class<T> handledType;
    public IYamlValue(Class<T> handledType) {
        this.handledType = handledType;
    }

    public abstract boolean validateValue(String value);
    public boolean validateValue(String value, Class<?> requestedType) {
        return validateValue(value);
    }

    public abstract T parseValue(String value);
    public T parseValue(String value, Class<?> requestedType) {
        return parseValue(value);
    }

}
