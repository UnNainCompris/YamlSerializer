package fr.evermon.yaml.object.map;

import fr.evermon.yaml.object.impl.YamlObject;
import fr.evermon.yaml.object.impl.YamlSerializable;
import fr.evermon.yaml.utils.IndentationUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class YamlMap<K extends YamlSerializable, V extends YamlSerializable> extends YamlObject {

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
}
