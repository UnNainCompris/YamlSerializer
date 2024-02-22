package fr.eris.yaml.object.comment;

import fr.eris.yaml.object.impl.YamlSerializable;

public abstract class YamlComment implements YamlSerializable {
    public static final String YAML_COMMENT_CHAR = "#";
    public static final String YAML_SPLIT_LINE_CHAR = "$\\n";

    protected final String commentValue;

    protected YamlComment(String commentValue) {
        this.commentValue = commentValue;
    }
}
