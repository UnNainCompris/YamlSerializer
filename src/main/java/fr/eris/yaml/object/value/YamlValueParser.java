package fr.eris.yaml.object.value;

import fr.eris.yaml.api.object.value.YamlValue;
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

import java.util.LinkedHashMap;

public class YamlValueParser {

    private final LinkedHashMap<Class<?>, YamlValue<?>> registeredYamlValue = new LinkedHashMap<>();

    public YamlValueParser(){
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

    public Object parseValue(String rawValue, Class<?> type) {
        type = TypeUtils.convertIfPrimitiveToObject(type);
        for(Class<?> valueClassType : registeredYamlValue.keySet()) {
            if(!valueClassType.isAssignableFrom(type)) continue;

            YamlValue<?> yamlValue = registeredYamlValue.get(type);
            if(!yamlValue.validateValue(rawValue)) continue;

            return yamlValue.parseValue(rawValue);
        }
        throw new ErisYamlException("No legal value found for the rawValue: " +
                rawValue + " of type: " + type + " !");
    }

    public boolean isAlreadyHandledType(Class<?> type) {
        type = TypeUtils.convertIfPrimitiveToObject(type);
        return registeredYamlValue.containsKey(type);
    }

    public void registerNewValue(YamlValue<?> newYamlValue) {
        registeredYamlValue.put(TypeUtils.convertIfPrimitiveToObject(newYamlValue.getHandledType()), newYamlValue);
    }
}
