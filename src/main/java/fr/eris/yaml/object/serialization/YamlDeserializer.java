package fr.eris.yaml.object.serialization;

import fr.eris.yaml.object.YamlDocument;
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
    private final ReflectionHelper reflectionHelper;

    private YamlDocument serializedDocument;

    public YamlDeserializer(String serializedData, Class<T> objectClass) {
        this.serializedData = serializedData;
        this.objectClass = objectClass;
        this.reflectionHelper = new ReflectionHelper(objectClass);
    }

    public T retrieveClass() {
        serializedDocument = retrieveDocument();

        return null;
    }

    public YamlDocument retrieveDocument() {
        YamlDocument deserializedDocument = new YamlDocument();
        HashMap<YamlPath, String> serializedValue = retrieveSerializedValue();
        HashMap<YamlPath, Field> pathToField = new HashMap<>(); // TODO: 01/03/2024  


        return deserializedDocument;
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
        return YamlPath.fromGlobalPath(yamlPath.deleteCharAt(yamlPath.lastIndexOf(".")).toString());
    }

    public String findYamlLineName(String fullLine) {
        return fullLine.split(": ")[0].trim();
    }

    public String findYamlLineValue(String fullLine) {
        return fullLine.split(": ")[1];
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
