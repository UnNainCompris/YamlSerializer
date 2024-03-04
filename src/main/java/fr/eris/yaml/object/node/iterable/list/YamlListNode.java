package fr.eris.yaml.object.node.iterable.list;

import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.IYamlObject;
import fr.eris.yaml.utils.IndentationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Use to represent ArrayList, List... as yaml
 * The only difference between set and list is the order of element
 * list have a right ordered list and set not on deserialization the list or set is adapt from the class field
 * or by default to a list
 */
public class YamlListNode<V extends IYamlObject> extends IYamlObject {

    public static final String ELEMENT_PREFIX = "- ";
    private List<V> values;

    public YamlListNode(String objectName) {
        super(objectName);
        values = new ArrayList<>();
    }

    public void add(V newElement) {
        values.add(newElement);
        newElement.setPrefix(ELEMENT_PREFIX);
    }

    public void set(List<V> newValues) {
        this.values = newValues;
    }

    public void validateNode() {
        if(!children.isEmpty()) {
            throw new ErisYamlException("A list node cannot have children !");
        }
    }

    public String serialize(int indentationLevel) {
        validateNode();
        StringBuilder serializedNode = new StringBuilder();
        serializedNode.append(IndentationUtils.createIndentation(indentationLevel));

        if(prefix.isEmpty()) serializedNode.append(name).append(": ");
        else serializedNode.append(prefix);


        for(V value : values) {
            serializedNode.append("\n").append(value.serialize(indentationLevel + 1));
        }
        return serializedNode.toString();
    }
}
