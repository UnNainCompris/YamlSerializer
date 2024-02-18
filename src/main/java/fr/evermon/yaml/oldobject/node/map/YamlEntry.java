package fr.evermon.yaml.oldobject.node.map;

import fr.evermon.yaml.oldobject.impl.YamlSerializable;
import fr.evermon.yaml.utils.IndentationUtils;
import lombok.Setter;

public class YamlEntry<K, V> implements YamlSerializable {

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
        return IndentationUtils.createIndentation(indentationLevel) +
                entryKey +
                ": " +
                (entryValue != null ? entryValue : "");
    }
}
