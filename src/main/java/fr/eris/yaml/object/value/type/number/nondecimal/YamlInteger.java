package fr.eris.yaml.object.value.type.number.nondecimal;

import fr.eris.yaml.object.value.type.number.YamlNumberValue;

public class YamlInteger extends YamlNumberValue<Integer> {
    public YamlInteger() {
        super(Integer.class);
    }

    public boolean validateNumberValue(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Integer parseNumberValue(String value) {
        return Integer.parseInt(value);
    }
}
