package com.payinsight.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class XmlResourceUtil {

    public static InputStream open(String path) throws Exception {

        // First, search inside the application resources.
        InputStream input = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(path);

        if (input != null) {
            return input;
        }

        // If not found, try the original project directory.
        File file = new File(path);

        if (file.exists()) {
            return new FileInputStream(file);
        }

        throw new FileNotFoundException(
                "Resource not found: " + path
        );
    }
}