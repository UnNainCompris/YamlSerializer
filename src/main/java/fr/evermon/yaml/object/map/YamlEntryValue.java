package fr.evermon.yaml.object.map;

import fr.evermon.yaml.object.YamlNode;
import fr.evermon.yaml.object.impl.YamlObject;
import fr.evermon.yaml.object.impl.YamlSerializable;
import fr.evermon.yaml.utils.IndentationUtils;
import fr.evermon.yaml.utils.TypeUtils;
import lombok.Getter;

public class YamlEntryValue<T> implements YamlSerializable {

    @Getter private final T nodeValue;

    public YamlEntryValue(T value) {
        if(!TypeUtils.isNativeObject(value)) {
            throw new IllegalArgumentException("Value in node value can only be a Native type. Native type allowed: {" + TypeUtils.getNativeType() + "}");
        }
        this.nodeValue = value;
    }

    public String serialize(int indentationLevel) {
        return nodeValue.toString();
    }
}
