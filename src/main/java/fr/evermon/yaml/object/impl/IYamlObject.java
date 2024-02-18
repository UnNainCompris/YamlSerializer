package fr.evermon.yaml.object.impl;

import fr.evermon.yaml.object.exception.YamlSerializerException;
import fr.evermon.yaml.oldobject.impl.YamlObject;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class IYamlObject implements YamlSerializable {

    protected final String name;
    @Setter protected String prefix; // use to set prefix before the name
    protected IYamlObject parent;
    protected final List<IYamlObject> children;

    public IYamlObject(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public void addChildren(IYamlObject... newChildren) {
        if(newChildren == null) return;
        for(IYamlObject child : newChildren) {
            if(child.parent != null)
                throw new YamlSerializerException("An singular IYamlObject cannot have 2 parent !");
            child.parent = this;
            this.children.add(child);
        }
    }
}
