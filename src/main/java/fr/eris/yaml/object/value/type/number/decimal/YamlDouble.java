package fr.eris.yaml.object.value.type.number.decimal;

import fr.eris.yaml.object.value.type.number.YamlNumberValue;

public class YamlDouble extends YamlNumberValue<Double> {
    public YamlDouble() {
        super(Double.class);
    }

    public boolean validateNumberValue(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Double parseNumberValue(String value) {
        return Double.parseDouble(value);
    }
}
