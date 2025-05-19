package com.example.aiagent.llm;

/**
 * Interface for interacting with a local open-source LLM.
 */
public interface LLMClient {
    /**
     * Generates code based on the given prompt.
     * @param prompt The prompt to send to the model.
     * @return The generated code as a String.
     * @throws Exception on inference failure.
     */
    String generateCode(String prompt) throws Exception;
}

