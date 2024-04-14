package fr.eris.objecttest;

import fr.eris.yaml.api.object.annotation.YamlExpose;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class TestYamlObject {

    @YamlExpose(yamlSaveName = "defaultFieldName1")
    @Setter @Getter private String testFieldFirst = "TestValue1";

    @YamlExpose(yamlSaveName = "defaultFieldName2")
    @Setter @Getter private String testFieldSecond = "TestValue2";

    @YamlExpose(yamlSaveName = "defaultFieldEnum")
    @Setter @Getter private TestEnum testEnumFirst = TestEnum.BAR;

    @YamlExpose(yamlSaveName = "defaultListObject", serializeEvenIfNull = false)
    @Setter @Getter private List<TestYamlObject> testListFieldObject;

    @YamlExpose(yamlSaveName = "defaultListString", serializeEvenIfNull = false)
    @Setter @Getter private List<String> testListFieldString;

    @YamlExpose(yamlSaveName = "defaultListInteger", serializeEvenIfNull = false)
    @Setter @Getter private List<Integer> testListFieldInteger;

    @YamlExpose(yamlSaveName = "defaultSet", serializeEvenIfNull = false)
    @Setter @Getter private HashSet<String> testSetFieldFirst;

    @YamlExpose(yamlSaveName = "innerClass", serializeEvenIfNull = false)
    @Setter @Getter private TestYamlObject testInnerClass;

    @YamlExpose(yamlSaveName = "innerOtherClass", serializeEvenIfNull = false)
    @Setter @Getter private TestYamlObjectOther testInnerOtherClass;

    public void applyInnerClass() {
        testInnerClass = new TestYamlObject();
    }
    public void applyInnerOtherClass() {
        testInnerOtherClass = new TestYamlObjectOther();
    }

    public void applyTestListStringField() {
        testListFieldString = Arrays.asList("TestList1", "TestList2", "TestList3");
    }

    public void applyTestListIntField(int aditional) {
        testListFieldInteger = Arrays.asList(111 + aditional, 222 + aditional, 333 + aditional);
    }

    public void applyTestListField(String customPrefix) {
        testListFieldString = Arrays.asList(customPrefix + "TestList1", customPrefix + "TestList2", customPrefix + "TestList3");
    }

    public void applyTestSetField() {
        testSetFieldFirst = new HashSet<>();
        testSetFieldFirst.add("TestSet1");
        testSetFieldFirst.add("TestSet2");
        testSetFieldFirst.add("TestSet3");
    }

    public void applyTestObjectList() {
        testListFieldObject = new ArrayList<>();
        for(int i = 1 ; i <= 3 ; i++) {
            TestYamlObject newObject = new TestYamlObject();
            if(i == 1)
                newObject.setTestEnumFirst(TestEnum.FOO);
            newObject.setTestFieldFirst("Here:" + i + "!");
            testListFieldObject.add(newObject);
        }
    }
}
