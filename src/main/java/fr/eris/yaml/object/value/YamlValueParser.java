package fr.eris.yaml.object.value;

import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.value.type.YamlBoolean;
import fr.eris.yaml.object.value.type.YamlNull;
import fr.eris.yaml.object.value.type.YamlString;
import fr.eris.yaml.object.value.type.number.decimal.YamlDouble;
import fr.eris.yaml.object.value.type.number.decimal.YamlFloat;
import fr.eris.yaml.object.value.type.number.nondecimal.YamlByte;
import fr.eris.yaml.object.value.type.number.nondecimal.YamlInteger;
import fr.eris.yaml.object.value.type.number.nondecimal.YamlLong;
import fr.eris.yaml.object.value.type.number.nondecimal.YamlShort;
import fr.eris.yaml.utils.TypeUtils;

import java.util.HashMap;

public class YamlValueParser {

    private static final HashMap<Class<?>, YamlValue<?>> registeredYamlValue = new HashMap<>();

    static {
        registeredYamlValue.put(Object.class, new YamlNull());
        registeredYamlValue.put(String.class, new YamlString());
        registeredYamlValue.put(Boolean.class, new YamlBoolean());

        registeredYamlValue.put(Double.class, new YamlDouble());
        registeredYamlValue.put(Float.class, new YamlFloat());

        registeredYamlValue.put(Integer.class, new YamlInteger());
        registeredYamlValue.put(Long.class, new YamlLong());
        registeredYamlValue.put(Short.class, new YamlShort());
        registeredYamlValue.put(Byte.class, new YamlByte());
    }

    public static Object parseValue(String rawValue, Class<?> type) {
        if(type.isPrimitive()) {
            Class<?> primitiveToObject = TypeUtils.primitiveToObject(type);
            if (primitiveToObject != null)
                type = primitiveToObject;
        }
        for(Class<?> valueClassType : registeredYamlValue.keySet()) {
            if(!valueClassType.isAssignableFrom(type)) continue;

            YamlValue<?> yamlValue = registeredYamlValue.get(type);
            if(!yamlValue.validateValue(rawValue)) continue;

            return yamlValue.parseValue(rawValue);
        }
        throw new ErisYamlException("No legal value found for the rawValue: " +
                rawValue + " of type: " + type + " !");
    }

}
