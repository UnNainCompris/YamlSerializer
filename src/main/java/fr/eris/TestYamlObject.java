package fr.eris;

import fr.eris.yaml.object.annotation.YamlExpose;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class TestYamlObject {

    @YamlExpose(yamlSaveName = "defaultFieldName1")
    @Setter @Getter private String testFieldFirst = "TestValue1";

    @YamlExpose(yamlSaveName = "defaultFieldName2")
    @Setter @Getter private String testFieldSecond = "TestValue2";

    @YamlExpose(yamlSaveName = "defaultList", serializeEvenIfNull = false)
    @Setter @Getter private List<String> testListFieldFirst;

    @YamlExpose(yamlSaveName = "defaultSet", serializeEvenIfNull = false)
    @Setter @Getter private Set<String> testSetFieldFirst;

    @YamlExpose(yamlSaveName = "innerClass", serializeEvenIfNull = false)
    @Setter @Getter private TestYamlObject testInnerClass;

    public void applyInnerClass() {
        testInnerClass = new TestYamlObject();
    }

    public void applyTestListField() {
        testListFieldFirst = Arrays.asList("TestList1", "TestList2", "TestList3");
    }

    public void applyTestSetField() {
        testSetFieldFirst = new HashSet<>();
        testSetFieldFirst.add("TestSet1");
        testSetFieldFirst.add("TestSet2");
        testSetFieldFirst.add("TestSet3");
    }

}
