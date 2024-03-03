package fr.eris.yaml.object;

/**
 * Class to store every format type
 * like string can be shown in the next ways:
 * "here a value" ; 'here a value' ; here a value.
 * Or null value can be shown like this:
 * none ; null ; .
 */
public enum YamlDictionary {
    STRING("%value%", "%value%", (valueToCheck) -> true),
    NULL(null, "null",
        (valueToCheck) -> valueToCheck.isEmpty() ||
                        valueToCheck.equalsIgnoreCase("none")||
                        valueToCheck.equalsIgnoreCase("null")),
    BOOLEAN_TRUE(true, "true",
        (valueToCheck) -> valueToCheck.equalsIgnoreCase("yes") ||
                        valueToCheck.equalsIgnoreCase("y")||
                        valueToCheck.equalsIgnoreCase("true") ||
                        valueToCheck.equalsIgnoreCase("allow")),
    BOOLEAN_FALSE(false, "false",
        (valueToCheck) -> valueToCheck.equalsIgnoreCase("no") ||
                        valueToCheck.equalsIgnoreCase("n")||
                        valueToCheck.equalsIgnoreCase("false") ||
                        valueToCheck.equalsIgnoreCase("deny"));

    YamlDictionary(Object value, String defaultVisualValue, YamlValueChecker yamlValueChecker) {

    }

    interface YamlValueRetriever<T> {
        T retrieveValue(String valueToCheck);
    }

    interface YamlValueChecker {
        boolean validateValue(String valueToCheck);
    }
}
