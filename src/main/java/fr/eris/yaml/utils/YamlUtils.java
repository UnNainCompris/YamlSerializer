package fr.eris.yaml.utils;

import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.YamlObjectImpl;
import fr.eris.yaml.object.node.YamlNodeImpl;
import fr.eris.yaml.object.node.iterable.list.YamlListNodeImpl;
import fr.eris.yaml.object.node.iterable.set.YamlSetNodeImpl;
import fr.eris.yaml.object.node.map.YamlMapImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class YamlUtils {

    public static void setValueToYamlObject(YamlObjectImpl object, Object valueToSet) {
        Class<? extends YamlObjectImpl> clazz = object.getClass();
        if(YamlListNodeImpl.class.isAssignableFrom(clazz)) {
            if(((List<?>) valueToSet).isEmpty()) return;
            List<YamlObjectImpl> listValue = new ArrayList<>();

            if(YamlObjectImpl.class.isAssignableFrom(((List<?>) valueToSet).get(0).getClass())) {
                listValue = (List<YamlObjectImpl>) valueToSet;
            } else {
                for (Object listObject : (List<?>) valueToSet)
                    listValue.add(new YamlNodeImpl<>(String.valueOf(((List<?>) valueToSet).indexOf(listObject) + 1), listObject));
            }
            if(valueToSet instanceof List)
                (YamlListNodeImpl.class.cast(object)).set(listValue);
            else throw new ErisYamlException("You cannot set other type of element to a YamlListNode than a list !");
        } else if(YamlSetNodeImpl.class.isAssignableFrom(clazz)) {
            if(valueToSet instanceof Set)
                (YamlSetNodeImpl.class.cast(object)).set((Set<?>) valueToSet);
            else throw new ErisYamlException("You cannot set other type of element to a YamlSetNode than a set !");
        } else if(YamlMapImpl.class.isAssignableFrom(clazz)) {
            if(valueToSet instanceof Map)
                throw new ErisYamlException("Not implemented yet");
            else throw new ErisYamlException("You cannot set other type of element to a YamlMapNode than a Map !");
        } else if(YamlNodeImpl.class.isAssignableFrom(clazz)) {
            YamlNodeImpl.class.cast(object).setValue(valueToSet);
        }
    }

    public static YamlObjectImpl createNodeWithDefaultValue(String nodeName, Object object) {
        YamlObjectImpl objectToReturn;
        if(object instanceof List) {
            objectToReturn = new YamlListNodeImpl<>(nodeName);
            (YamlListNodeImpl.class.cast(object)).set((List<?>) object);
        } else if(object instanceof Set) {
            objectToReturn = new YamlListNodeImpl<>(nodeName);
            (YamlSetNodeImpl.class.cast(object)).set((Set<?>) object);
        } else if(object instanceof Map) {
            throw new ErisYamlException("Map serialization isn't supported yet");
        } else {
            objectToReturn = new YamlNodeImpl<>(nodeName, object);
        }
        return objectToReturn;
    }

    public static Object getYamlObjectValue(YamlObjectImpl object) {
        if(object instanceof YamlListNodeImpl<?>)
            return ((YamlListNodeImpl<?>) object).get();
        if(object instanceof YamlSetNodeImpl<?>)
            return ((YamlSetNodeImpl<?>) object).get();
        if(object instanceof YamlNodeImpl<?>)
            return ((YamlNodeImpl<?>) object).getValue();
        if(object instanceof YamlMapImpl<?, ?>)
            throw new ErisYamlException("Map aren't implemented yet");

        throw new ErisYamlException("Invalid Yaml object");
    }

}
