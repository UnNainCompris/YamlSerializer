package fr.evermon.yaml.oldobject.node.list;

import fr.evermon.yaml.oldobject.impl.YamlObject;
import fr.evermon.yaml.utils.IndentationUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class YamlList<V extends YamlObject> extends YamlObject {

    @Getter private final List<V> values;

    public YamlList(String objectName) {
        super(objectName);
        this.values = new ArrayList<>();
    }

    public void add(V newElement) {
        super.addChild(newElement);
        this.values.add(newElement);
        newElement.setListObject(true);
    }

    public String serialize(int indentationLevel) {
        StringBuilder yaml = new StringBuilder();
        yaml.append(IndentationUtils.createIndentation(indentationLevel))
                .append(isListObject() ? "- " : "")
                .append(objectName).append(": ")
                .append("\n");
        for(YamlObject child : children) {
            yaml.append(child.serialize(indentationLevel));
        }
        return yaml.toString();
    }
}
