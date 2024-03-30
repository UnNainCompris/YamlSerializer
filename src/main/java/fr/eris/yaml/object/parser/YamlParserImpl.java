package fr.eris.yaml.object.parser;

import fr.eris.yaml.api.object.parser.YamlParser;
import fr.eris.yaml.object.node.iterable.list.YamlListNodeImpl;
import fr.eris.yaml.object.node.iterable.set.YamlSetNodeImpl;
import fr.eris.yaml.object.path.YamlPath;
import fr.eris.yaml.utils.IndentationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class YamlParserImpl implements YamlParser {

    public LinkedHashMap<YamlPath, String> parseYamlContent(String rawContent) {
        LinkedHashMap<YamlPath, String> serializedValue = new LinkedHashMap<>();
        List<String> content = new ArrayList<>(Arrays.asList(rawContent.split("\n")));

        List<String> currentPath = new ArrayList<>();
        for(int currentLineIndex = 0 ; currentLineIndex < content.size() ; currentLineIndex++) {
            String currentLine = content.get(currentLineIndex);
            if(currentLine.trim().isEmpty()) {
                continue;
            }
            currentPath.add(findYamlLineName(currentLine));

            if(!isNextLineAnInnerObject(content, currentLineIndex)) {
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
        } else if(fullLine.startsWith(YamlSetNodeImpl.ELEMENT_PREFIX)) {
            actualListIndex++;
            return Integer.toString(actualListIndex);
        } else if(fullLine.startsWith(YamlListNodeImpl.ELEMENT_PREFIX)) {
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
        } else if(fullLine.startsWith(YamlSetNodeImpl.ELEMENT_PREFIX)) {
            return fullLine.replaceFirst(YamlSetNodeImpl.ELEMENT_PREFIX, "");
        } else if(fullLine.startsWith(YamlListNodeImpl.ELEMENT_PREFIX)) {
            return fullLine.replaceFirst(YamlListNodeImpl.ELEMENT_PREFIX, "");
        } else {
            return fullLine;
        }
    }

    public boolean isNextLineAnInnerObject(List<String> content, int currentLine) {
        if(!hasNextLine(content, currentLine)) return false;
        int currentIndentationLevel = IndentationUtils.retrieveIndentationLevel(content.get(currentLine));
        return currentIndentationLevel < IndentationUtils.retrieveIndentationLevel(content.get(currentLine + 1));
    }

    public boolean hasNextLine(List<String> content, int currentLine) {
        return content.size() - 1 > currentLine;
    }
}
