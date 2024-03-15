package fr.eris.yaml.object;

import fr.eris.yaml.api.object.YamlDocument;
import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.YamlObjectImpl;
import fr.eris.yaml.object.node.YamlNodeImpl;
import fr.eris.yaml.object.path.YamlPath;
import fr.eris.yaml.utils.YamlUtils;

import java.util.HashMap;

public class YamlDocumentImpl implements YamlDocument {

    private final HashMap<String, YamlObjectImpl> rootObjects;

    public YamlDocumentImpl() {
        rootObjects = new HashMap<>();
    }

    public void addRootObject(YamlObjectImpl newRootObject) {
        if(rootObjects.containsKey(newRootObject.getName()))
            throw new ErisYamlException("Cannot have root object with same name");
        rootObjects.put(newRootObject.getName(), newRootObject);
    }

    private void removeRootObject(String rootObjectName) {
        rootObjects.remove(rootObjectName);
    }

    public String serialize() {
        StringBuilder serializedDocument = new StringBuilder();

        for(YamlObjectImpl rootObject : rootObjects.values()) {
            serializedDocument.append(rootObject.serialize(0)).append("\n");
        }

        return serializedDocument.toString();
    }

    public <T extends YamlObjectImpl> T retrieveObject(YamlPath pathToObject, Class<T> yamlObjectType) {
        YamlObjectImpl lastObject = rootObjects.get(pathToObject.getFirstPathValue());
        for(String objectName : pathToObject.getWholePathExceptFirstValue()) {
            if(lastObject == null) break;
            YamlObjectImpl currentObject = lastObject.getChild(objectName);
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

    public YamlObjectImpl retrieveObject(YamlPath pathToObject) {
        return retrieveObject(pathToObject, YamlObjectImpl.class);
    }

    public YamlObjectImpl retrieveObject(String pathToObject) {
        return retrieveObject(YamlPath.fromGlobalPath(pathToObject), YamlObjectImpl.class);
    }

    public <T extends YamlObjectImpl> T retrieveObject(String pathToObject, Class<T> yamlObjectType) {
        return retrieveObject(YamlPath.fromGlobalPath(pathToObject), yamlObjectType);
    }

    public void set(String path, Object value) {
        set(YamlPath.fromGlobalPath(path), value);
    }

    public void set(YamlPath path, Object value) {
        YamlObjectImpl currentObject;

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
                YamlObjectImpl newObject = YamlNodeImpl.buildEmptyNode(currentNodeName);
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
        return classCast.cast(get(path));
    }
    public <T> T get(String path, Class<T> classCast) {
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

    public String getString(String path) {
        return get(path, String.class);
    }

    public double getDouble(String path) {
        return get(path, double.class);
    }

    public float getFloat(String path) {
        return get(path, float.class);
    }

    public long getLong(String path) {
        return get(path, long.class);
    }

    public int getInt(String path) {
        return get(path, int.class);
    }

    public short getShort(String path) {
        return get(path, short.class);
    }

    public byte getByte(String path) {
        return get(path, byte.class);
    }
}
