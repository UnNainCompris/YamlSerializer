package fr.eris.yaml.object.value.type;

import fr.eris.yaml.object.value.IYamlValue;

import java.util.Arrays;
import java.util.List;

public class YamlString extends IYamlValue<String> {

    private final List<String> STRING_DEFINER_CHARACTER = Arrays.asList("\"", "'");

    public YamlString() {
        super(String.class);
    }

    public boolean validateValue(String value) {
        return true;
    }

    public String parseValue(String value) {
        value = value.trim();
        for(String possibleDefinerChar : STRING_DEFINER_CHARACTER) {
            if(!value.startsWith(possibleDefinerChar)) continue;

            String[] buffer = value.split(possibleDefinerChar);
            String[] realValueBuffer = Arrays.copyOfRange(buffer, 1, buffer.length - 1); // "Here something" interesting !" -> Here something" interesting !
            StringBuilder newValueBuilder = new StringBuilder();
            for(String newValue : realValueBuffer)
                newValueBuilder.append(newValue);
            value = newValueBuilder.toString();

            break;
        }
        return value.trim();
    }
}
