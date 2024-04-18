package fr.eris.yaml.object.value.type.number;

import fr.eris.yaml.object.value.IYamlValue;

public abstract class YamlNumberValue<T extends Number> extends IYamlValue<T> {
    public YamlNumberValue(Class<T> handledType) {
        super(handledType);
    }

    protected final String DECIMAL_SEPARATOR = "\\."; // as split char
    protected final String[] BIG_NUMBER_SEPARATOR = new String[]{"-", "_", "|"};

    public final String valueToNumberValue(String value) {
        if(!isDecimalType())
            value = value.split(DECIMAL_SEPARATOR)[0];
        for(String bigNumberSeparator : BIG_NUMBER_SEPARATOR)
            value = value.replace(bigNumberSeparator, "");
        return value;
    }

    public final boolean validateValue(String value) {
        value = value.trim();
        return validateNumberValue(valueToNumberValue(value));
    }


    public final T parseValue(String value) {
        value = value.trim();
        return parseNumberValue(valueToNumberValue(value));
    }

    public abstract boolean validateNumberValue(String value);
    public abstract T parseNumberValue(String value);

    public boolean isDecimalType() {
        return Double.class.isAssignableFrom(handledType) ||
                Float.class.isAssignableFrom(handledType);
    }
}
