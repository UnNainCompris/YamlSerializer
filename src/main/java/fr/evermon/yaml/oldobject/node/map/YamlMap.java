package fr.evermon.yaml.oldobject.node.map;

import fr.evermon.yaml.oldobject.impl.YamlObject;
import fr.evermon.yaml.utils.IndentationUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class YamlMap<K, V> extends YamlObject {

    @Getter private final List<YamlEntry<K, V>> entryList;

    public YamlMap(String objectName) {
        super(objectName);
        this.entryList = new ArrayList<>();
    }

    public String serialize(int indentationLevel) {
        StringBuilder yaml = new StringBuilder();
        yaml.append(IndentationUtils.createIndentation(indentationLevel))
            .append(isListObject() ? "- " : "")
            .append(objectName)
            .append(": ");

        for(YamlEntry<K, V> entry : entryList) {
            yaml.append("\n")
                .append(entry.serialize(indentationLevel + 1));
        }

        return yaml.toString();
    }

    public void addEntry(YamlEntry<K, V> newEntry) {
        this.entryList.add(newEntry);
    }

    public void addEntry(K key, V value) {
        YamlEntry<K, V> newEntry = new YamlEntry<>(this);
        newEntry.setEntryKey(key);
        newEntry.setEntryValue(value);
        this.entryList.add(newEntry);
    }
}
