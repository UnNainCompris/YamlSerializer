package fr.eris.yaml.object.impl;

import fr.eris.yaml.object.exception.ErisYamlException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class IYamlObject implements YamlSerializable {

    @Getter protected final String name;
    @Setter protected String prefix; // use to set prefix before the name
    protected IYamlObject parent;
    protected final HashMap<String, IYamlObject> children;

    public IYamlObject(String name) {
        this.name = name;
        this.children = new HashMap<>();
    }

    public void addChildren(IYamlObject... newChildren) {
        if(newChildren == null) return;
        for(IYamlObject child : newChildren) {
            if(child.getName() == null)
                throw new ErisYamlException("Object name cannot be null ! {" + this.getName() + "}");
            if(children.containsKey(child.getName()))
                throw new ErisYamlException("Cannot have 2 children with the same name and case !");
            if(child.parent != null)
                throw new ErisYamlException("An singular IYamlObject cannot have 2 parent !");
            child.parent = this;
            this.children.put(child.getName(), child);
        }
    }
}
