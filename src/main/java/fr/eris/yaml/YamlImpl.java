package fr.eris.yaml;

import fr.eris.yaml.api.Yaml;
import fr.eris.yaml.api.object.YamlDocument;
import fr.eris.yaml.api.object.parser.YamlParser;
import fr.eris.yaml.api.object.value.YamlValue;
import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.parser.YamlParserImpl;
import fr.eris.yaml.object.value.YamlValueParser;
import lombok.Getter;

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

    public String serializeObject(Object toSerialize) {
        YamlDocument document = new YamlSerializer<>(toSerialize).serialize();
        return document.serialize();
    }

    public <T> T deserializeObject(Class<T> clazz, String serializedData) {
        return null;
    }

    public YamlDocument createDocumentFromObject(Object object) {
        YamlSerializer<?> yamlSerializer = new YamlSerializer<>(object);
        return yamlSerializer.serialize();
    }

    public <T> T deserializeDocument(Class<T> clazz, YamlDocument document) {
        return null;
    }

}
