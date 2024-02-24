package fr.eris;

import fr.eris.yaml.object.YamlDocument;
import fr.eris.yaml.object.impl.IYamlObject;
import fr.eris.yaml.object.node.YamlNode;
import fr.eris.yaml.object.node.iterable.list.YamlListNode;
import fr.eris.yaml.utils.IndentationUtils;

public class Main {
    public void start() {
        testIndentationUtils();
        testYamlNode();
        testYamlList();
        testYamlMap();
        testSerializer();
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
        testYamlObject.applyTestListField();
        testYamlObject.applyInnerClass();
        testYamlObject.getTestInnerClass().setTestFieldSecond("TestInnerSecond");
        testYamlObject.getTestInnerClass().setTestFieldFirst("TestInnerFirst");
        YamlDocument document = YamlDocument.generateFromClass(testYamlObject);
        System.out.println(document.serialize());

        System.out.println("\n  -- </YAML SERIALIZER> --  \n");
    }
}