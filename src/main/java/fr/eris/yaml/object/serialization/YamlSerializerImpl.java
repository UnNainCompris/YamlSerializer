package fr.eris.yaml.object.serialization;

import fr.eris.yaml.api.object.serializer.YamlSerializer;
import fr.eris.yaml.object.YamlDocumentImpl;
import fr.eris.yaml.object.annotation.YamlExpose;
import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.IYamlObject;
import fr.eris.yaml.object.node.YamlNode;
import fr.eris.yaml.object.node.iterable.list.YamlListNode;
import fr.eris.yaml.object.node.iterable.set.YamlSetNode;
import fr.eris.yaml.utils.TypeUtils;
import fr.eris.yaml.utils.YamlUtils;
import fr.eris.yaml.utils.reflection.ReflectionHelper;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class YamlSerializerImpl<T> implements YamlSerializer<T> {

    @Getter private final T serializedObject;
    @Getter private final Class<T> serializedObjectClass;
    private final ReflectionHelper<T> reflectionHelper;

    private YamlDocumentImpl serializedDocument;

    public YamlSerializerImpl(T serializedObject) {
        this.serializedObject = serializedObject;
        this.serializedObjectClass = (Class<T>) serializedObject.getClass();
        this.reflectionHelper = new ReflectionHelper<>(serializedObjectClass);
    }

    public YamlDocumentImpl serialize() {
        serializedDocument = new YamlDocumentImpl();

        build();

        return serializedDocument;
    }

    public void build() {
        for(Field field : getSavableField()) {
            validateNewField(field);
            IYamlObject newRoot = buildYamlObjectFromField(field, serializedObject);
            if(newRoot == null) continue;
            serializedDocument.addRootObject(newRoot);
        }
    }

    public void validateNewField(Field fieldToValidate) {
        if(fieldToValidate == null)
            throw new ErisYamlException("Field cannot be null !");
        if(Modifier.isFinal(fieldToValidate.getModifiers()))
            throw new ErisYamlException("Cannot serialize a final field ! {fieldName:" + fieldToValidate.getName() + "}");
        try {
            if(!TypeUtils.isAnYamlSupportedType(fieldToValidate.getType()))
                fieldToValidate.getType().getDeclaredConstructor();
        } catch (NoSuchMethodException exception) {
            throw new ErisYamlException("Cannot serialize a field without an empty constructor to build ! {fieldName:" + fieldToValidate.getName() + "}");
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
                ReflectionHelper<?> currentHelper = new ReflectionHelper<>(currentField.getType(), fieldValue);
                newObject = YamlNode.buildEmptyNode(exposeName);
                for(Field field : currentHelper.findFieldWithAnnotation(YamlExpose.class)) {
                    IYamlObject newChild = buildYamlObjectFromField(field, fieldValue);
                    if(newChild == null) continue;
                    newObject.addChildren(newChild);
                }
            }

            else if(List.class.isAssignableFrom(currentField.getType())
                    || currentField.getType().isArray()) {
                newObject = new YamlListNode<>(exposeName);
                List<Object> fieldListContent = currentField.getType().isArray() ?
                        Arrays.asList((Object[]) fieldValue) : ((List<Object>) fieldValue);
                for(Object object : fieldListContent) {
                    ReflectionHelper<?> currentHelper = new ReflectionHelper<>(object.getClass(), object);
                    IYamlObject newListElement = TypeUtils.getYamlClassFromNativeType(object.getClass())
                            .getDeclaredConstructor(String.class).newInstance(String.valueOf(fieldListContent.indexOf(object)));
                    YamlUtils.setValueToYamlObject(newListElement, object);
                    for(Field field : currentHelper.findFieldWithAnnotation(YamlExpose.class)) {
                        newListElement.addChildren(buildYamlObjectFromField(field, object));
                    }
                    ((YamlListNode<IYamlObject>) newObject).add(newListElement);
                }
            } else if(Set.class.isAssignableFrom(currentField.getType())) {
                newObject = new YamlSetNode<>(exposeName);
                Set<Object> fieldSetContent = currentField.getType().isArray() ?
                        new HashSet<>(Arrays.asList((Object[]) fieldValue)) : ((Set<Object>) fieldValue);
                for(Object object : fieldSetContent) {
                    ReflectionHelper<?> currentHelper = new ReflectionHelper<>(object.getClass(), object);
                    IYamlObject newSetElement = TypeUtils.getYamlClassFromNativeType(object.getClass())
                            .getDeclaredConstructor(String.class).newInstance("NoNeedName");
                    YamlUtils.setValueToYamlObject(newSetElement, object);
                    for(Field field : currentHelper.findFieldWithAnnotation(YamlExpose.class)) {
                        newSetElement.addChildren(buildYamlObjectFromField(field, object));
                    }
                    ((YamlSetNode<IYamlObject>) newObject).add(newSetElement);
                }
            } else if(Map.class.isAssignableFrom(currentField.getType())) {
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

    public List<Field> getSavableField() {
        return reflectionHelper.findFieldWithAnnotation(YamlExpose.class);
    }

}
