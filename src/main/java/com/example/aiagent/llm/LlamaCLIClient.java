package com.example.aiagent.llm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Implementation that invokes a local llama.cpp-based executable via command line.
 * Supports specifying the binary name via LLAMA_BIN_PATH (or defaults to "llama-cli").
 * Expects a model file path from LLAMA_MODEL_PATH environment variable.
 */
public class LlamaCLIClient implements LLMClient {
    private final String modelPath;
    private final String llamaBinary;

    public LlamaCLIClient(String modelPath) {
        this.modelPath = modelPath;
        String bin = System.getenv("LLAMA_BIN_PATH");
        if (bin != null && Files.exists(Paths.get(bin))) {
            this.llamaBinary = bin;
        } else {
            this.llamaBinary = "llama-cli"; // default executable name
        }
    }

    @Override
    public String generateCode(String prompt) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(
            llamaBinary,
            // "--quiet",
            // "--no-print-info",
            "-m", modelPath,
            "-p", prompt,
            "--threads", "4",
            // "-n", "256",
            "--single-turn",
            "--log-disable",
            "--no-display-prompt"
            // "2>/dev/null"
        );
        builder.redirectErrorStream(true);
        Process process = builder.start();

        try (BufferedReader reader = new BufferedReader(
                 new InputStreamReader(process.getInputStream()))) {
            String output = reader.lines().collect(Collectors.joining("\n"));
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("LLM process exited with code: " + exitCode);
            }
            return output;
        }
    }
}
