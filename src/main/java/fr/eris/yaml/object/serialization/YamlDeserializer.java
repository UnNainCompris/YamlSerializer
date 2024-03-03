package fr.eris.yaml.object.serialization;

import fr.eris.yaml.object.annotation.YamlExpose;
import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.node.iterable.list.YamlListNode;
import fr.eris.yaml.object.node.iterable.set.YamlSetNode;
import fr.eris.yaml.object.path.YamlPath;
import fr.eris.yaml.utils.IndentationUtils;
import fr.eris.yaml.utils.TypeUtils;
import fr.eris.yaml.utils.reflection.ReflectionHelper;
import fr.eris.yaml.utils.storage.Tuple;

import java.lang.reflect.Field;
import java.util.*;

public class YamlDeserializer<T> {

    private final String serializedData;
    private final Class<T> objectClass;
    private final ReflectionHelper<T> reflectionHelper;
    private T builtClass;

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

    public T retrieveClass() {
        HashMap<YamlPath, String> serializedValue = retrieveSerializedValue();
        return buildClassObject(serializedValue);
    }

    private T buildClassObject(HashMap<YamlPath, String> serializedValue) {
        builtClass = reflectionHelper.buildClass();
        HashMap<YamlPath, Tuple<Field, Object>> yamlPathToField = buildYamlPathToField(serializedValue.keySet());

        for(YamlPath path : serializedValue.keySet()) {
            Tuple<Field, Object> tuple = yamlPathToField.get(path);
            if(tuple == null) {
                System.out.println(path + " -- null");
                continue;
            }
            Field field = tuple.getA();
            System.out.println(path + " -- " + field.getName() + " -- " + tuple.getB().getClass().getCanonicalName());
        }

        System.out.println("\n\n\n\n\n");

        for(YamlPath path : yamlPathToField.keySet()) {
            Tuple<Field, Object> tuple = yamlPathToField.get(path);
            if(tuple == null) {
                System.out.println(path + " -- null");
                continue;
            }
            Field field = tuple.getA();
            System.out.println(path + " -- " + field.getName() + " -- " + tuple.getB().getClass().getCanonicalName());
        }

        applyValueToField(yamlPathToField, serializedValue);
        // use the deserializedDocument
        return builtClass;
    }

    private void applyValueToField(HashMap<YamlPath, Tuple<Field, Object>> yamlPathToField,
                                   HashMap<YamlPath, String> serializedValue) {
        for(YamlPath path : serializedValue.keySet()) {
            Tuple<Field, Object> tuple = yamlPathToField.get(path);
            if (tuple == null)
                continue;
            try {
                Field field = tuple.getA();
                field.setAccessible(true);
                if(Collection.class.isAssignableFrom(field.getType()) || field.getType().isArray()) {
                    field.set(tuple.getB(), buildObjectFromField(field));
                } else if(TypeUtils.isNativeClass(field.getType())) {
                    field.set(tuple.getB(), serializedValue.get(path));
                } else {
                    System.out.println("j'ai plus d'idée de sysout");
                }
            } catch (Exception e) {
                throw new ErisYamlException("Error here lmao x))))) " + e.getMessage());
            }
        }
    }

    public HashMap<YamlPath, Tuple<Field, Object>> buildYamlPathToField(Collection<YamlPath> requiredPath) {
        HashMap<YamlPath, Tuple<Field, Object>> yamlPathToField = new HashMap<>();
        for(YamlPath path : requiredPath)
            createFieldObjectTuple(path, yamlPathToField);

        //starting from parent as "builtClass"
        return yamlPathToField;
    }

    public Tuple<Field, Object> createFieldObjectTuple(YamlPath path, HashMap<YamlPath, Tuple<Field, Object>> yamlPathToField) {
        Field foundedField = null;
        Object currentObject = builtClass;
        List<String> pathAsList = Arrays.asList(path.retrieveParsedPathAsArray());

        if(pathAsList.isEmpty()) return null;

        YamlPath actualPath = null;
        for(String currentPathValue : pathAsList) {
            if(actualPath == null) actualPath = YamlPath.fromGlobalPath(pathAsList.get(0));
            else actualPath.append(currentPathValue);

            ReflectionHelper<?> reflectionHelper = new ReflectionHelper<>(currentObject.getClass(), currentObject);
            List<Field> savableFields = reflectionHelper.findFieldWithAnnotation(YamlExpose.class);

            if(savableFields.isEmpty())
                return null;

            for(Field currentField : savableFields) {
                YamlExpose exposeAnnotation = currentField.getAnnotation(YamlExpose.class);
                String exposeSaveName = exposeAnnotation.yamlSaveName();
                if(currentPathValue.equals(exposeSaveName)) {
                    if((Collection.class.isAssignableFrom(currentField.getType()) || currentField.getType().isArray())
                            && pathAsList.indexOf(currentPathValue) == pathAsList.size() - 2) {
                        foundedField = currentField;
                        break;
                    }
                    foundedField = currentField;
                    break;
                }
            }
            if(foundedField == null)
                throw new ErisYamlException("Unable field null blah blah blah");

            if(!yamlPathToField.containsKey(actualPath))
                yamlPathToField.put(actualPath, new Tuple<>(foundedField, currentObject));
            if(Collection.class.isAssignableFrom(currentObject.getClass()) || currentObject.getClass().isArray())
                break;
            currentObject = buildObjectFromField(foundedField);
        }
        return new Tuple<>(foundedField, currentObject);
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
    
    private Field findFieldFromYamlPath(YamlPath path, Object startObject) {
        Field foundedField = null;
        Object currentObject = startObject;
        String currentPathTarget = path.getPathValueFromIndex(0);

        while(true) {
            ReflectionHelper<?> reflectionHelper = new ReflectionHelper<>(startObject.getClass(), currentObject);
            List<Field> savableFields = reflectionHelper.findFieldWithAnnotation(YamlExpose.class);
            for(Field currentField : savableFields) {
                YamlExpose exposeAnnotation = currentField.getAnnotation(YamlExpose.class);
                String exposeSaveName = exposeAnnotation.yamlSaveName();
                if(currentPathTarget.equals(exposeSaveName)) {
                    foundedField = currentField;
                    break;
                }
            }
            break;
        }
        return foundedField;
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
        while((lastDotIndex = yamlPath.lastIndexOf(".")) == yamlPath.length() - 1)
            yamlPath.deleteCharAt(lastDotIndex);

        return YamlPath.fromGlobalPath(yamlPath.toString());
    }

    private int listIndex = 0;
    public String findYamlLineName(String fullLine) {
        fullLine = IndentationUtils.removeIndentation(fullLine);
        if(fullLine.contains(": ")) {
            listIndex = 0;
            return fullLine.split(": ")[0].trim();
        } else if(fullLine.startsWith(YamlSetNode.ELEMENT_PREFIX)) {
            listIndex++;
            return Integer.toString(listIndex);
        } else if(fullLine.startsWith(YamlListNode.ELEMENT_PREFIX)) {
            listIndex++;
            return Integer.toString(listIndex);
        } else {
            listIndex = 0;
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

    public List<Field> getSavableField() {
        return reflectionHelper.findFieldWithAnnotation(YamlExpose.class);
    }
}
