package fr.eris.yaml.object;

import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.IYamlObject;
import fr.eris.yaml.object.path.YamlPath;
import fr.eris.yaml.object.serialization.YamlSerializer;

import java.util.HashMap;

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

    public static YamlDocument generateFromSerializedData(String serializedData) {
        return null;
    }

    public static <T> YamlDocument generateFromClass(T clazz) {
        return new YamlSerializer<>(clazz).serialize();
    }
    
    public static <T extends YamlClass> YamlDocument generateFromYamlClass(T yamlClass) {
        return YamlSerializer.serializeData(yamlClass.serializeClass());
    }

    public String serialize() {
        StringBuilder serializedDocument = new StringBuilder();

        for(IYamlObject rootObject : rootObjects.values()) {
            serializedDocument.append(rootObject.serialize(0)).append("\n");
        }

        return serializedDocument.toString();
    }

    public <T> T deserialize(Class<T> deserializationClassType) {
        return null;
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
            lastObject = lastObject.getChild(objectName);
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

}
