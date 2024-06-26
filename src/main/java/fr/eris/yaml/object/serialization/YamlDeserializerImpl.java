package fr.eris.yaml.object.serialization;

import fr.eris.yaml.api.Yaml;
import fr.eris.yaml.api.object.YamlDocument;
import fr.eris.yaml.api.object.serializer.YamlDeserializer;
import fr.eris.yaml.api.object.annotation.YamlExpose;
import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.path.YamlPath;
import fr.eris.yaml.object.serialization.deserialization.YamlDeserializationObject;
import fr.eris.yaml.utils.TypeUtils;
import fr.eris.yaml.utils.reflection.ReflectionHelper;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class YamlDeserializerImpl<T> implements YamlDeserializer<T> {

    @Getter private final String serializedData;
    @Getter private final Class<T> requiredObjectClass;
    private T builtClass;

    public YamlDeserializerImpl(String serializedData, T currentExistingObject) {

        this.serializedData = serializedData;
        this.builtClass = currentExistingObject;
        this.requiredObjectClass = (Class<T>)currentExistingObject.getClass();
    }

    public YamlDeserializerImpl(String serializedData, Class<T> requiredObjectClass) {
        try {
            requiredObjectClass.getDeclaredConstructor();
        } catch (NoSuchMethodException exception) {
            throw new ErisYamlException("The deserialized class type need an empty constructor to build ! class{" + requiredObjectClass + "}");
        }

        this.serializedData = serializedData;
        this.requiredObjectClass = requiredObjectClass;
    }

    public T buildObject() {
        LinkedHashMap<YamlPath, String> serializedValue = Yaml.getYaml().getYamlParser().parseYamlContent(serializedData);

        LinkedHashMap<YamlPath, YamlDeserializationObject> map = new LinkedHashMap<>();
        if(builtClass == null)
            builtClass = new ReflectionHelper<>(requiredObjectClass).buildClass();

        List<YamlPath> pathToThing = new ArrayList<>(serializedValue.keySet());
        pathToThing.sort(Comparator.comparingInt(o -> o.retrieveParsedPathAsArray().length));
        Collections.reverse(pathToThing);

        YamlPath skipTillPath = null;
        outerFor:
        for(YamlPath path : pathToThing) {
            if(skipTillPath != null && !path.equals(skipTillPath)) continue;
            else skipTillPath = null;
            YamlPath lastPath = null;
            for(YamlPath splitPath : splitPath(path)) {
                if(map.containsKey(splitPath.clone())) {
                    lastPath = splitPath;
                    continue;
                }

                YamlDeserializationObject lastObject = map.get(lastPath);
                if(lastObject == null)
                    lastObject = YamlDeserializationObject.build(null, builtClass, null);

                if(lastObject.getAssosiatedField() != null &&
                        Collection.class.isAssignableFrom(lastObject.getAssosiatedField().getType())) {
                    int index = -1;
                    try {
                        index = Integer.parseInt(splitPath.getLastPathValue());
                    } catch (Exception ignored) {}

                    if(lastObject.hasObjectListIndex(index) || index == -1) continue;

                    YamlDocument document = Yaml.getYaml().createEmptyDocument();

                    for(Map.Entry<YamlPath, String> entry : serializedValue.entrySet()) {
                        YamlPath key = entry.getKey();
                        if(!key.startWith(splitPath)) continue;
                        document.set(YamlPath.fromGlobalPath(key.getTargetPath().replace(splitPath.getTargetPath() +
                                YamlPath.YAML_PATH_SEPARATOR, "")), serializedValue.get(key));
                    }

                    Class<?> classType = ((Class<?>) ((ParameterizedType) lastObject.getAssosiatedField().getGenericType())
                            .getActualTypeArguments()[0]);
                    Object toSet = null;
                    if(TypeUtils.isNativeClass(classType)) {
                        toSet = Yaml.getYaml().getYamlValueParser().parseValue(serializedValue.get(splitPath), classType);
                    } else {
                        toSet = new YamlDeserializerImpl<>(document.serialize(), classType).buildObject();
                    }
                    lastObject.setObjectListValue(index, toSet);
                    continue outerFor;
                }

                Object whereToSearchObject = lastObject.getAssosiatedField() != null ? lastObject.getFieldValue() : builtClass;
                Class<?> whereToSearch = lastObject.getAssosiatedField() != null ? lastObject.getAssosiatedField().getType() : builtClass.getClass();
                Field newField = null;
                for(Field field : new ReflectionHelper<>(whereToSearch).findFieldWithAnnotation(YamlExpose.class)) {
                    YamlExpose yamlExpose = field.getDeclaredAnnotation(YamlExpose.class);
                    if(!yamlExpose.yamlSaveName().equals(splitPath.getLastPathValue())) continue;
                    if(!yamlExpose.useDefaultValue()) {
                        field.setAccessible(true);
                        try {
                            if(TypeUtils.isNativeClass(field.getType())) {
                                field.set(whereToSearchObject, TypeUtils.getDefaultNativeValue(field.getType()));
                            } else {
                                field.set(whereToSearchObject, null);
                            }
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    newField = field;
                }

                if(newField == null) {
                    continue;
                }

                Object parentObject;
                if(lastObject.getAssosiatedField() != null)
                    parentObject = lastObject.getFieldValue();
                else parentObject = builtClass;

                YamlDeserializationObject newObject = YamlDeserializationObject.build(newField, parentObject, splitPath);

                try {
                    newObject.setFieldValue(TypeUtils.buildObjectFromField(newField));
                } catch (Exception ignored) {
                    newObject.setFieldValue(Yaml.getYaml().getYamlValueParser().parseValue(serializedValue.get(splitPath), newField.getType()));
                    map.put(splitPath.clone(), newObject);
                    break;
                }

                map.put(splitPath.clone(), newObject);

                lastPath = splitPath;
            }
        }

        for(YamlDeserializationObject deserializationObject : map.values()) {
            deserializationObject.doCollectionThings();
        }
        return builtClass;
    }

    public List<YamlPath> splitPath(YamlPath path) {
        List<YamlPath> newPath = new ArrayList<>();
        List<String> splitPath = new ArrayList<>();

        LinkedHashMap<Integer, UUID> indexIdMap = new LinkedHashMap<>();
        int temp = 0;
        for(String actualPath : path.retrieveParsedPathAsArray()) {
            UUID uuid = UUID.randomUUID();
            splitPath.add(actualPath + uuid);
            indexIdMap.put(temp, uuid);
            temp++;
        }

        List<String> newPathBuffer = new ArrayList<>();

        for(int i = 0 ; i < splitPath.size() ; i++)
            newPathBuffer.add("");

        for(String actualPath : splitPath) {
            for (int i = 0; i < splitPath.size(); i++) {
                if (i < splitPath.indexOf(actualPath)) continue;
                String toAdd = actualPath;
                for(UUID value : indexIdMap.values())
                    toAdd = toAdd.replace(value.toString(), "");
                newPathBuffer.set(i, newPathBuffer.get(i) + (newPathBuffer.get(i).isEmpty() ? "" : YamlPath.YAML_PATH_SEPARATOR) + toAdd);
            }
        }

        for(String builtPath : newPathBuffer)
            newPath.add(YamlPath.fromGlobalPath(builtPath));

        return newPath;
    }
}
