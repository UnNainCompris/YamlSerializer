package fr.eris.yaml.object.node.iterable.list;

import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.IYamlObject;
import fr.eris.yaml.utils.IndentationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Use to represent ArrayList, List... as yaml
 */
public class YamlListNode<V extends IYamlObject> extends IYamlObject {

    private final String ELEMENT_PREFIX = "- ";
    private List<V> values;

    public YamlListNode(String objectName) {
        super(objectName);
        values = new ArrayList<>();
    }

    public void add(V newElement) {
        values.add(newElement);
        newElement.setPrefix(ELEMENT_PREFIX);
    }

    public void validateNode() {
        if(!children.isEmpty()) {
            throw new ErisYamlException("A list node cannot have children !");
        }
    }

    public String serialize(int indentationLevel) {
        validateNode();
        StringBuilder serializedNode = new StringBuilder();

        serializedNode.append(IndentationUtils.createIndentation(indentationLevel))
                .append(prefix).append(name).append(": ");

        for(V value : values) {
            serializedNode.append("\n").append(value.serialize(indentationLevel + 1));
        }
        serializedNode.append("\n");
        return serializedNode.toString();
    }
}
