package fr.eris.yaml.api.object;

public interface YamlObject extends YamlSerializable {

    String getName();
    void setPrefix(String newPrefix);

    void validateName();

    YamlObject getChild(String childName);
    boolean hasChild(String childName);

    void validateNode();

}
