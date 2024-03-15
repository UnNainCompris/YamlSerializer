package fr.eris.yaml.object.node.map;

import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.YamlObjectImpl;

public abstract class YamlMapImpl<K, V> extends YamlObjectImpl {
    public YamlMapImpl() {
        super("NotImplemented");
        throw new ErisYamlException("YamlMap aren't implemented yet !");
    }
}
