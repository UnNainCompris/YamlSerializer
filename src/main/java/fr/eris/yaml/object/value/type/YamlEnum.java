package fr.eris.yaml.object.value.type;

import fr.eris.yaml.object.value.IYamlValue;

public class YamlEnum extends IYamlValue<Enum> {
    public YamlEnum() {
        super(Enum.class);
    }

    public boolean validateValue(String value) {
        return false;
    }

    public boolean validateValue(String value, Class<?> type) {
        if(!Enum.class.isAssignableFrom(type)) return false;
        try {
            Enum.valueOf((Class<Enum>) type, value);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public Enum<?> parseValue(String value) {
        return null;
    }

    public Enum<?> parseValue(String value, Class<?> type) {
        if(!Enum.class.isAssignableFrom(type)) return null;
        try {
            return Enum.valueOf((Class<Enum>) type, value);
        } catch (Exception ignored) {
            return null;
        }
    }
}
