package fr.eris.yaml.object.node.map;

import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.IYamlObject;

public abstract class YamlMap<K, V> extends IYamlObject {
    public YamlMap() {
        super("NotImplemented");
        throw new ErisYamlException("YamlMap aren't implemented yet !");
    }
}
