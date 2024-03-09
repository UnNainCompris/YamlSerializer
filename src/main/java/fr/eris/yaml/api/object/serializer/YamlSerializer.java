package fr.eris.yaml.api.object.serializer;

import fr.eris.yaml.api.object.YamlDocument;

public interface YamlSerializer<T> {

    T getSerializedObject();
    Class<T> getSerializedObjectClass();
    YamlDocument serialize();

}
