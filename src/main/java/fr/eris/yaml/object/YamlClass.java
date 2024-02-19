package fr.eris.yaml.object;

public interface YamlClass {
    String serializeClass();
    YamlClass deserializeClass(String serializedClass);
}
