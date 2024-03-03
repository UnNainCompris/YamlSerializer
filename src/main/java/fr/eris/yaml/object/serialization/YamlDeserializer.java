package fr.eris.yaml.object.serialization;

import fr.eris.yaml.object.annotation.YamlExpose;
import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.node.iterable.list.YamlListNode;
import fr.eris.yaml.object.node.iterable.set.YamlSetNode;
import fr.eris.yaml.object.path.YamlPath;
import fr.eris.yaml.utils.IndentationUtils;
import fr.eris.yaml.utils.reflection.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class YamlDeserializer<T> {

    private final String serializedData;
    private final Class<T> objectClass;
    private final ReflectionHelper<T> reflectionHelper;
    private T buildedClass;

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
        buildedClass = reflectionHelper.buildClass();
        // use the deserializedDocument
        return buildedClass;
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
