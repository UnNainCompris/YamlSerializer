package fr.eris.objecttest;

import fr.eris.yaml.api.object.annotation.YamlExpose;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

public class TestYamlTypeObject {

    @YamlExpose(yamlSaveName = "stringValueField")
    @Setter @Getter private String stringValue = "HereStringValue";

    @YamlExpose(yamlSaveName = "nullValueField")
    @Setter @Getter private Object nullValue = "null";

    @YamlExpose(yamlSaveName = "booleanTrueValueField")
    @Setter @Getter private boolean booleanValueTrue = true;

    @YamlExpose(yamlSaveName = "booleanFalseValueField")
    @Setter @Getter private boolean booleanValueFalse = false;

    @YamlExpose(yamlSaveName = "doubleValueField")
    @Setter @Getter private double doubleValue = 100.00d;

    @YamlExpose(yamlSaveName = "floatValueField")
    @Setter @Getter private float floatValue = 500.00f;

    @YamlExpose(yamlSaveName = "longValueField")
    @Setter @Getter private long longValue = 10L;

    @YamlExpose(yamlSaveName = "intValueField")
    @Setter @Getter private int intValue = 50;

    @YamlExpose(yamlSaveName = "shortValueField")
    @Setter @Getter private short shortValue = 1;

    @YamlExpose(yamlSaveName = "byteValueField")
    @Setter @Getter private byte byteValue = 5;

    public void printAll() {

        System.out.println("\nPRINT ALL\n");

        for(Field field : this.getClass().getDeclaredFields()) {
            try {
                //if(field.getType().isPrimitive())
                //    System.out.println(TypeUtils.primitiveToObject(field.getType()));
                System.out.println(field.getName() + " -- " + field.get(this) + " (" + field.getType().getCanonicalName() + ")");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
