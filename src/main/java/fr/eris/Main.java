package fr.eris;

import fr.eris.objecttest.TestYamlObject;
import fr.eris.yaml.object.YamlDocument;
import fr.eris.yaml.object.impl.IYamlObject;
import fr.eris.yaml.object.node.YamlNode;
import fr.eris.yaml.object.node.iterable.list.YamlListNode;
import fr.eris.yaml.object.node.iterable.set.YamlSetNode;
import fr.eris.yaml.object.serialization.YamlDeserializer;
import fr.eris.yaml.utils.IndentationUtils;

public class Main {
    public void start() {
        testIndentationUtils();
        testYamlNode();
        testYamlList();
        testYamlSet();
        testYamlMap();
        testSerializer();
        testDeserializer();
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
        testYamlObject.applyTestListField();
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
        testYamlObject.applyTestListField();
        testYamlObject.applyTestSetField();
        testYamlObject.applyInnerClass();
        testYamlObject.getTestInnerClass().setTestFieldSecond("TestInnerSecond");
        testYamlObject.getTestInnerClass().setTestFieldFirst("TestInnerFirst");
        testYamlObject.getTestInnerClass().applyInnerClass();
        testYamlObject.applyInnerOtherClass();
        testYamlObject.getTestInnerClass().getTestInnerClass().setTestFieldFirst("TestDoubleInner1");
        testYamlObject.getTestInnerClass().getTestInnerClass().setTestFieldSecond("TestDoubleInner2");

        testYamlObject.setTestFieldFirst("HEREDESERIALIZED");
        YamlDocument document = YamlDocument.generateFromClass(testYamlObject);
        String serializedDocument = document.serialize();

        TestYamlObject deserializedObject = new YamlDeserializer<>(serializedDocument, TestYamlObject.class).retrieveClass();
        System.out.println(testYamlObject.getTestFieldFirst());
        //System.out.println("Default document: \n\n" + serializedDocument);
        //System.out.println("Deserialized document: \n\n" +
        //        new YamlDeserializer<>(serializedDocument, TestYamlObject.class).retrieveSerializedValue().toString().replace(", ", "\n"));

        System.out.println("\n  -- </YAML DESERIALIZER> --  \n");
    }
}