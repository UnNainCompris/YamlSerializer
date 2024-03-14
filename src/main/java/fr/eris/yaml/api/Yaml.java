package fr.eris.yaml.api;

import fr.eris.yaml.YamlImpl;
import fr.eris.yaml.api.object.YamlDocument;
import fr.eris.yaml.api.object.parser.YamlParser;
import fr.eris.yaml.api.object.serializer.YamlDeserializer;
import fr.eris.yaml.api.object.value.YamlValue;
import fr.eris.yaml.object.serialization.YamlDeserializerImpl;
import fr.eris.yaml.object.value.YamlValueParser;

public interface Yaml {

    static Yaml getYaml() {
        return YamlImpl.getYaml();
    }

    YamlValueParser getYamlValueParser();
    YamlParser getYamlParser();

    String serializeObject(Object toSerialize);

    YamlDocument createDocumentFromObject(Object toConvert);

    <T> T deserializeDocument(Class<T> clazz, YamlDocument document);

    <T> T deserializeData(Class<T> clazz, String serializedData);

    <T extends YamlValue<?>> void loadYamlValue(T newYamlValue);
}
