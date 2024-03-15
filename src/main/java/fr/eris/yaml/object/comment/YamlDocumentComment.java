package fr.eris.yaml.object.comment;

import java.util.Arrays;

/**
 * Use to place a comment inside a YamlDocument between 2 object or at a specified line
 */
public class YamlDocumentComment extends YamlComment {

    public YamlDocumentComment(String commentValue) {
        super(commentValue);
    }

    public String serialize(int indentationLevel) {
        return YAML_COMMENT_CHAR + " " +
                Arrays.toString(commentValue.split(YAML_SPLIT_LINE_CHAR));
    }
}
