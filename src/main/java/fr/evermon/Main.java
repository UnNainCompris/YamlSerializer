package fr.evermon;

import fr.evermon.yaml.oldobject.node.YamlEmptyNode;
import fr.evermon.yaml.oldobject.node.list.YamlList;
import fr.evermon.yaml.oldobject.node.YamlNode;
import fr.evermon.yaml.oldobject.impl.YamlObject;
import fr.evermon.yaml.oldobject.node.map.YamlMap;
import fr.evermon.yaml.utils.IndentationUtils;

public class Main {
    public void start() {
        testIndentationUtils();
        testYamlNode();
        testYamlList();
        testYamlMap();
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

        YamlEmptyNode rootNode = new YamlEmptyNode("RootNode");
        YamlObject lastNode = rootNode;
        for(int i = 1 ; i <= 5 ; i++) {
            YamlEmptyNode newNode = new YamlEmptyNode("ChildNode{" + i + "}");
            lastNode.addChild(newNode);
            lastNode = newNode;
        }
        lastNode.addChild(new YamlNode<>("LastNode", "Here a value"));

        System.out.println(rootNode.serialize(0));

        System.out.println("\n  -- </YAML NODE> --  \n");
    }

    public void testYamlList() {
        System.out.println("  -- <YAML LIST> --  \n");

        YamlEmptyNode rootNode = new YamlEmptyNode("RootNode");
        YamlList<YamlNode<String>> listNode = new YamlList<>("ListNode");
        rootNode.addChild(listNode);

        for(int i = 1 ; i <= 5 ; i++) {
            YamlNode<String> newNode = new YamlNode<>("ChildNode{" + i + "}", "Here a value {" + i + "}");
            listNode.add(newNode);
        }

        System.out.println(rootNode.serialize(0));

        System.out.println("\n  -- </YAML LIST> --  \n");
    }

    public void testYamlMap() {
        System.out.println("  -- <YAML MAP> --  \n");

        YamlEmptyNode rootNode = new YamlEmptyNode("RootNode");
        YamlMap<String, Integer> yamlMap = new YamlMap<>("MapNode");
        rootNode.addChild(yamlMap);

        for(int i = 1 ; i <= 5 ; i++) {
            yamlMap.addEntry("Here a Key {" + i + "}", i);
        }

        System.out.println(rootNode.serialize(0));

        System.out.println("\n  -- </YAML MAP> --  \n");
    }
}