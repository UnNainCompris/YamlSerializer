package fr.eris;

import fr.eris.objecttest.TestYamlObject;
import fr.eris.objecttest.TestYamlTypeObject;
import fr.eris.yaml.object.YamlDocument;
import fr.eris.yaml.object.impl.IYamlObject;
import fr.eris.yaml.object.node.YamlNode;
import fr.eris.yaml.object.node.iterable.list.YamlListNode;
import fr.eris.yaml.object.node.iterable.set.YamlSetNode;
import fr.eris.yaml.object.path.YamlPath;
import fr.eris.yaml.object.serialization.YamlDeserializer;
import fr.eris.yaml.utils.IndentationUtils;

import java.util.Arrays;

public class Main {
    public void start() {
        testIndentationUtils();
        testYamlNode();
        testYamlList();
        testYamlSet();
        testYamlMap();
        testSerializer();
        testDeserializer();
        //testDeserializerType();
    }

    public void testIndentationUtils() {
        System.out.println("  -- <INDENTATION UTILS> --  \n");

        System.out.println("|" + IndentationUtils.createIndentation(1) + "|");
        System.out.println("|" + IndentationUtils.createIndentation(3) + "|");
        System.out.println(IndentationUtils.retrieveIndentationLevel(""));
        System.out.println(IndentationUtils.retrieveIndentationLevel("      "));

        System.out.println("\n  -- </INDENTATION UTILS> --  \n");
    }

    public void testYamlNode() {
        System.out.println("  -- <YAML NODE> --  \n");

        YamlNode<?> rootNode = YamlNode.buildEmptyNode("RootNode");
        IYamlObject lastNode = rootNode;
        for(int i = 1 ; i <= 5 ; i++) {
            YamlNode<?> newNode = YamlNode.buildEmptyNode("ChildNode{" + i + "}");
            lastNode.addChildren(newNode);
            lastNode = newNode;
        }
        lastNode.addChildren(new YamlNode<>("LastNode", "Here a value"));

        System.out.println(rootNode.serialize(0));

        System.out.println("\n  -- </YAML NODE> --  \n");
    }

    public void testYamlList() {
        System.out.println("  -- <YAML LIST> --  \n");

        YamlNode<?> rootNode = YamlNode.buildEmptyNode("RootNode");
        YamlListNode<YamlNode<String>> listNode = new YamlListNode<>("ListNode");
        rootNode.addChildren(listNode);

        for(int i = 1 ; i <= 5 ; i++) {
            YamlNode<String> newNode = new YamlNode<>("ChildNode{" + i + "}", "Here a value {" + i + "}");
            listNode.add(newNode);
        }

        System.out.println(rootNode.serialize(0));

        System.out.println("\n  -- </YAML LIST> --  \n");
    }

    public void testYamlSet() {
        System.out.println("  -- <YAML SET> --  \n");

        YamlNode<?> rootNode = YamlNode.buildEmptyNode("RootNode");
        YamlSetNode<YamlNode<String>> listNode = new YamlSetNode<>("SetNode");
        rootNode.addChildren(listNode);

        for(int i = 1 ; i <= 5 ; i++) {
            YamlNode<String> newNode = new YamlNode<>("ChildNode{" + i + "}", "Here a value {" + i + "}");
            listNode.add(newNode);
        }

        System.out.println(rootNode.serialize(0));

        System.out.println("\n  -- </YAML SET> --  \n");
    }

    public void testYamlMap() {
        System.out.println("  -- <YAML MAP> --  \n");

        YamlNode<?> rootNode = YamlNode.buildEmptyNode("RootNode");
        //YamlMap<String, Integer> yamlMap = new YamlMap<>("MapNode");
        //rootNode.addChild(yamlMap);

        //for(int i = 1 ; i <= 5 ; i++) {
        //    yamlMap.addEntry("Here a Key {" + i + "}", i);
        //}

        System.out.println(rootNode.serialize(0));

        System.out.println("\n  -- </YAML MAP> --  \n");
    }

    public void testSerializer() {
        System.out.println("  -- <YAML SERIALIZER> --  \n");

        TestYamlObject testYamlObject = new TestYamlObject();
        testYamlObject.applyTestSetField();
        testYamlObject.applyTestListIntField(1);
        testYamlObject.applyTestListStringField();
        testYamlObject.applyInnerClass();
        testYamlObject.applyInnerOtherClass();
        testYamlObject.getTestInnerClass().setTestFieldSecond("TestInnerSecond");
        testYamlObject.getTestInnerClass().setTestFieldFirst("TestInnerFirst");
        YamlDocument document = YamlDocument.generateFromClass(testYamlObject);
        System.out.println(document.serialize());

        System.out.println("\n  -- </YAML SERIALIZER> --  \n");
    }

    public void testDeserializer() {
        System.out.println("  -- <YAML DESERIALIZER> --  \n");

        TestYamlObject testYamlObject = new TestYamlObject();
        testYamlObject.applyTestListIntField(10);
        testYamlObject.applyTestListStringField();
        testYamlObject.applyTestSetField();
        testYamlObject.applyInnerClass();
        testYamlObject.getTestInnerClass().applyTestListIntField(100);
        testYamlObject.getTestInnerClass().applyTestListStringField();
        testYamlObject.getTestInnerClass().setTestFieldSecond("TestInnerSecond");
        testYamlObject.getTestInnerClass().setTestFieldFirst("TestInnerFirst");
        testYamlObject.getTestInnerClass().applyInnerClass();
        testYamlObject.applyInnerOtherClass();
        testYamlObject.getTestInnerClass().getTestInnerClass().setTestFieldFirst("TestDoubleInner1");
        testYamlObject.getTestInnerClass().getTestInnerClass().setTestFieldSecond("TestDoubleInner2");
        //testYamlObject.getTestInnerClass().applyTestListField("SERIRIRIEJHD");
        testYamlObject.setTestFieldFirst("HEREDESERIALIZED");
        YamlDocument document = YamlDocument.generateFromClass(testYamlObject);
        document.set("innerClass.defaultListInteger", Arrays.asList(1, 2, 999));
        String serializedDocument = document.serialize();

        System.out.println("Default document: \n\n" + serializedDocument);
        TestYamlObject deserializedObject = new YamlDeserializer<>(serializedDocument, TestYamlObject.class).retrieveClass();
        YamlDocument deserializedDocument = YamlDocument.generateFromClass(deserializedObject);
        System.out.println("-- " + deserializedObject.getTestSetFieldFirst());
        System.out.println("Deserialized document: \n\n" + deserializedDocument.serialize());

        System.out.println("List " + deserializedObject.getTestInnerClass().getTestListFieldInteger());

        System.out.println("\n  -- </YAML DESERIALIZER> --  \n");
    }

    public void testDeserializerType() {
        System.out.println("  -- <YAML DESERIALIZER TYPE> --  \n");

        TestYamlTypeObject testYamlTypeObject = new TestYamlTypeObject();
        testYamlTypeObject.printAll();
        YamlDocument document = YamlDocument.generateFromClass(testYamlTypeObject);

        document.set(YamlPath.fromGlobalPath("byteValueField"), 0);
        document.set(YamlPath.fromGlobalPath("test.attempt"), 0);
        //document.retrieveAnyObject(YamlPath.fromGlobalPath("byteValueField"), YamlNode.class).setValue(0);
        //System.out.println(document.retrieveAnyObject(YamlPath.fromGlobalPath("byteValueField"), YamlNode.class).getValue());

        String serializedDocument = document.serialize();
        System.out.println(serializedDocument);
        //TestYamlTypeObject deserializedObject = new YamlDeserializer<>(serializedDocument, TestYamlTypeObject.class).retrieveClass();
        //deserializedObject.printAll();


        System.out.println("\n  -- </YAML DESERIALIZER TYPE> --  \n");
    }
}