package fr.eris.yaml.object.node.iterable.set;

import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.IYamlObject;
import fr.eris.yaml.utils.IndentationUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Use to represent Collection, Set... as yaml
 */
public class YamlSetNode<V extends IYamlObject> extends IYamlObject {
    private final String ELEMENT_PREFIX = "- ";
    private Set<V> values;

    public YamlSetNode(String objectName) {
        super(objectName);
        values = new HashSet<>();
    }

    public void add(V newElement) {
        values.add(newElement);
        newElement.setPrefix(ELEMENT_PREFIX);
    }

    public void validateNode() {
        if(!children.isEmpty()) {
            throw new ErisYamlException("A set node cannot have children !");
        }
    }

    public String serialize(int indentationLevel) {
        validateNode();
        StringBuilder serializedNode = new StringBuilder();

        serializedNode.append(IndentationUtils.createIndentation(indentationLevel))
                .append(prefix).append(name).append(": ");

        for(IYamlObject child : values) {
            serializedNode.append("\n").append(child.serialize(indentationLevel + 1));
        }
        serializedNode.append("\n");
        return serializedNode.toString();
    }
}
