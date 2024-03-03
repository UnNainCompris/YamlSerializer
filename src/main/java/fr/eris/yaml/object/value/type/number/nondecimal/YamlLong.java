package fr.eris.yaml.object.value.type.number.nondecimal;

import fr.eris.yaml.object.value.type.number.YamlNumberValue;

public class YamlLong extends YamlNumberValue<Long> {
    public YamlLong() {
        super(Long.class);
    }

    public boolean validateNumberValue(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long parseNumberValue(String value) {
        return Long.parseLong(value);
    }
}
