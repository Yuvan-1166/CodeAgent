package com.example.aiagent.util;

import java.util.HashMap;
import java.util.Map;

public class LanguageDetector {
    private static final Map<String, String> EXTENSION_MAP = new HashMap<>();

    static {
        EXTENSION_MAP.put(".py", "python");
        EXTENSION_MAP.put(".java", "java");
        EXTENSION_MAP.put(".js", "javascript");
        // Add more mappings as needed
    }

    /**
     * Detects language from file extension.
     * @param fileName the name of the file
     * @return detected language or null if unknown
     */
    public static String detectLanguage(String fileName) {
        for (String ext : EXTENSION_MAP.keySet()) {
            if (fileName.endsWith(ext)) {
                return EXTENSION_MAP.get(ext);
            }
        }
        return null;
    }
}
