package fr.eris.yaml.object.node;

import fr.eris.yaml.api.object.YamlObject;
import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.YamlObjectImpl;
import fr.eris.yaml.utils.IndentationUtils;
import fr.eris.yaml.utils.TypeUtils;
import lombok.Getter;

public class YamlNodeImpl<T> extends YamlObjectImpl {

    @Getter private T value;

    public YamlNodeImpl(String objectName, T value) {
        super(objectName);
        if(value != null && !TypeUtils.isNativeObject(value)) {
            throw new ErisYamlException("A node value can only be an Native type like String, integer and other.");
        }
        this.value = value;
    }

    public YamlNodeImpl(String objectName) {
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

        for(YamlObject child : children.values()) {
            serializedNode.append("\n").append(child.serialize(indentationLevel + 1));
        }
        return serializedNode.toString();
    }

    public static YamlNodeImpl<?> buildEmptyNode(String nodeName) {
        return new YamlNodeImpl<>(nodeName);
    }
}
