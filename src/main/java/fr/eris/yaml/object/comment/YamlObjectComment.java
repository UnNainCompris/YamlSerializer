package fr.eris.yaml.object.comment;

import fr.eris.yaml.api.object.annotation.YamlCommentPlacement;
import fr.eris.yaml.utils.IndentationUtils;
import lombok.Getter;

public class YamlObjectComment extends YamlComment {
    @Getter private final YamlCommentPlacement yamlCommentPlacement;

    public YamlObjectComment(String commentValue,
                             YamlCommentPlacement yamlCommentPlacement) {
        super(commentValue);
        this.yamlCommentPlacement = yamlCommentPlacement;
    }

    public String serialize(int indentationLevel) {
        if(yamlCommentPlacement == YamlCommentPlacement.SAME_LINE)
            return YAML_COMMENT_CHAR + " " + commentValue;

        StringBuilder serializedComment = new StringBuilder();
        for(String currentComment : commentValue.split(YAML_SPLIT_LINE_CHAR))
            serializedComment.append(IndentationUtils.createIndentation(indentationLevel))
                    .append(YAML_COMMENT_CHAR).append(" ").append(currentComment);

        return serializedComment.toString();
    }
}
