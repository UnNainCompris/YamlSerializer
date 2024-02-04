package fr.evermon.yaml.object.map;

import fr.evermon.yaml.object.impl.YamlObject;
import fr.evermon.yaml.object.impl.YamlSerializable;
import fr.evermon.yaml.utils.IndentationUtils;
import lombok.Setter;

public class YamlEntry<K extends YamlSerializable, V extends YamlSerializable> implements YamlSerializable {

    private YamlMap<K, V> parentMap;

    @Setter private K entryKey;
    @Setter private V entryValue;

    public YamlEntry(YamlMap<K, V> parentMap) {
        this.parentMap = parentMap;
    }

    public String serialize(int indentationLevel) {
        if(entryKey == null) {
            throw new IllegalStateException("The entry key cannot be null ! parentMap {" + parentMap.getObjectName() + "}");
        }

        StringBuilder yaml = new StringBuilder();
        yaml.append(IndentationUtils.createIndentation(indentationLevel))
                .append(entryKey.serialize(indentationLevel))
                .append(entryValue != null ? entryValue.serialize(indentationLevel) : "");

        return yaml.toString();
    }
}
