package fr.eris.yaml.api.object;

public interface YamlObject extends YamlSerializable {

    String getName();
    void setPrefix(String newPrefix);

    void validateName();

    YamlObject getParent();
    void setParent(YamlObject newParent);

    YamlObject getChild(String childName);
    void addChildren(YamlObject... newChildren);
    boolean hasChild(String childName);
    boolean hasAnyChild();

    void validateNode();

}
