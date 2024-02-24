package fr.eris.yaml.object.serialization;

import fr.eris.yaml.object.YamlDocument;
import fr.eris.yaml.object.annotation.YamlExpose;
import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.IYamlObject;
import fr.eris.yaml.object.node.YamlNode;
import fr.eris.yaml.object.node.iterable.list.YamlListNode;
import fr.eris.yaml.object.node.iterable.set.YamlSetNode;
import fr.eris.yaml.object.node.map.YamlMap;
import fr.eris.yaml.utils.TypeUtils;
import fr.eris.yaml.utils.reflection.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class YamlSerializer<T> {

    private final T objectToSerialize;
    private final Class<?> objectClass;
    private final ReflectionHelper reflectionHelper;

    private YamlDocument serializedDocument;

    public YamlSerializer(T objectToSerialize) {
        this.objectToSerialize = objectToSerialize;
        this.objectClass = objectToSerialize.getClass();
        this.reflectionHelper = new ReflectionHelper(objectClass);
    }

    public static YamlDocument serializeData(String serializedData) {
        return null;
    }

    public YamlDocument serialize() {
        serializedDocument = new YamlDocument();

        build();

        return serializedDocument;
    }

    public void build() {
        for(Field field : getSavableField()) {
            IYamlObject newRoot = buildYamlObjectFromField(field, objectToSerialize);
            if(newRoot == null) continue;
            serializedDocument.addRootObject(newRoot);
        }
    }

    public IYamlObject buildYamlObjectFromField(Field currentField, Object parentFieldObject) {
        IYamlObject newObject = null;
        YamlExpose exposeAnnotation = currentField.getDeclaredAnnotation(YamlExpose.class);
        String exposeName = exposeAnnotation.yamlSaveName();
        boolean serializeEvenIfNull = exposeAnnotation.serializeEvenIfNull();
        if(exposeName == null || exposeName.isEmpty())
            exposeName = currentField.getName();
        try {
            currentField.setAccessible(true);
            Object fieldValue = currentField.get(parentFieldObject);
            if (fieldValue == null) {
                if(serializeEvenIfNull)
                    return new YamlNode<>(exposeName, null);
                return null;
            }

            if(!TypeUtils.isAnYamlSupportedType(currentField.getType())) {
                ReflectionHelper currentHelper = new ReflectionHelper(parentFieldObject);
                newObject = YamlNode.buildEmptyNode(exposeName);
                for(Field field : currentHelper.findFieldWithAnnotation(YamlExpose.class)) {
                    IYamlObject newChild = buildYamlObjectFromField(field, fieldValue);
                    if(newChild == null) continue;
                    newObject.addChildren(newChild);
                }
            }

            else if(currentField.getType().isAssignableFrom(List.class)
                    || currentField.getType().isArray()) {
                newObject = new YamlListNode<>(exposeName);
                List<Object> fieldListContent = currentField.getType().isArray() ?
                        Arrays.asList((Object[]) fieldValue) : ((List<Object>) fieldValue);
                for(Object object : fieldListContent) {
                    ReflectionHelper currentHelper = new ReflectionHelper(object);
                    IYamlObject newListElement = getYamlClassFromNativeType(object.getClass())
                            .getDeclaredConstructor(String.class).newInstance(String.valueOf(fieldListContent.indexOf(object)));
                    setValueToYamlObject(newListElement, object);
                    for(Field field : currentHelper.findFieldWithAnnotation(YamlExpose.class)) {
                        newListElement.addChildren(buildYamlObjectFromField(field, object));
                    }
                    ((YamlListNode<IYamlObject>) newObject).add(newListElement);
                }
            } else if(currentField.getType().isAssignableFrom(Set.class)) {
                // set node
            } else if(currentField.getType().isAssignableFrom(Map.class)) {
                // map node
            } else if(TypeUtils.isNativeObject(fieldValue)) {
                newObject = new YamlNode<>(exposeName, fieldValue);
            } else {
                throw new ErisYamlException("Illegal argument type !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(newObject == null) {
            throw new ErisYamlException("Error while parsing !");
        }
        return newObject;
    }

    public Class<? extends IYamlObject> getYamlClassFromNativeType(Class<?> clazz) {
        if(List.class.isAssignableFrom(clazz)) {
            return YamlListNode.class;
        } else if(Set.class.isAssignableFrom(clazz)) {
            return YamlSetNode.class;
        } else if(Map.class.isAssignableFrom(clazz)) {
            return null; //return YamlMap.class;
        } else if(TypeUtils.isNativeClass(clazz)) {
            return YamlNode.class;
        }
        return null;
    }

    public void setValueToYamlObject(IYamlObject object, Object valueToSet) {
        Class<? extends IYamlObject> clazz = object.getClass();
        if(YamlListNode.class.isAssignableFrom(clazz)) {
            if(valueToSet instanceof List)
                (YamlListNode.class.cast(object)).set((List<?>) valueToSet);
            else throw new ErisYamlException("You cannot set other type of element to a YamlListNode than a list !");
            return;
        } else if(YamlSetNode.class.isAssignableFrom(clazz)) {
            if(valueToSet instanceof Set)
                (YamlSetNode.class.cast(object)).set((Set<?>) valueToSet);
            else throw new ErisYamlException("You cannot set other type of element to a YamlSetNode than a set !");
            return;
        } else if(YamlMap.class.isAssignableFrom(clazz)) {

            return;
        } else if(YamlNode.class.isAssignableFrom(clazz)) {
            YamlNode.class.cast(object).setValue(valueToSet);
            return;
        }
    }

    public List<Field> getSavableField() {
        return reflectionHelper.findFieldWithAnnotation(YamlExpose.class);
    }

}
