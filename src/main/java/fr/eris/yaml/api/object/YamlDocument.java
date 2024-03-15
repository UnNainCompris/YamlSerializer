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

    Object get(YamlPath path);
    Object get(String path);

    <T> T get(YamlPath path, Class<T> classCast);
    <T> T get(String path, Class<T> classCast);

    boolean isType(String path, Class<?> requestedType);
    boolean contains(String path);

    String getString(String path);
    double getDouble(String path);
    float getFloat(String path);
    long getLong(String path);
    int getInt(String path);
    short getShort(String path);
    byte getByte(String path);

}
