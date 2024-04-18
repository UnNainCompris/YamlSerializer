package fr.eris.objecttest.superclass;

import fr.eris.yaml.api.object.annotation.YamlExpose;
import lombok.Getter;

public class ChildObject extends ParentObject {
    @YamlExpose(yamlSaveName = "intValue")
    @Getter private int hereIntField = 100;
    @YamlExpose(yamlSaveName = "StringValue")
    @Getter private String hereStringField = "TestChildValue";
}
