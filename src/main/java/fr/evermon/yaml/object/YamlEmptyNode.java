package fr.evermon.yaml.object;

import fr.evermon.yaml.object.impl.YamlObject;
import fr.evermon.yaml.utils.IndentationUtils;

/**
 * <p> An empty node should be used to be like a separator
 * <p> Here an example of where it should be used :
 * <p>
 * <p> RootNode:                        # Here because this node don't hold any value we are using an YamlEmptyNode
 * <p>   ChildNode{1}:                  # Same here
 * <p>     ChildNode{2}:                # Same here
 * <p>       ValueNode: Here a value!   # Here we are using a YamlNode because we want to hold a value
 * <p>
 * <p> This Yaml path can be seen like this:
 * <p>   RootNode.ChildNode{1}.ChildNode{2}.ValueNode
 * <p>
 * <p> In conclusion if you need a separator for a yaml data sheet you'll use an YamlEmptyNode
 */
public class YamlEmptyNode extends YamlObject {

    public YamlEmptyNode(String objectName) {
        super(objectName);
    }

    public String serialize(int indentationLevel) {
        StringBuilder yaml = new StringBuilder();
        yaml.append(IndentationUtils.createIndentation(indentationLevel))
                .append(isListObject() ? "- " : "")
                .append(objectName)
                .append(": ");

        yaml.append("\n");
        for(YamlObject child : children) {
            yaml.append(child.serialize(indentationLevel + 1));
        }

        return yaml.toString();
    }
}
