package fr.eris.yaml.object.value.type;

import fr.eris.yaml.object.value.YamlValue;

public class YamlNull extends YamlValue<Object> {
    public YamlNull() {
        super(Object.class);
    }

    public boolean validateValue(String value) {
        return value != null &&
                (value.equalsIgnoreCase("none") ||
                 value.equalsIgnoreCase("null") ||
                 value.equalsIgnoreCase("nothing"));
    }

    public Object parseValue(String value) {
        return null;
    }
}
