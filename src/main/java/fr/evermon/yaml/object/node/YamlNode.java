package fr.evermon.yaml.object.node;

import fr.evermon.yaml.object.exception.YamlSerializerException;
import fr.evermon.yaml.object.impl.IYamlObject;
import fr.evermon.yaml.oldobject.impl.YamlObject;
import fr.evermon.yaml.utils.IndentationUtils;
import fr.evermon.yaml.utils.TypeUtils;
import lombok.Getter;

public class YamlNode<T> extends IYamlObject {

    @Getter private T value;

    public YamlNode(String objectName, T value) {
        super(objectName);
        if(value != null && !TypeUtils.isNativeObject(value)) {
            throw new YamlSerializerException("A node value can only be an Native type. Native type allowed: {" + TypeUtils.getNativeType() + "}");
        }
        this.value = value;
    }

    public YamlNode(String objectName) {
        this(objectName, null);
    }

    public void validateNode() {
        if(value != null && !children.isEmpty()) {
            throw new YamlSerializerException("A node cannot have children and a value !");
        }
    }

    public String serialize(int indentationLevel) {
        validateNode();
        StringBuilder serializedNode = new StringBuilder();
        serializedNode.append(IndentationUtils.createIndentation(indentationLevel))
                .append(prefix).append(name).append(": ");

        if(children.isEmpty())
            serializedNode.append(value.toString());

        for(IYamlObject child : children) {
            serializedNode.append(child.serialize(indentationLevel + 1));
        }
        serializedNode.append("\n");
        return serializedNode.toString();
    }

    public String toString() {
        return serialize(0);
    }

    public static YamlNode<?> buildEmptyNode(String nodeName) {
        return new YamlNode<>(nodeName);
    }
}
