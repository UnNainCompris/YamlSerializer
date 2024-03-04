package fr.eris.yaml.utils;

import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.IYamlObject;
import fr.eris.yaml.object.node.YamlNode;
import fr.eris.yaml.object.node.iterable.list.YamlListNode;
import fr.eris.yaml.object.node.iterable.set.YamlSetNode;
import fr.eris.yaml.object.node.map.YamlMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class YamlUtils {

    public static void setValueToYamlObject(IYamlObject object, Object valueToSet) {
        Class<? extends IYamlObject> clazz = object.getClass();
        if(YamlListNode.class.isAssignableFrom(clazz)) {
            if(((List<?>) valueToSet).isEmpty()) return;
            List<IYamlObject> listValue = new ArrayList<>();

            if(IYamlObject.class.isAssignableFrom(((List<?>) valueToSet).get(0).getClass())) {
                listValue = (List<IYamlObject>) valueToSet;
            } else {
                for (Object listObject : (List<?>) valueToSet)
                    listValue.add(new YamlNode<>(String.valueOf(((List<?>) valueToSet).indexOf(listObject) + 1), listObject));
            }
            if(valueToSet instanceof List)
                (YamlListNode.class.cast(object)).set(listValue);
            else throw new ErisYamlException("You cannot set other type of element to a YamlListNode than a list !");
        } else if(YamlSetNode.class.isAssignableFrom(clazz)) {
            if(valueToSet instanceof Set)
                (YamlSetNode.class.cast(object)).set((Set<?>) valueToSet);
            else throw new ErisYamlException("You cannot set other type of element to a YamlSetNode than a set !");
        } else if(YamlMap.class.isAssignableFrom(clazz)) {
            if(valueToSet instanceof Map)
                throw new ErisYamlException("Not implemented yet");
            else throw new ErisYamlException("You cannot set other type of element to a YamlMapNode than a Map !");
        } else if(YamlNode.class.isAssignableFrom(clazz)) {
            YamlNode.class.cast(object).setValue(valueToSet);
        }
    }

    public static IYamlObject createNodeWithDefaultValue(String nodeName, Object object) {
        IYamlObject objectToReturn;
        if(object instanceof List) {
            objectToReturn = new YamlListNode<>(nodeName);
            (YamlListNode.class.cast(object)).set((List<?>) object);
        } else if(object instanceof Set) {
            objectToReturn = new YamlListNode<>(nodeName);
            (YamlSetNode.class.cast(object)).set((Set<?>) object);
        } else if(object instanceof Map) {
            throw new ErisYamlException("Map serialization isn't supported yet");
        } else {
            objectToReturn = new YamlNode<>(nodeName, object);
        }
        return objectToReturn;
    }

}
