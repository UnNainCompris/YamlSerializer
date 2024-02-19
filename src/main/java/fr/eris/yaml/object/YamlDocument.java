package fr.eris.yaml.object;

import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.IYamlObject;

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

    public static <T> YamlDocument generateFromClass(T yamlClass) {
        return null;
    }
    
    public static <T extends YamlClass> YamlDocument generateFromYamlClass(T yamlClass) {
        return null;
    }

    public String serialize() {
        return "";
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

    public IYamlObject retrieveObject(String yamlObjectName) {
        return retrieveObject(yamlObjectName, IYamlObject.class);
    }

}
