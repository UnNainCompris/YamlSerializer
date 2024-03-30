package fr.eris.yaml.api.object.parser;

import fr.eris.yaml.object.path.YamlPath;

import java.util.LinkedHashMap;
import java.util.List;

public interface YamlParser {

    LinkedHashMap<YamlPath, String> parseYamlContent(String rawContent);
    String findYamlLineName(String fullLine);
    String findYamlLineValue(String fullLine);

    boolean isNextLineAnInnerObject(List<String> content, int currentLine);
    boolean hasNextLine(List<String> content, int currentLine);

}
