package fr.eris.objecttest.superclass;

import fr.eris.yaml.api.object.annotation.YamlExpose;
import lombok.Getter;

public class ParentObject {
    @YamlExpose(yamlSaveName = "doubleValue")
    @Getter private Double hereDoubleField = 10.999D;
    @YamlExpose(yamlSaveName = "booleanValue")
    @Getter private boolean hereBooleanField = true;
}
