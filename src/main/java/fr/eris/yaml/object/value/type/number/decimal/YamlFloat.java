package fr.eris.yaml.object.value.type.number.decimal;

import fr.eris.yaml.object.value.type.number.YamlNumberValue;

public class YamlFloat extends YamlNumberValue<Float> {
    public YamlFloat() {
        super(Float.class);
    }

    public boolean validateNumberValue(String value) {
        try {
            Float.parseFloat(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Float parseNumberValue(String value) {
        return Float.parseFloat(value);
    }
}
