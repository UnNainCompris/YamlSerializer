package fr.eris.yaml.object;

import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.IYamlObject;
import fr.eris.yaml.object.node.YamlNode;
import fr.eris.yaml.object.node.iterable.list.YamlListNode;
import fr.eris.yaml.object.node.iterable.set.YamlSetNode;
import fr.eris.yaml.object.node.map.YamlMap;
import fr.eris.yaml.object.path.YamlPath;
import fr.eris.yaml.object.serialization.YamlDeserializer;
import fr.eris.yaml.object.serialization.YamlSerializer;
import fr.eris.yaml.utils.YamlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class YamlDocument {

    private final HashMap<String, IYamlObject> rootObjects;

    public YamlDocument() {
        rootObjects = new HashMap<>();
    }
    
    public void addRootObject(IYamlObject newRootObject) {
        if(rootObjects.containsKey(newRootObject.getName()))
            throw new ErisYamlException("Cannot have root object with same name");
        
        rootObjects.put(newRootObject.getName(), newRootObject);
    }
    
    public void removeRootObject(String rootObjectName) {
        rootObjects.remove(rootObjectName);
    }

    public static <T> YamlDocument generateFromClass(T clazz) {
        return new YamlSerializer<>(clazz).serialize();
    }
    
    public String serialize() {
        StringBuilder serializedDocument = new StringBuilder();

        for(IYamlObject rootObject : rootObjects.values()) {
            serializedDocument.append(rootObject.serialize(0)).append("\n");
        }

        return serializedDocument.toString();
    }

    public <T> T deserialize(Class<T> deserializationClassType) {
        return new YamlDeserializer<T>(serialize(), deserializationClassType).retrieveClass();
    }

    public <T extends IYamlObject> T retrieveObject(String yamlObjectName, Class<T> yamlObjectType) {
        IYamlObject foundedObject = rootObjects.get(yamlObjectName);
        if(foundedObject == null) return null;
        if(yamlObjectType.isAssignableFrom(foundedObject.getClass())) {
            return yamlObjectType.cast(foundedObject);
        } else {
            throw new ErisYamlException("Illegal class type for " + yamlObjectName + " {Class type requested: " 
                    + yamlObjectType + " ; real class type: " + foundedObject.getClass() + "}");
        }
    }

    public <T extends IYamlObject> T retrieveAnyObject(YamlPath pathToObject, Class<T> yamlObjectType) {
        IYamlObject lastObject = rootObjects.get(pathToObject.getFirstPathValue());
        for(String objectName : pathToObject.retrieveParsedPathAsArray()) {
            if(lastObject == null) break;
            IYamlObject currentObject = lastObject.getChild(objectName);
            if(currentObject == null) break;
            lastObject = currentObject;
        }
        if(lastObject == null) return null;
        if(!lastObject.getClass().isAssignableFrom(yamlObjectType)) {
            throw new ErisYamlException("The requested value was not the right type of value ! " +
                    "{Requested=" + yamlObjectType + ";Given=" + lastObject.getClass() + "}");
        }
        return yamlObjectType.cast(lastObject);
    }

    public IYamlObject retrieveAnyObject(YamlPath pathToObject) {
        return retrieveAnyObject(pathToObject, IYamlObject.class);
    }

    public IYamlObject retrieveObject(String yamlObjectName) {
        return retrieveObject(yamlObjectName, IYamlObject.class);
    }

    public void set(String path, Object value) {
        set(YamlPath.fromGlobalPath(path), value);
    }

    public void set(YamlPath path, Object value) {
        IYamlObject currentObject;

        if(rootObjects.containsKey(path.getFirstPathValue()))
            currentObject = retrieveObject(path.getFirstPathValue());
        else {
            currentObject = YamlNode.buildEmptyNode(path.getFirstPathValue());
            addRootObject(currentObject);
        }


        for (String currentNodeName : path.getWholePathExceptFirstValue()) {
            if(currentObject.hasChild(currentNodeName)) {
                currentObject = currentObject.getChild(currentNodeName);
            } else {
                IYamlObject newObject = YamlNode.buildEmptyNode(currentNodeName);
                currentObject.addChildren(newObject);
                currentObject = newObject;
            }
        }
        if(currentObject == null)
            throw new ErisYamlException("Cannot add children to a object that is null");
        YamlUtils.setValueToYamlObject(currentObject, value);
    }
}
