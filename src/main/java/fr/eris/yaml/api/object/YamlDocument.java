package fr.eris.yaml.api.object;

import fr.eris.yaml.object.IYamlDocument;
import fr.eris.yaml.object.impl.IYamlObject;
import fr.eris.yaml.object.path.YamlPath;

import java.util.HashMap;

public interface YamlDocument {

    IYamlObject retrieveObject(YamlPath pathToObject);
    IYamlObject retrieveObject(String pathToObject);

    String serialize();

    void set(YamlPath path, Object value);
    void set(String path, Object value);


}
