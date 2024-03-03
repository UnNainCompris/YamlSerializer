package fr.eris.yaml.object.value;

import lombok.Getter;

public abstract class YamlValue<T> {

    @Getter
    protected final Class<T> handledType;

    public YamlValue(Class<T> handledType) {
        this.handledType = handledType;
    }

    public abstract boolean validateValue(String value);
    public abstract T parseValue(String value);

}
