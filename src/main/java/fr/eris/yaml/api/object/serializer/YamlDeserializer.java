package fr.eris.yaml.api.object.serializer;

import fr.eris.yaml.object.path.YamlPath;

import java.util.LinkedHashMap;

public interface YamlDeserializer<T> {

    String getSerializedData();
    Class<T> getRequiredObjectClass();
    T buildObject();
}
