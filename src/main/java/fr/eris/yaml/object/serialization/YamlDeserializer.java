package fr.eris.yaml.object.serialization;

import fr.eris.yaml.object.YamlDocument;
import fr.eris.yaml.utils.reflection.ReflectionHelper;

public class YamlDeserializer<T> {

    private final String serializedData;
    private final Class<T> objectClass;
    private final ReflectionHelper reflectionHelper;

    private YamlDocument serializedDocument;

    public YamlDeserializer(String serializedData, Class<T> objectClass) {
        this.serializedData = serializedData;
        this.objectClass = objectClass;
        this.reflectionHelper = new ReflectionHelper(objectClass);
    }

    public T retrieveClass() {
        loadDocument();

        return null;
    }

    public void loadDocument() {
        serializedDocument = YamlDocument.generateFromSerializedData(serializedData);
    }

}
