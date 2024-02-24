package fr.eris;

import fr.eris.yaml.object.annotation.YamlExpose;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestYamlObject {

    @YamlExpose(yamlSaveName = "defaultFieldName1")
    @Setter @Getter private String testFieldFirst = "TestValue1";

    @YamlExpose(yamlSaveName = "defaultFieldName2")
    @Setter @Getter private String testFieldSecond = "TestValue2";

    @YamlExpose(yamlSaveName = "defaultList", serializeEvenIfNull = true)
    @Setter @Getter private List<String> testListFieldFirst;

    @YamlExpose(yamlSaveName = "innerClass", serializeEvenIfNull = true)
    @Setter @Getter private TestYamlObject testInnerClass;

    public void applyInnerClass() {
        testInnerClass = new TestYamlObject();
    }

    public void applyTestListField() {
        testListFieldFirst = Arrays.asList("Test1", "Test2", "Test3");
    }

}
