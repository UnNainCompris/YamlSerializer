package fr.eris.yaml.api;

import fr.eris.yaml.YamlImpl;
import fr.eris.yaml.api.object.YamlDocument;
import fr.eris.yaml.api.object.parser.YamlParser;
import fr.eris.yaml.api.object.serializer.YamlDeserializer;
import fr.eris.yaml.api.object.value.YamlValue;
import fr.eris.yaml.object.path.YamlPath;
import fr.eris.yaml.object.serialization.YamlDeserializerImpl;
import fr.eris.yaml.object.value.YamlValueParser;
import fr.eris.yaml.utils.FileUtils;

import java.io.File;

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

    YamlDocument createEmptyDocument();
    YamlDocument createDocumentFromFile(File file);
    YamlDocument createDocumentFromData(String data);

    void saveDocument(YamlDocument document, File file);
    <T> T retrieveObjectFromFile(File file, Class<T> clazz);

    void loadObjectFromFile(File file, Object object);
    void loadObjectFromData(String data, Object object);

}
