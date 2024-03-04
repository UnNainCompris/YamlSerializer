package fr.eris.yaml.object.serialization;

import fr.eris.objecttest.TestYamlObject;
import fr.eris.yaml.object.annotation.YamlExpose;
import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.node.iterable.list.YamlListNode;
import fr.eris.yaml.object.node.iterable.set.YamlSetNode;
import fr.eris.yaml.object.path.YamlPath;
import fr.eris.yaml.object.value.YamlValueParser;
import fr.eris.yaml.utils.IndentationUtils;
import fr.eris.yaml.utils.TypeUtils;
import fr.eris.yaml.utils.reflection.ReflectionHelper;
import fr.eris.yaml.utils.storage.Tuple;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class YamlDeserializerOld<T> {

    private final String serializedData;
    private final Class<T> objectClass;
    private final ReflectionHelper<T> reflectionHelper;
    private T builtClass;

    public YamlDeserializerOld(String serializedData, Class<T> objectClass) {
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

        for(YamlPath path : yamlPathToField.keySet()) {
            if(true) break;
            Tuple<Field, Object> tuple = yamlPathToField.get(path);
            YamlPath actualPath = null;
            List<String> pathAsList = Arrays.asList(path.retrieveParsedPathAsArray());
            for (String currentPathValue : pathAsList) {
                if (actualPath == null) actualPath = YamlPath.fromGlobalPath(pathAsList.get(0));
                else actualPath.append(currentPathValue);

                for(YamlPath currentPath : yamlPathToField.keySet()) {
                    if (actualPath.getSplitPathLength() <= 1) break;
                    if (YamlPath.fromGlobalPath(actualPath.getWholePathExceptLastValue()).equals
                            (YamlPath.fromGlobalPath(currentPath.getWholePathExceptLastValue())) && !actualPath.equals(currentPath)) {
                        System.out.println("Found similar ! " + actualPath + " -- " + currentPath);
                        System.out.println("-- Found similar ! " + actualPath + " -- " + currentPath);
                        yamlPathToField.get(path).setB(yamlPathToField.get(actualPath).getB());
                        break;
                    }
                }
            }
        }


        for(YamlPath path : yamlPathToField.keySet()) {
            Tuple<Field, Object> tuple = yamlPathToField.get(path);
            YamlPath actualPath = null;
            List<String> pathAsList = Arrays.asList(path.retrieveParsedPathAsArray());
            for(String currentPathValue : pathAsList) {
                if (actualPath == null) actualPath = YamlPath.fromGlobalPath(pathAsList.get(0));
                else actualPath.append(currentPathValue);
                tuple = yamlPathToField.get(actualPath);
                if(tuple == null) continue;
                try {
                    tuple.getA().setAccessible(true);
                    if (tuple.getA().get(tuple.getB()) == null) {
                        System.out.println("Parent object " + tuple.getB() + " for " + actualPath);
                        //System.out.println("New value for " + actualPath + " -- " + path);
                        tuple.getA().set(tuple.getB(), buildObjectFromField(tuple.getA()));
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        for(YamlPath path : new ArrayList<>(serializedValue.keySet())) {
            Tuple<Field, Object> tuple = yamlPathToField.get(path);
            if (tuple == null) {

                YamlPath other = YamlPath.fromGlobalPath(path.getWholePathExceptLastValue());
                tuple = yamlPathToField.get(other);
                if(tuple == null) {
                    System.out.println("Le mec a rien compris " + other);
                    continue;
                }
                Field field = tuple.getA();
                if(Collection.class.isAssignableFrom(field.getType()) || field.getType().isArray()) {
                    Collection<Object> collection;
                    for(int i = 0 ; i < 999 ; i++) {
                        YamlPath newPath = other.clone().append(String.valueOf(i + 1));
                        if(!serializedValue.containsKey(newPath)) {
                            System.out.println("Doesn't contain " + newPath);
                            break;
                        } else {
                            //System.out.println("Contain " + newPath + " -- " + serializedValue.get(newPath) + " -- ");
                        }
                        try {
                            System.out.println(newPath + " -- " + serializedValue.get(newPath));
                            collection = (Collection<Object>)field.get(tuple.getB());
                            if(collection == null) {
                                collection = new ArrayList<>();
                                field.set(tuple.getB(), collection);
                            }
                            ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                            Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
                            collection.add(YamlValueParser.parseValue(serializedValue.get(newPath), stringListClass));

                            if(newPath.equals(YamlPath.fromGlobalPath("innerClass.defaultListString.3"))) {
                                System.out.println("HERERRE " + ((TestYamlObject)builtClass).getTestInnerClass().getTestListFieldString()
                                        + " -- " + ((TestYamlObject)builtClass).getTestInnerClass() + " -- " + builtClass);
                                System.out.println(tuple.getB() + " - " + field.getName() + " -- " + field.get(tuple.getB()));
                            }

                            if(newPath.equals(YamlPath.fromGlobalPath("innerClass.defaultListInteger.3"))) {
                                System.out.println("HERERRE " + ((TestYamlObject)builtClass).getTestInnerClass().getTestListFieldInteger()
                                        + " -- " + ((TestYamlObject)builtClass).getTestInnerClass()
                                        + " -- " + builtClass);
                                System.out.println(tuple.getB() + " - " + field.getName() + " -- " + field.get(tuple.getB()));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        serializedValue.remove(newPath);
                    }
                    //System.out.println("List ouuu " + path);
                }
                continue;
            }
            //System.out.println("Allowed - " + path);
            Field field = tuple.getA();
            try {
                field.setAccessible(true);
                if(Collection.class.isAssignableFrom(field.getType()) || field.getType().isArray()) {
                    field.set(tuple.getB(), buildObjectFromField(field));
                } else if(TypeUtils.isNativeClass(field.getType())) {
                    //System.out.println("Native set " + path);
                    field.set(tuple.getB(), YamlValueParser.parseValue(serializedValue.get(path), field.getType()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new ErisYamlException("Error while deserializing field ! {fieldName:" + field.getName() + "}");
            }
        }
    }
    public HashMap<YamlPath, Tuple<Field, Object>> buildYamlPathToField(Collection<YamlPath> requiredPath) {
        HashMap<YamlPath, Tuple<Field, Object>> yamlPathToField = new HashMap<>();

        for(YamlPath path : requiredPath) {
            createFieldObjectTuple(path, yamlPathToField);
        }
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

            if(actualPath.getSplitPathLength() > 1 &&
                    yamlPathToField.containsKey(YamlPath.fromGlobalPath(actualPath.getWholePathExceptFirstValue()))) {
                System.out.println("Setting similar for " + actualPath + " -- " + YamlPath.fromGlobalPath(actualPath.getWholePathExceptFirstValue()));
                currentObject = yamlPathToField.get(YamlPath.fromGlobalPath(actualPath.getWholePathExceptLastValue()));
            }

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
                throw new ErisYamlException("Error while deserializing field !");

            if(!yamlPathToField.containsKey(actualPath)) {
                yamlPathToField.put(actualPath.clone(), new Tuple<>(foundedField, currentObject));
            }
            if(Collection.class.isAssignableFrom(currentObject.getClass()) || currentObject.getClass().isArray())
                break;
            if(TypeUtils.isNativeClass(foundedField.getType()))
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

    public HashMap<YamlPath, String> retrieveSerializedValue() {
        HashMap<YamlPath, String> serializedValue = new HashMap<>();
        List<String> content = new ArrayList<>(Arrays.asList(serializedData.split("\n")));

        List<String> currentPath = new ArrayList<>();
        for(int currentLineIndex = 0 ; currentLineIndex < content.size() ; currentLineIndex++) {
            String currentLine = content.get(currentLineIndex);
            currentPath.add(findYamlLineName(currentLine));

            if(!isNextLineAnInnerYamlObject(content, currentLineIndex)) {
                System.out.println(buildYamlPath(currentPath) + " -- " + findYamlLineValue(currentLine));
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
