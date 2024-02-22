package fr.eris.yaml.object;

public class YamlSerializer<T> {

    private final T objectToSerialize;
    private final Class<?> objectClass;

    private YamlDocument serializedDocument;

    public YamlSerializer(T objectToSerialize) {
        this.objectToSerialize = objectToSerialize;
        this.objectClass = objectToSerialize.getClass();
    }

    public static YamlDocument serializeData(String s) {
        return null;
    }

    public YamlDocument serialize() {
        return null;
    }

}
