package io.penguinstats.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

/**
 * @author ：yamika
 * @date ：Created in 2020/5/17 12:50
 * @description： Read static resource
 * @modified By：yamika
 */
public class FileReaderUtil {

    public static  String readJsonFile(String fileurl) throws IOException {
        Resource resource = new ClassPathResource(fileurl);
        return readJsonFile(resource);
    }

    public static String readJsonFile(Resource resource) throws IOException{
        File sourceFile = resource.getFile();
        BufferedReader reader = new BufferedReader(new java.io.FileReader(sourceFile));
        StringBuilder builder = new StringBuilder();
        String currentLine = reader.readLine();
        while (currentLine != null) {
            builder.append(currentLine).append("\n");
            currentLine = reader.readLine();
        }
        reader.close();
        return builder.toString();
    }
}
