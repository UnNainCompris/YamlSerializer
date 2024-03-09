package fr.eris.yaml.object.value.type;

import fr.eris.yaml.object.value.IYamlValue;

public class YamlString extends IYamlValue<String> {
    public YamlString() {
        super(String.class);
    }

    public boolean validateValue(String value) {
        return true;
    }

    public String parseValue(String value) {
        return value;
    }
}
