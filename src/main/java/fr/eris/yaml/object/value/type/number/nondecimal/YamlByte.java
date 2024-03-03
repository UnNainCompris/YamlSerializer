package fr.eris.yaml.object.value.type.number.nondecimal;

import fr.eris.yaml.object.value.type.number.YamlNumberValue;

public class YamlByte extends YamlNumberValue<Byte> {
    public YamlByte() {
        super(Byte.class);
    }

    public boolean validateNumberValue(String value) {
        try {
            Byte.parseByte(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Byte parseNumberValue(String value) {
        return Byte.parseByte(value);
    }
}
