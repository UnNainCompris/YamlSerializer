package fr.eris.yaml.object.node;

import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.IYamlObject;
import fr.eris.yaml.utils.IndentationUtils;
import fr.eris.yaml.utils.TypeUtils;
import lombok.Getter;
import lombok.Setter;

public class YamlNode<T> extends IYamlObject {

    @Getter private T value;

    public YamlNode(String objectName, T value) {
        super(objectName);
        if(value != null && !TypeUtils.isNativeObject(value)) {
            throw new ErisYamlException("A node value can only be an Native type like String, integer and other.");
        }
        this.value = value;
    }

    public YamlNode(String objectName) {
        this(objectName, null);
    }

    public void validateNode() {
        if(value != null && !children.isEmpty()) {
            throw new ErisYamlException("A separator node cannot have children and a value !");
        }
    }

    public void setValue(T newValue) {
        this.value = newValue;
        validateNode();
    }

    public String serialize(int indentationLevel) {
        validateNode();
        StringBuilder serializedNode = new StringBuilder();
        serializedNode.append(IndentationUtils.createIndentation(indentationLevel));
        if(prefix.isEmpty()) serializedNode.append(name).append(": ");
        else serializedNode.append(prefix);

        if(children.isEmpty())
            serializedNode.append(value != null ? value.toString() : "none");

        for(IYamlObject child : children.values()) {
            serializedNode.append("\n").append(child.serialize(indentationLevel + 1));
        }
        return serializedNode.toString();
    }

    public String toString() {
        return serialize(0);
    }

    public static YamlNode<?> buildEmptyNode(String nodeName) {
        return new YamlNode<>(nodeName);
    }
}
