package fr.eris.yaml.object;

import fr.eris.yaml.api.Yaml;
import fr.eris.yaml.api.object.YamlDocument;
import fr.eris.yaml.api.object.YamlObject;
import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.YamlObjectImpl;
import fr.eris.yaml.object.node.YamlNodeImpl;
import fr.eris.yaml.object.path.YamlPath;
import fr.eris.yaml.utils.TypeUtils;
import fr.eris.yaml.utils.YamlUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class YamlDocumentImpl implements YamlDocument {

    private final LinkedHashMap<String, YamlObject> rootObjects;

    public YamlDocumentImpl() {
        rootObjects = new LinkedHashMap<>();
    }

    public void addRootObject(YamlObject newRootObject) {
        if(rootObjects.containsKey(newRootObject.getName()))
            throw new ErisYamlException("Cannot have root object with same name");
        rootObjects.put(newRootObject.getName(), newRootObject);
    }

    private void removeRootObject(String rootObjectName) {
        rootObjects.remove(rootObjectName);
    }

    public String serialize() {
        StringBuilder serializedDocument = new StringBuilder();

        for(YamlObject rootObject : rootObjects.values()) {
            serializedDocument.append(rootObject.serialize(0)).append("\n");
        }

        return serializedDocument.toString();
    }

    public <T extends YamlObject> T retrieveObject(YamlPath pathToObject, Class<T> yamlObjectType) {
        YamlObject lastObject = rootObjects.get(pathToObject.getFirstPathValue());
        for(String objectName : pathToObject.getWholePathExceptFirstValue()) {
            if(lastObject == null) break;
            YamlObject currentObject = lastObject.getChild(objectName);
            if(currentObject == null) break;
            lastObject = currentObject;
        }
        if(lastObject == null) return null;
        if(!yamlObjectType.isAssignableFrom(lastObject.getClass())) {
            throw new ErisYamlException("The requested value was not the right type of value ! " +
                    "{Requested=" + yamlObjectType + ";Given=" + lastObject.getClass() + "}");
        }
        return yamlObjectType.cast(lastObject);
    }

    public void applyData(String data) {
        LinkedHashMap<YamlPath, String> serializedValue = Yaml.getYaml().getYamlParser().parseYamlContent(data);
        for(Map.Entry<YamlPath, String> entry : serializedValue.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    public YamlObjectImpl retrieveObject(YamlPath pathToObject) {
        return retrieveObject(pathToObject, YamlObjectImpl.class);
    }

    public YamlObject retrieveObject(String pathToObject) {
        return retrieveObject(YamlPath.fromGlobalPath(pathToObject), YamlObjectImpl.class);
    }

    public <T extends YamlObject> T retrieveObject(String pathToObject, Class<T> yamlObjectType) {
        return retrieveObject(YamlPath.fromGlobalPath(pathToObject), yamlObjectType);
    }

    public void set(String path, Object value) {
        set(YamlPath.fromGlobalPath(path), value);
    }

    public void set(YamlPath path, Object value) {
        YamlObject currentObject;

        if(rootObjects.containsKey(path.getFirstPathValue()))
            currentObject = retrieveObject(path.getFirstPathValue());
        else {
            currentObject = YamlNodeImpl.buildEmptyNode(path.getFirstPathValue());
            addRootObject(currentObject);
        }


        for (String currentNodeName : path.getWholePathExceptFirstValue()) {
            if(currentObject.hasChild(currentNodeName)) {
                currentObject = currentObject.getChild(currentNodeName);
            } else {
                YamlObject newObject = YamlNodeImpl.buildEmptyNode(currentNodeName);
                currentObject.addChildren(newObject);
                currentObject = newObject;
            }
        }
        if(currentObject == null)
            throw new ErisYamlException("Cannot add children to a object that is null");
        YamlUtils.setValueToYamlObject(currentObject, value);
    }

    public Object get(YamlPath path) {
        try {
            return YamlUtils.getYamlObjectValue(retrieveObject(path));
        } catch (Exception exception) {
            return null;
        }
    }

    public Object get(String path) {
        return get(YamlPath.fromGlobalPath(path));
    }

    public <T> T get(YamlPath path, Class<T> classCast) {
        Object object = get(path);
        if(String.class.isAssignableFrom(object.getClass())) {
            return classCast.cast(Yaml.getYaml().getYamlValueParser().parseValue((String) object, classCast));
        }
        return classCast.cast(get(path));
    }
    public <T> T get(String path, Class<T> classCast) {
        Object object = get(path);
        if(String.class.isAssignableFrom(object.getClass())) {
            return classCast.cast(Yaml.getYaml().getYamlValueParser().parseValue((String) object, classCast));
        }
        return classCast.cast(get(path));
    }

    public boolean isType(String path, Class<?> requestedType) {
        Object rawTargetObject = get(path);
        if(rawTargetObject == null) return false;
        return requestedType.isAssignableFrom(rawTargetObject.getClass());
    }

    public boolean contains(String path) {
        return get(path) != null;
    }

    public boolean isSectionExist(String path) {
        YamlPath pathToObject = YamlPath.fromGlobalPath(path);
        YamlObject lastObject = rootObjects.get(pathToObject.getFirstPathValue());
        if(lastObject == null) return false;
        for(String objectName : pathToObject.getWholePathExceptLastAndFirstValue()) {
            YamlObject currentObject = lastObject.getChild(objectName);
            if(currentObject == null)
                return false;
            lastObject = currentObject;
        }
        return lastObject.hasAnyChild();
    }

    public String getString(String path) {
        return get(path, String.class);
    }

    public String getString(String path, String def) {
        if(!contains(path)) return def;
        return get(path, String.class);
    }

    public double getDouble(String path) {
        return get(path, double.class);
    }

    public double getDouble(String path, double def) {
        if(!contains(path)) return def;
        return getDouble(path);
    }

    public float getFloat(String path) {
        return get(path, float.class);
    }

    public float getFloat(String path, float def) {
        if(!contains(path)) return def;
        return getFloat(path);
    }

    public long getLong(String path) {
        return get(path, long.class);
    }

    public long getLong(String path, long def) {
        if(!contains(path)) return def;
        return getLong(path);
    }

    public int getInt(String path) {
        return get(path, int.class);
    }

    public int getInt(String path, int def) {
        if(!contains(path)) return def;
        return getInt(path);
    }

    public short getShort(String path) {
        return get(path, short.class);
    }

    public short getShort(String path, short def) {
        if(!contains(path)) return def;
        return getShort(path);
    }

    public byte getByte(String path) {
        return get(path, byte.class);
    }

    public byte getByte(String path, byte def) {
        if(!contains(path)) return def;
        return getByte(path);
    }
    public Boolean getBoolean(String path) {
        return get(path, Boolean.class);
    }

    public Boolean getBoolean(String path, Boolean def) {
        if(!contains(path)) return def;
        return getBoolean(path);
    }
}
