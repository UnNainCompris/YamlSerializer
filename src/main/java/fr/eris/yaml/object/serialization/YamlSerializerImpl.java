package fr.eris.yaml.object.serialization;

import fr.eris.yaml.api.object.serializer.YamlSerializer;
import fr.eris.yaml.object.YamlDocumentImpl;
import fr.eris.yaml.api.object.annotation.YamlExpose;
import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.impl.YamlObjectImpl;
import fr.eris.yaml.object.node.YamlNodeImpl;
import fr.eris.yaml.object.node.iterable.list.YamlListNodeImpl;
import fr.eris.yaml.object.node.iterable.set.YamlSetNodeImpl;
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
            YamlObjectImpl newRoot = buildYamlObjectFromField(field, serializedObject);
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

    public YamlObjectImpl buildYamlObjectFromField(Field currentField, Object parentFieldObject) {
        YamlObjectImpl newObject = null;
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
                    return new YamlNodeImpl<>(exposeName, null);
                return null;
            }

            if(!TypeUtils.isAnYamlSupportedType(currentField.getType())) {
                ReflectionHelper<?> currentHelper = new ReflectionHelper<>(currentField.getType(), fieldValue);
                newObject = YamlNodeImpl.buildEmptyNode(exposeName);
                for(Field field : currentHelper.findFieldWithAnnotation(YamlExpose.class)) {
                    YamlObjectImpl newChild = buildYamlObjectFromField(field, fieldValue);
                    if(newChild == null) continue;
                    newObject.addChildren(newChild);
                }
            }

            else if(List.class.isAssignableFrom(currentField.getType())
                    || currentField.getType().isArray()) {
                newObject = new YamlListNodeImpl<>(exposeName);
                List<Object> fieldListContent = currentField.getType().isArray() ?
                        Arrays.asList((Object[]) fieldValue) : ((List<Object>) fieldValue);
                for(Object object : fieldListContent) {
                    ReflectionHelper<?> currentHelper = new ReflectionHelper<>(object.getClass(), object);
                    YamlObjectImpl newListElement = TypeUtils.getYamlClassFromNativeType(object.getClass())
                            .getDeclaredConstructor(String.class).newInstance(String.valueOf(fieldListContent.indexOf(object)));
                    System.out.println("Current index: " + fieldListContent.indexOf(object));
                    ((YamlListNodeImpl<YamlObjectImpl>) newObject).add(newListElement);
                    for(Field field : currentHelper.findFieldWithAnnotation(YamlExpose.class)) {
                        field.setAccessible(true);
                        if(field.get(object) == null) continue;
                        System.out.println(field.get(object));
                        newListElement.addChildren(buildYamlObjectFromField(field, object));
                        newListElement.setPrefix("");
                    }
                    if(!newListElement.hasAnyChild())
                        YamlUtils.setValueToYamlObject(newListElement, object);

                }
            } else if(Set.class.isAssignableFrom(currentField.getType())) {
                newObject = new YamlSetNodeImpl<>(exposeName);
                Set<Object> fieldSetContent = currentField.getType().isArray() ?
                        new HashSet<>(Arrays.asList((Object[]) fieldValue)) : ((Set<Object>) fieldValue);
                for(Object object : fieldSetContent) {
                    ReflectionHelper<?> currentHelper = new ReflectionHelper<>(object.getClass(), object);
                    YamlObjectImpl newSetElement = TypeUtils.getYamlClassFromNativeType(object.getClass())
                            .getDeclaredConstructor(String.class).newInstance("NoNeedName");
                    for(Field field : currentHelper.findFieldWithAnnotation(YamlExpose.class)) {
                        field.setAccessible(true);
                        if(field.get(object) == null) continue;
                        System.out.println(field.get(object));
                        newSetElement.addChildren(buildYamlObjectFromField(field, object));
                    }
                    if(!newSetElement.hasAnyChild())
                        YamlUtils.setValueToYamlObject(newSetElement, object);
                    ((YamlSetNodeImpl<YamlObjectImpl>) newObject).add(newSetElement);
                }
            } else if(Map.class.isAssignableFrom(currentField.getType())) {
                // map node
            } else if(TypeUtils.isNativeObject(fieldValue)) {
                newObject = new YamlNodeImpl<>(exposeName, fieldValue);
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
