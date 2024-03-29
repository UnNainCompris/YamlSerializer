package fr.eris.yaml.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils {

    public static void writeFile(File file, String newFileContent) {
        if(file.isDirectory()) throw new IllegalArgumentException("The file cannot be a directory ! " + file.getName());

        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            writer.write(newFileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(File file) {
        return readFile(file, true);
    }
    
    public static String readFile(File file, boolean withLineSeparator) {
        if(file.isDirectory()) return "";
        
        StringBuilder currentContent = new StringBuilder();
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    Files.newInputStream(file.toPath()), StandardCharsets.UTF_8));
            String currentLine;
            while((currentLine = reader.readLine()) != null) {
                currentContent.append(currentLine);
                if(withLineSeparator) currentContent.append(System.lineSeparator());
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return currentContent.toString();
    }
}
