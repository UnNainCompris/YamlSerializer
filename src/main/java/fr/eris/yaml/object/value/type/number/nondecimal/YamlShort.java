package fr.eris.yaml.object.value.type.number.nondecimal;

import fr.eris.yaml.object.value.type.number.YamlNumberValue;

public class YamlShort extends YamlNumberValue<Short> {
    public YamlShort() {
        super(Short.class);
    }

    public boolean validateNumberValue(String value) {
        try {
            Short.parseShort(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Short parseNumberValue(String value) {
        return Short.parseShort(value);
    }
}
