package fr.eris.yaml.object.node.iterable.set;

import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.YamlObjectImpl;
import fr.eris.yaml.utils.IndentationUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Use to represent Collection, Set... as yaml
 * The only difference between set and list is the order of element
 * list have a right ordered list and set not on deserialization the list or set is adapt from the class field
 * or by default to a list
 */
public class YamlSetNodeImpl<V extends YamlObjectImpl> extends YamlObjectImpl {
    public static final String ELEMENT_PREFIX = "- ";
    private Set<V> values;

    public YamlSetNodeImpl(String objectName) {
        super(objectName);
        values = new HashSet<>();
    }

    public void add(V newElement) {
        values.add(newElement);
        newElement.setPrefix(ELEMENT_PREFIX);
    }

    public void set(Set<V> newValues) {
        this.values = newValues;
    }

    public Set<V> get() {
        return new HashSet<>(values);
    }

    public void validateNode() {
        if(!children.isEmpty()) {
            throw new ErisYamlException("A set node cannot have children !");
        }
    }

    public String serialize(int indentationLevel) {
        validateNode();
        StringBuilder serializedNode = new StringBuilder();
        serializedNode.append(IndentationUtils.createIndentation(indentationLevel));
        if(prefix.isEmpty()) serializedNode.append(name).append(": ");
        else serializedNode.append(prefix);


        for(YamlObjectImpl child : values) {
            serializedNode.append("\n").append(child.serialize(indentationLevel + 1));
        }
        return serializedNode.toString();
    }
}
