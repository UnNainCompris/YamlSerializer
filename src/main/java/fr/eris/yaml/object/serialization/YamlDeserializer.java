package fr.eris.yaml.object.serialization;

import fr.eris.objecttest.TestYamlObject;
import fr.eris.yaml.object.YamlDocument;
import fr.eris.yaml.object.annotation.YamlExpose;
import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.node.iterable.list.YamlListNode;
import fr.eris.yaml.object.node.iterable.set.YamlSetNode;
import fr.eris.yaml.object.path.YamlPath;
import fr.eris.yaml.object.serialization.deserialization.YamlDeserializationObject;
import fr.eris.yaml.object.value.YamlValueParser;
import fr.eris.yaml.utils.IndentationUtils;
import fr.eris.yaml.utils.TypeUtils;
import fr.eris.yaml.utils.reflection.ReflectionHelper;
import fr.eris.yaml.utils.reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class YamlDeserializer<T> {

    private final String serializedData;
    private final Class<T> objectClass;
    private final ReflectionHelper<T> reflectionHelper;
    private T builtClass;

    private HashMap<YamlPath, YamlDeserializationObject> deserializationObjectMap;

    public YamlDeserializer(String serializedData, Class<T> objectClass) {
        try {
            objectClass.getDeclaredConstructor();
        } catch (NoSuchMethodException exception) {
            throw new ErisYamlException("The deserialized class type need an empty constructor to build !");
        }

        this.serializedData = serializedData;
        this.objectClass = objectClass;
        this.reflectionHelper = new ReflectionHelper<>(objectClass);
    }

    public Object buildObjectFromField(Field field) {
        if(List.class.isAssignableFrom(field.getType()))
            return new ArrayList<>();
        else if(Set.class.isAssignableFrom(field.getType()))
            return new HashSet<>();
        else if(Map.class.isAssignableFrom(field.getType()))
            throw new ErisYamlException("Map is not handled yet !");
        else return new ReflectionHelper<>(field.getType()).buildClass();
    }

    public T buildObject() {
        HashMap<YamlPath, String> serializedValue = retrieveSerializedValue();
        HashMap<YamlPath, YamlDeserializationObject> pathToField = retrievePathToField(serializedValue);

        //System.out.println(pathToField.toString().replace(", ", "\n"));

        return buildClass(pathToField);
    }

    private T buildClass(HashMap<YamlPath, YamlDeserializationObject> pathToField) {
        return null;
    }

    public HashMap<YamlPath, YamlDeserializationObject> retrievePathToField(HashMap<YamlPath, String> requestedPath) {

        HashMap<YamlPath, YamlDeserializationObject> map = new HashMap<>();
        builtClass = new ReflectionHelper<>(objectClass).buildClass();

        List<YamlPath> pathToThing = new ArrayList<>(requestedPath.keySet());
        pathToThing.sort(Comparator.comparingInt(o -> o.retrieveParsedPathAsArray().length));
        Collections.reverse(pathToThing);

        for(YamlPath path : pathToThing) {
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
                    lastObject.setObjectListValue(Integer.parseInt(splitPath.getLastPathValue()), requestedPath.get(splitPath));
                    continue;
                }


                Class<?> whereToSearch = lastObject.getAssosiatedField() != null ? lastObject.getAssosiatedField().getType() : builtClass.getClass();
                Field newField = null;
                for(Field field : new ReflectionHelper<>(whereToSearch).findFieldWithAnnotation(YamlExpose.class)) {
                    if(!field.getDeclaredAnnotation(YamlExpose.class).yamlSaveName().equals(splitPath.getLastPathValue())) continue;
                    newField = field;
                }

                if(newField == null)
                    throw new ErisYamlException("T'a le seum");

                Object parentObject = null;
                if(lastObject.getAssosiatedField() != null)
                    parentObject = lastObject.getFieldValue();
                else parentObject = builtClass;

                YamlDeserializationObject newObject = YamlDeserializationObject.build(newField, parentObject, splitPath);

                try {
                    newObject.setFieldValue(buildObjectFromField(newField));
                } catch (Exception ignored) {
                    newObject.setFieldValue(YamlValueParser.parseValue(requestedPath.get(splitPath), newField.getType()));
                    map.put(splitPath.clone(), newObject);
                    break;
                }

                map.put(splitPath.clone(), newObject);
                System.out.println(splitPath);

                lastPath = splitPath;
            }
        }

        for(YamlDeserializationObject deserializationObject : map.values()) {
            deserializationObject.doCollectionThings();
        }

        System.out.println("---- " + ((TestYamlObject)builtClass).getTestInnerClass().getTestInnerClass().getTestFieldFirst());
        System.out.println("---- " + ((TestYamlObject)builtClass).getTestInnerClass().getTestInnerClass().getTestFieldSecond());

        System.out.println("---- " + ((TestYamlObject)builtClass).getTestListFieldInteger());
        System.out.println("---- " + ((TestYamlObject)builtClass).getTestInnerClass().getTestListFieldInteger());
        return map;
    }

    public List<YamlPath> splitPath(YamlPath path) {
        List<YamlPath> newPath = new ArrayList<>();

        List<String> splitPath = new ArrayList<>();

        HashMap<Integer, UUID> indexIdMap = new HashMap<>();
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

        //System.out.println(newPathBuffer);
        //System.out.println(splitPath);
        //System.out.println(path);

        for(String builtPath : newPathBuffer)
            newPath.add(YamlPath.fromGlobalPath(builtPath));

        return newPath;
    }

    public Field findFieldFromPath(YamlPath pathToFollow, Class<?> startingClass) {
        return null;
    }

    public HashMap<YamlPath, String> retrieveSerializedValue() {
        HashMap<YamlPath, String> serializedValue = new HashMap<>();
        List<String> content = new ArrayList<>(Arrays.asList(serializedData.split("\n")));

        List<String> currentPath = new ArrayList<>();
        for(int currentLineIndex = 0 ; currentLineIndex < content.size() ; currentLineIndex++) {
            String currentLine = content.get(currentLineIndex);
            currentPath.add(findYamlLineName(currentLine));

            if(!isNextLineAnInnerYamlObject(content, currentLineIndex)) {
                serializedValue.put(buildYamlPath(currentPath), findYamlLineValue(currentLine));
                if(hasNextLine(content, currentLineIndex)) {
                    int indentationDifference =
                            IndentationUtils.retrieveIndentationLevel(currentLine) -
                            IndentationUtils.retrieveIndentationLevel(content.get(currentLineIndex + 1)) + 1;
                    for (int i = 0; i < indentationDifference; i++) {
                        currentPath.remove(currentPath.size() - 1);
                    }
                }
            }
        }
        return serializedValue;
    }

    public YamlPath buildYamlPath(List<String> currentPath) {
        StringBuilder yamlPath = new StringBuilder();
        for(String string : currentPath)
            yamlPath.append(string).append(".");

        int lastDotIndex;
        while((lastDotIndex = yamlPath.lastIndexOf(".")) == yamlPath.length() - 1) {
            //if (lastDotIndex >= 0 && lastDotIndex < yamlPath.length())
                yamlPath.deleteCharAt(lastDotIndex);
            //else break;
        }

        return YamlPath.fromGlobalPath(yamlPath.toString());
    }

    private int actualListIndex = 0;
    public String findYamlLineName(String fullLine) {
        fullLine = IndentationUtils.removeIndentation(fullLine);
        if(fullLine.contains(": ")) {
            actualListIndex = 0;
            return fullLine.split(": ")[0].trim();
        } else if(fullLine.startsWith(YamlSetNode.ELEMENT_PREFIX)) {
            actualListIndex++;
            return Integer.toString(actualListIndex);
        } else if(fullLine.startsWith(YamlListNode.ELEMENT_PREFIX)) {
            actualListIndex++;
            return Integer.toString(actualListIndex);
        } else {
            actualListIndex = 0;
            return fullLine;
        }
    }

    public String findYamlLineValue(String fullLine) {
        fullLine = IndentationUtils.removeIndentation(fullLine);
        if(fullLine.contains(": ")) {
            return fullLine.split(": ")[1];
        } else if(fullLine.startsWith(YamlSetNode.ELEMENT_PREFIX)) {
            return fullLine.replaceFirst(YamlSetNode.ELEMENT_PREFIX, "");
        } else if(fullLine.startsWith(YamlListNode.ELEMENT_PREFIX)) {
            return fullLine.replaceFirst(YamlListNode.ELEMENT_PREFIX, "");
        } else {
            return fullLine;
        }
    }

    public boolean isNextLineAnInnerYamlObject(List<String> content, int currentLine) {
        if(!hasNextLine(content, currentLine)) return false;
        int currentIndentationLevel = IndentationUtils.retrieveIndentationLevel(content.get(currentLine));
        return currentIndentationLevel < IndentationUtils.retrieveIndentationLevel(content.get(currentLine + 1));
    }

    public boolean hasNextLine(List<String> content, int currentLine) {
        return content.size() - 1 > currentLine;
    }
}
