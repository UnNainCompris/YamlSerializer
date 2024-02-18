package fr.evermon.yaml.oldobject.node;

import fr.evermon.yaml.oldobject.impl.YamlObject;
import fr.evermon.yaml.utils.IndentationUtils;
import fr.evermon.yaml.utils.TypeUtils;
import lombok.Getter;

public class YamlNode<T> extends YamlObject {

    @Getter private final T nodeValue;

    public YamlNode(String objectName, T value) {
        super(objectName);
        if(!TypeUtils.isNativeObject(value)) {
            throw new IllegalArgumentException("Value in node value can only be a Native type. Native type allowed: {" + TypeUtils.getNativeType() + "}");
        }
        nodeValue = value;
    }

    public YamlNode(String objectName) {
        super(objectName);
        nodeValue = null;
    }

    public void validateNode() {
        if(nodeValue != null && !children.isEmpty())
            throw new IllegalStateException("A node cannot have children if the value is not null !");
    }

    public String serialize(int indentationLevel) {
        validateNode();
        StringBuilder yaml = new StringBuilder();
        yaml.append(IndentationUtils.createIndentation(indentationLevel))
                .append(isListObject() ? "- " : "")
                .append(objectName)
                .append(": ");

        if(nodeValue != null) {
            yaml.append(nodeValue).append("\n");
        }
        else {
            yaml.append("\n");
            for(YamlObject child : children) {
                yaml.append(child.serialize(indentationLevel + 1));
            }
        }

        return yaml.toString();
    }
}
