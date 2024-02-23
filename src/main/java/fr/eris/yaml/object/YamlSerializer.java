package fr.eris.yaml.object;

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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class YamlSerializer<T> {

    private final T objectToSerialize;
    private final Class<?> objectClass;
    private final ReflectionHelper reflectionHelper;
    public final int SERIALIZATION_SEARCH_DEPTH = -1;

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
        String exposeName = currentField.getDeclaredAnnotation(YamlExpose.class).yamlSaveName();
        try {
            Object fieldValue = currentField.get(parentFieldObject);
            if (fieldValue == null) return null;

            if(!TypeUtils.isAnYamlSupportedType(currentField.getType())) {
                ReflectionHelper currentHelper = new ReflectionHelper(parentFieldObject);
                newObject = YamlNode.buildEmptyNode(exposeName);
                for(Field field : currentHelper.findFieldWithAnnotation(YamlExpose.class))
                    newObject.addChildren(buildYamlObjectFromField(field, fieldValue));
            }

            else if(currentField.getType().isAssignableFrom(List.class)
                    || currentField.getType().isArray()) {
                newObject = new YamlListNode<>(exposeName);
                Object[] fieldListContent = currentField.getType().isArray() ?
                        (Object[]) fieldValue : ((List<?>) fieldValue).toArray();
                for(Object object : fieldListContent) {
                    ReflectionHelper currentHelper = new ReflectionHelper(object);
                    IYamlObject newListElement = getYamlClassFromNativeType(object.getClass())
                            .getDeclaredConstructor(String.class).newInstance("ElementList");
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
        if(clazz.isAssignableFrom(List.class)) {
            return YamlListNode.class;
        } else if(clazz.isAssignableFrom(Set.class)) {
            return YamlSetNode.class;
        } else if(clazz.isAssignableFrom(Map.class)) {
            return null; //return YamlMap.class;
        } else if(TypeUtils.isNativeClass(clazz)) {
            return YamlNode.class;
        }
        return null;
    }

    public IYamlObject buildYamlListObject(YamlListNode<?> parentList, Object listObject) {

    }

    public List<Field> getSavableField() {
        return reflectionHelper.findFieldWithAnnotation(YamlExpose.class);
    }

}
