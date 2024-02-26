package fr.eris.yaml.utils;

public class IndentationUtils {

    public static final String INDENTATION = "  ";

    public static String createIndentation(int indentationLevel) {
        if(indentationLevel < 0)
            throw new IllegalArgumentException("The indentation level should be at least 1 ! (provided " + indentationLevel + ")");

        StringBuilder newIndentation = new StringBuilder();

        for(int i = 0 ; i < indentationLevel ; i++) {
            newIndentation.append(INDENTATION);
        }

        return newIndentation.toString();
    }

    public static int retrieveIndentationLevel(String yamlLine) {
        String firstLine = yamlLine.split("\n")[0]; // In case of multiple line we only want to work with the first one
        int currentFoundLevel = 0;
        while(firstLine.startsWith(INDENTATION)) {
            currentFoundLevel += 1;
            firstLine = firstLine.replaceFirst(INDENTATION, "");
        }

        return currentFoundLevel;
    }

    public static String removeIndentation(String yamlLine) {
        return yamlLine.replace(createIndentation(retrieveIndentationLevel(yamlLine)), "");
    }
}
