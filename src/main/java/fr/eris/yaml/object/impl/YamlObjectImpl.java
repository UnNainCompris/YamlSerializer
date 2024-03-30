package fr.eris.yaml.object.impl;

import fr.eris.yaml.api.object.YamlObject;
import fr.eris.yaml.api.object.YamlSerializable;
import fr.eris.yaml.api.object.annotation.YamlCommentPlacement;
import fr.eris.yaml.object.comment.YamlObjectComment;
import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.path.YamlPath;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;

public abstract class YamlObjectImpl implements YamlObject {

    @Getter protected final String name;
    @Setter protected String prefix; // use to set prefix and replace the name (used for list & set management)
    @Setter @Getter protected YamlObject parent;
    @Setter protected YamlObjectComment comment;
    protected final LinkedHashMap<String, YamlObject> children;

    public YamlObjectImpl(String name) {
        prefix = "";
        this.name = name;
        validateName();
        this.children = new LinkedHashMap<>();
    }

    public void validateName() {
        if(name == null || name.isEmpty()) {
            throw new ErisYamlException("Cannot instantiate a IYamlObject without name !");
        } if(name.contains(YamlPath.YAML_PATH_SEPARATOR)) {
            throw new ErisYamlException("Cannot instantiate a IYamlObject with a " +
                    "name that contains an '.' {" + name + "} !");
        }
    }

    public void addChildren(YamlObject... newChildren) {
        if(newChildren == null) return;
        for(YamlObject child : newChildren) {
            if(child == null)
                throw new ErisYamlException("Cannot add children that is null ! {parent:" + this.getName() + "}");
            if(child.getName() == null)
                throw new ErisYamlException("Object name cannot be null ! {parent:" + this.getName() + "}");
            if(children.containsKey(child.getName()))
                throw new ErisYamlException("Cannot have 2 children with the same name and case !");
            if(child.getParent() != null)
                throw new ErisYamlException("An singular IYamlObject cannot have 2 parent !");
            child.setParent(this);
            this.children.put(child.getName(), child);
        }
    }

    public String applyComment(String currentValue, int indentationLevel) {
        if(comment.getYamlCommentPlacement() == YamlCommentPlacement.NEXT_LINE) {
            return currentValue + "\n" + comment.serialize(indentationLevel);
        } else if(comment.getYamlCommentPlacement() == YamlCommentPlacement.PREVIOUS_LINE) {
            return comment.serialize(indentationLevel) + "\n" + currentValue;
        } else if(comment.getYamlCommentPlacement() == YamlCommentPlacement.SAME_LINE) {
            return currentValue + " " + comment.serialize(indentationLevel);
        }
        return "";
    }

    public String toString() {
        return serialize(0);
    }

    public YamlObject getChild(String objectName) {
        return children.get(objectName);
    }

    public boolean hasChild(String objectName) {
        return children.containsKey(objectName);
    }
    public boolean hasAnyChild() {
        return !children.isEmpty();
    }
}
