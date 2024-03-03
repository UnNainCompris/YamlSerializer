package fr.eris.yaml.object.path;

import fr.eris.yaml.object.exception.ErisYamlException;

import java.util.Arrays;
import java.util.List;

public class YamlPath {

    public static final String YAML_PATH_SEPARATOR = ".";

    // used in retrieveParsedPath (as regex char)!
    public static final String YAML_PATH_SPLIT_CHAR = "\\" + YAML_PATH_SEPARATOR;

    public String targetPath;

    public static YamlPath fromGlobalPath(String fullYamlPath) {
        return new YamlPath(fullYamlPath);
    }

    public YamlPath(String fullYamlPath) {
        if(fullYamlPath == null)
            throw new ErisYamlException("A yaml path cannot be null !");
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

    public String[] getWholePathExceptLastValue() {
        return Arrays.copyOfRange(retrieveParsedPathAsArray(), 0, getSplitPathLength() - 2);
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

    public String toString() {
        return "{YamlPath:" + targetPath + "}";
    }

    @Override
    public int hashCode() {
        return targetPath.hashCode();
    }

}
