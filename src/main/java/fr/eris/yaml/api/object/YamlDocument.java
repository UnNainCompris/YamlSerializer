package fr.eris.yaml.api.object;

import fr.eris.yaml.object.impl.YamlObjectImpl;
import fr.eris.yaml.object.path.YamlPath;

public interface YamlDocument {

    <T extends YamlObject> T retrieveObject(YamlPath pathToObject, Class<T> requiredObject);
    YamlObject retrieveObject(YamlPath pathToObject);

    <T extends YamlObject> T retrieveObject(String pathToObject, Class<T> requiredObject);
    YamlObject retrieveObject(String pathToObject);

    void applyData(String data);

    String serialize();

    void set(YamlPath path, Object value);
    void set(String path, Object value);

    Object get(YamlPath path);
    Object get(String path);

    <T> T get(YamlPath path, Class<T> classCast);
    <T> T get(String path, Class<T> classCast);

    boolean isType(String path, Class<?> requestedType);
    boolean contains(String path);
    boolean isSectionExist(String path);

    String getString(String path);
    double getDouble(String path);
    float getFloat(String path);
    long getLong(String path);
    int getInt(String path);
    short getShort(String path);
    byte getByte(String path);

}
