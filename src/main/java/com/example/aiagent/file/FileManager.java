package com.example.aiagent.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileManager {
    /**
     * Creates a new file at the given path with a basic template based on language.
     *
     * @param filePath the file to create
     * @param language the programming language (e.g., "python", "java", "javascript")
     * @throws IOException if file operations fail
     */
    public void createFileWithTemplate(Path filePath, String language) throws IOException {
        if (Files.exists(filePath)) {
            throw new IOException("File already exists: " + filePath);
        }
        Files.createDirectories(filePath.getParent());
        String template = getTemplate(language, filePath.getFileName().toString());
        Files.writeString(filePath, template, StandardOpenOption.CREATE_NEW);
    }

    /**
     * Returns a basic file template for the given language.
     *
     * @param language the programming language
     * @param fileName the target file name
     * @return template string
     */
    private String getTemplate(String language, String fileName) {
        switch (language.toLowerCase()) {
            case "python":
                return "#!/usr/bin/env python3\n\n" +
                       "def main():\n" +
                       "    pass\n\n" +
                       "if __name__ == \"__main__\":\n" +
                       "    main()\n";
            case "java":
                String className = fileName.replaceAll("\\.java$", "");
                return "public class " + className + " {\n" +
                       "    public static void main(String[] args) {\n" +
                       "        // TODO: implement\n" +
                       "    }\n" +
                       "}\n";
            case "javascript":
            case "js":
                return "#!/usr/bin/env node\n\n" +
                       "// TODO: implement\n";
            default:
                return "// Unknown language: " + language + "\n";
        }
    }
}
