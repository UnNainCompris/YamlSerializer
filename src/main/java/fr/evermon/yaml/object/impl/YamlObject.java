package fr.evermon.yaml.object.impl;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public abstract class YamlObject implements YamlSerializable {

    @Getter protected final String objectName;
    @Getter protected YamlObject parent;
    @Getter protected final List<YamlObject> children = new ArrayList<>();

    @Getter @Setter protected boolean listObject;

    public YamlObject(String objectName) {
        this.objectName = objectName;
    }

    public void addChild(YamlObject newChild) {
        children.add(newChild);
        newChild.parent = this;
    }
}
