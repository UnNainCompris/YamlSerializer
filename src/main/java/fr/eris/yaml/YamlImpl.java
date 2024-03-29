package fr.eris.yaml;

import fr.eris.yaml.api.Yaml;
import fr.eris.yaml.api.object.YamlDocument;
import fr.eris.yaml.api.object.serializer.YamlDeserializer;
import fr.eris.yaml.api.object.value.YamlValue;
import fr.eris.yaml.object.YamlDocumentImpl;
import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.parser.YamlParserImpl;
import fr.eris.yaml.object.serialization.YamlDeserializerImpl;
import fr.eris.yaml.object.serialization.YamlSerializerImpl;
import fr.eris.yaml.object.value.YamlValueParser;
import fr.eris.yaml.utils.FileUtils;
import lombok.Getter;

import java.io.File;

public class YamlImpl implements Yaml {

    @Getter private static final YamlImpl yaml;
    @Getter private final YamlValueParser yamlValueParser;
    @Getter private final YamlParserImpl yamlParser;

    static {
        yaml = new YamlImpl();
    }

    public YamlImpl() {
        yamlValueParser = new YamlValueParser();
        yamlParser = new YamlParserImpl();
    }

    public <T extends YamlValue<?>> void loadYamlValue(T newYamlValue) {
        Class<?> handledType = newYamlValue.getHandledType();
        if(yamlValueParser.isAlreadyHandledType(handledType))
            throw new ErisYamlException("A yaml value cannot be register twice in the parser !");

        yamlValueParser.registerNewValue(newYamlValue);
    }

    public YamlDocument createEmptyDocument() {
        return new YamlDocumentImpl();
    }

    public String serializeObject(Object toSerialize) {
        YamlDocument document = new YamlSerializerImpl<>(toSerialize).serialize();
        return document.serialize();
    }

    public YamlDocument createDocumentFromObject(Object object) {
        YamlSerializerImpl<?> yamlSerializer = new YamlSerializerImpl<>(object);
        return yamlSerializer.serialize();
    }

    public <T> T deserializeDocument(Class<T> clazz, YamlDocument document) {
        YamlDeserializer<T> yamlDeserializer = new YamlDeserializerImpl<>(document.serialize(), clazz);
        return yamlDeserializer.buildObject();
    }

    public <T> T deserializeData(Class<T> clazz, String serializedData) {
        YamlDeserializer<T> yamlDeserializer = new YamlDeserializerImpl<>(serializedData, clazz);
        return yamlDeserializer.buildObject();
    }

    public void saveDocument(YamlDocument document, File file) {
        String serializedData = document.serialize();
        FileUtils.writeFile(file, serializedData);
    }

    public YamlDocument createDocumentFromFile(File file) {
        return createDocumentFromData(FileUtils.readFile(file));
    }

    public YamlDocument createDocumentFromData(String data) {
        YamlDocument document = new YamlDocumentImpl();
        document.applyData(data);
        return document;
    }

    public <T> T retrieveObjectFromFile(File file, Class<T> clazz) {
        return deserializeData(clazz, FileUtils.readFile(file));
    }

    public void loadObjectFromFile(File file, Object object) {
        new YamlDeserializerImpl<>(FileUtils.readFile(file), object).buildObject();
    }

    public void loadObjectFromData(String data, Object object) {
        new YamlDeserializerImpl<>(data, object).buildObject();
    }
}
