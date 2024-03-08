package fr.eris.yaml.object.value.type;

import fr.eris.yaml.object.value.IYamlValue;

public class YamlBoolean extends IYamlValue<Boolean> {
    public YamlBoolean() {
        super(Boolean.class);
    }

    private static final String[] possibleFalseValue = new String[]{"false", "no", "n", "deny"};
    private static final String[] possibleTrueValue = new String[]{"true", "yes", "y", "allow"};

    public boolean validateValue(String value) {
        for(String trueValue : possibleTrueValue) {
            if(value.equalsIgnoreCase(trueValue))
                return true;
        }

        for(String trueValue : possibleFalseValue) {
            if(value.equalsIgnoreCase(trueValue))
                return true;
        }

        return false;
    }

    public Boolean parseValue(String value) {
        for(String trueValue : possibleTrueValue) {
            if(value.equalsIgnoreCase(trueValue)) return true;
        }

        for(String trueValue : possibleFalseValue) {
            if(value.equalsIgnoreCase(trueValue)) return false;
        }

        return null;
    }
}
