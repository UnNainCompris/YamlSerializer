package fr.evermon;

import fr.evermon.yaml.object.YamlEmptyNode;
import fr.evermon.yaml.object.YamlList;
import fr.evermon.yaml.object.YamlNode;
import fr.evermon.yaml.object.impl.YamlObject;
import fr.evermon.yaml.object.map.YamlEntry;
import fr.evermon.yaml.object.map.YamlEntryKey;
import fr.evermon.yaml.object.map.YamlEntryValue;
import fr.evermon.yaml.object.map.YamlMap;
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
        YamlMap<YamlEntryKey<String>, YamlEntryValue<String>> yamlMap = new YamlMap<>("MapNode");
        rootNode.addChild(yamlMap);

        for(int i = 1 ; i <= 5 ; i++) {
            YamlEntryKey<String> newKeyEntry = new YamlEntryKey<>("Here a Key {" + i + "}");
            YamlEntryValue<String> newValueEntry = new YamlEntryValue<>("Here a value {" + i + "}");

            YamlEntry<YamlEntryKey<String>, YamlEntryValue<String>> newEntry = new YamlEntry<>(yamlMap);
            newEntry.setEntryKey(newKeyEntry);
            newEntry.setEntryValue(newValueEntry);
            yamlMap.addEntry(newEntry);
        }

        System.out.println(rootNode.serialize(0));

        System.out.println("\n  -- </YAML MAP> --  \n");
    }
}