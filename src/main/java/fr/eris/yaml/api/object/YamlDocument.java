package fr.eris.yaml.api.object;

import fr.eris.yaml.object.impl.IYamlObject;
import fr.eris.yaml.object.path.YamlPath;

public interface YamlDocument {

    <T extends IYamlObject> T retrieveObject(YamlPath pathToObject, Class<T> requiredObject);
    IYamlObject retrieveObject(YamlPath pathToObject);

    <T extends IYamlObject> T retrieveObject(String pathToObject, Class<T> requiredObject);
    IYamlObject retrieveObject(String pathToObject);

    String serialize();

    void set(YamlPath path, Object value);
    void set(String path, Object value);


}
