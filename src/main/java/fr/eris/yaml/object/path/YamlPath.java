package fr.eris.yaml.object.path;

import fr.eris.yaml.api.Yaml;
import fr.eris.yaml.object.exception.ErisYamlException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class YamlPath {

    public static final String YAML_PATH_SEPARATOR = ".";

    // used in retrieveParsedPath (as regex char)!
    public static final String YAML_PATH_SPLIT_CHAR = "\\" + YAML_PATH_SEPARATOR;

    public static final String[] YAML_ILLEGAL_PATH_CHAR = new String[]{":"};

    @Getter private String targetPath;

    public static YamlPath fromGlobalPath(String fullYamlPath) {
        return new YamlPath(fullYamlPath);
    }

    public static YamlPath fromGlobalPath(String[] fullYamlPath) {
        if(fullYamlPath == null || fullYamlPath.length == 0)
            return null;
        StringBuilder path = new StringBuilder();
        for(String currentPath : fullYamlPath)
            path.append(currentPath.trim()).append(".");
        return new YamlPath(path.deleteCharAt(path.lastIndexOf(".")).toString());
    }

    public YamlPath(String fullYamlPath) {
        if(fullYamlPath == null)
            throw new ErisYamlException("A yaml path cannot be null !");

        for(String illegalChar : YAML_ILLEGAL_PATH_CHAR)
            fullYamlPath = fullYamlPath.replace(illegalChar, "");

        if(fullYamlPath.isEmpty())
            throw new ErisYamlException("A yaml path cannot be empty !");

        this.targetPath = fullYamlPath.trim();
    }

    public String[] retrieveParsedPathAsArray() {
        return targetPath.split(YAML_PATH_SPLIT_CHAR);
    }

    public int getSplitPathLength() {
        return retrieveParsedPathAsArray().length;
    }

    public String getPathValueFromIndex(int index) {
        validateAccessIndex(index);
        return retrieveParsedPathAsArray()[index];
    }

    public String getLastPathValue() {
        return getPathValueFromIndex(getSplitPathLength() - 1);
    }

    public String getFirstPathValue() {
        return getPathValueFromIndex(0);
    }

    public String[] getPathInRange(int start, int end) {
        int startCopy = start;
        start = Math.max(Math.min(startCopy, end), 0);
        end = Math.max(Math.max(startCopy, end), 0);

        if(start >= getSplitPathLength())
            return new String[0];
        end = Math.min(getSplitPathLength() - 1, end);
        return Arrays.copyOfRange(retrieveParsedPathAsArray(), start, end);
    }

    public String[] getWholePathExceptLastValue() {
        if(retrieveParsedPathAsArray().length <= 1)
            return new String[0];
        return Arrays.copyOfRange(retrieveParsedPathAsArray(), 0, getSplitPathLength() - 1);
    }

    public String[] getWholePathExceptLastAndFirstValue() {
        if(retrieveParsedPathAsArray().length <= 1)
            return new String[0];
        return Arrays.copyOfRange(retrieveParsedPathAsArray(), 1, getSplitPathLength() - 1);
    }

    public String[] getWholePathExceptFirstValue() {
        if(retrieveParsedPathAsArray().length <= 1)
            return new String[0];
        return Arrays.copyOfRange(retrieveParsedPathAsArray(), 1, getSplitPathLength());
    }

    public void validateAccessIndex(int index) {
        if(index >= getSplitPathLength())
            throw new ErisYamlException("Index cannot be > as split path length");
    }

    public boolean equals(Object other) {
        return other instanceof YamlPath && ((YamlPath)other).targetPath.equals(this.targetPath);
    }

    public YamlPath append(String pathToAdd) {
        if(pathToAdd == null)
            throw new ErisYamlException("A yaml path cannot be null !");
        if(pathToAdd.isEmpty())
            throw new ErisYamlException("A yaml path cannot be empty !");
        this.targetPath += (YAML_PATH_SEPARATOR + pathToAdd.trim());
        return this;
    }

    public boolean startWith(String prefix) {
        return targetPath.startsWith(prefix);
    }

    public boolean startWith(YamlPath prefix) {
        return targetPath.startsWith(prefix.getTargetPath());
    }

    public String toString() {
        return "{YamlPath:" + targetPath + "}";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public YamlPath clone() {
        return new YamlPath(targetPath);
    }

    public YamlPath removeLast() {
        StringBuilder newTargetPath = new StringBuilder();
        for(String path : targetPath.split(YAML_PATH_SPLIT_CHAR))
            newTargetPath.append(path).append(YAML_PATH_SEPARATOR);

        newTargetPath.deleteCharAt(newTargetPath.lastIndexOf(YAML_PATH_SEPARATOR));

        int lastIndex = newTargetPath.lastIndexOf(YAML_PATH_SEPARATOR);
        if(lastIndex != -1)
            newTargetPath.replace(lastIndex, newTargetPath.length(), "");

        return this;
    }
}
