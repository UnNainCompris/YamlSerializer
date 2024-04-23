package fr.eris.yaml.object.serialization.deserialization;

import fr.eris.yaml.object.exception.ErisYamlException;
import fr.eris.yaml.object.path.YamlPath;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Getter
public class YamlDeserializationObject {
    private Field assosiatedField;
    private Object fieldParentObject;
    private YamlPath objectSerializationPath;

    private LinkedHashMap<Integer, Object> listValue = new LinkedHashMap<>();

    public static YamlDeserializationObject build(Field field, Object parentFieldObject,
                                                 YamlPath objectSerializationPath) {
        return new YamlDeserializationObject().setAssosiatedField(field)
                .setParentFieldObject(parentFieldObject)
                .setObjectSerializationPath(objectSerializationPath);
    }

    public YamlDeserializationObject setAssosiatedField(Field field) {
        this.assosiatedField = field;
        return this;
    }

    public YamlDeserializationObject setObjectSerializationPath(YamlPath objectSerializationPath) {
        this.objectSerializationPath = objectSerializationPath;
        return this;
    }

    public YamlDeserializationObject setParentFieldObject(Object parentFieldObject) {
        this.fieldParentObject = parentFieldObject;
        return this;
    }

    public void setFieldValue(Object fieldValue) {
        if(fieldParentObject == null)
            throw new ErisYamlException("Cannot get field value if the fieldparent class is null");
        try {
            assosiatedField.setAccessible(true);
            assosiatedField.set(fieldParentObject, fieldValue);
        } catch (Exception exception){
            throw new ErisYamlException("Unable to set field value");
        }
    }

    public Object getFieldValue() {
        if(fieldParentObject == null)
            throw new ErisYamlException("Cannot get field value if the fieldparent class is null");
        try {
            assosiatedField.setAccessible(true);
            return assosiatedField.get(fieldParentObject);
        } catch (Exception exception){
            exception.printStackTrace();
            throw new ErisYamlException("Unable to retrieve field value ");
        }
    }

    public String toString() {
        return "{YamlDeserializationObject:field: " + assosiatedField.getName() + ";parent: "
                + fieldParentObject + ";path: " + objectSerializationPath + (fieldParentObject != null ? (";value: " + getFieldValue()) : "") + "}";
    }

    public void setObjectListValue(int index, Object value) {
        listValue.put(index, value);
    }

    public boolean hasObjectListIndex(int index) {
        return listValue.containsKey(index);
    }

    public void doCollectionThings() {
        if(List.class.isAssignableFrom(assosiatedField.getType())) {
            List<Object> list = (List<Object>)getFieldValue();

            int highest = 0;
            for(Integer index : listValue.keySet())
                highest = Math.max(index, highest);

            for(int i = 0 ; i < highest ; i++)
                list.add(listValue.get(i+1));

        } else if(Set.class.isAssignableFrom(assosiatedField.getType())) {
            Set<Object> set = (Set<Object>)getFieldValue();
            set.addAll(listValue.values());
        }
    }
}
