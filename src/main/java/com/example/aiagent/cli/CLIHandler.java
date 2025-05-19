package com.example.aiagent.cli;

import com.example.aiagent.file.FileManager;
import com.example.aiagent.util.LanguageDetector;
import com.example.aiagent.llm.LLMClient;
import com.example.aiagent.llm.LlamaCLIClient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicBoolean;

public class CLIHandler {
    private FileManager fileManager;
    private LLMClient llmClient;

    public CLIHandler() {
        this.fileManager = new FileManager();
        // Initialize LLM client with model path from env variable
        String modelPath = System.getenv("LLAMA_MODEL_PATH");
        if (modelPath != null && Files.exists(Paths.get(modelPath))) {
            this.llmClient = new LlamaCLIClient(modelPath);
        } else {
            this.llmClient = null; // LLM not configured
        }
    }

    public void handle(String[] args) {
        if (args.length < 2) {
            printUsage();
            return;
        }
        String command = args[0];
        switch (command.toLowerCase()) {
            case "scaffold":
                handleScaffold(args);
                break;
            case "generate":
                handleGenerate(args);
                break;
            default:
                System.err.println("Unknown command: " + command);
                printUsage();
        }
    }

    private void handleScaffold(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: scaffold <language> <filePath>");
            return;
        }
        String language = args[1];
        String filePathStr = args[2];
        if (language.equalsIgnoreCase("auto")) {
            language = LanguageDetector.detectLanguage(filePathStr);
            if (language == null) {
                System.err.println("Could not detect language for file: " + filePathStr);
                return;
            }
        }
        Path filePath = Paths.get(filePathStr);
        try {
            fileManager.createFileWithTemplate(filePath, language);
            System.out.println("Created " + filePath + " with " + language + " template.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void handleGenerate(String[] args) {
        if (llmClient == null) {
            System.err.println("LLM client not configured. Set LLAMA_MODEL_PATH environment variable.");
            return;
        }
        if (args.length < 3) {
            System.err.println("Usage: generate <filePath> <prompt>");
            return;
        }
        String filePathStr = args[2];
        Path filePath = Paths.get(filePathStr);
        String prompt = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));

        AtomicBoolean load = new AtomicBoolean(true);
        try {
          Thread Loader = new Thread(() -> {
              String baseText = new String("Generating");
              int dots = 0;

              while(load.get()) {
                dots = dots%3 + 1;
                
                String line = "\r" + baseText + ".".repeat(dots) + "   ";
                System.out.print(line);
                System.out.flush();

                try {
                  Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
              }
              System.out.println("\r");
          });

          Loader.start();
            String generatedCode = llmClient.generateCode(prompt);
          load.set(false);
          try {
            Loader.join();
            Thread.sleep(500);
          }catch(InterruptedException e) {
            Thread.currentThread().interrupt();
          }
            System.out.println("Writing to the file " + filePath);
            Files.writeString(filePath, generatedCode, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Generated code written to " + filePath);
        } catch (Exception e) {
            load.set(false);
            System.err.println("\n Error during code generation: " + e.getMessage());
        }
    }

    private void printUsage() {
        System.out.println("Usage:");
        System.out.println("  scaffold <language|auto> <filePath>   - Create new file with template");
        System.out.println("  generate <filePath> <prompt>        - Generate code via LLM into file");
    }
}
