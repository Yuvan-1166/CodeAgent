/*
 * Java Code-Generation Agent - Starting Stage
 * This agent reads a prompt from the user, calls the OpenAI API to generate Java code,
 * and writes the generated code into a .java file on disk.
 *
 * Dependencies (Maven):
 *
 * <dependency>
 *   <groupId>com.squareup.okhttp3</groupId>
 *   <artifactId>okhttp</artifactId>
 *   <version>4.9.3</version>
 * </dependency>
 * <dependency>
 *   <groupId>com.fasterxml.jackson.core</groupId>
 *   <artifactId>jackson-databind</artifactId>
 *   <version>2.12.5</version>
 * </dependency>
 */

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CodeAgent { // Replace with your OpenAI API key
    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your Java coding problem prompt:");
        String prompt = scanner.nextLine();

        String generatedCode = generateJavaCode(prompt);
        System.out.println("\n--- Generated Code ---\n" + generatedCode);

        // Write to file
        try (FileWriter writer = new FileWriter("GeneratedSolution.java")) {
            writer.write(generatedCode);
            System.out.println("\nJava code written to GeneratedSolution.java");
        }
    }

    private static String generateJavaCode(String prompt) throws IOException {
        // Build request payload
        String jsonPayload = objectMapper.writeValueAsString(
            new ChatRequest(
                "gpt-4", 
                new Message[]{ new Message("system", "You are an expert Java developer. Write a complete Java class based on the user prompt."),
                                new Message("user", prompt) }
            )
        );

        RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
            .url(OPENAI_URL)
            .header("Authorization", "Bearer " + OPENAI_API_KEY)
            .post(body)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            // Parse response
            ChatResponse chatResponse = objectMapper.readValue(response.body().string(), ChatResponse.class);
            return chatResponse.getChoices()[0].getMessage().getContent();
        }
    }

    // Helper classes for JSON mapping
    static class ChatRequest {
        public String model;
        public Message[] messages;
        public ChatRequest(String model, Message[] messages) {
            this.model = model;
            this.messages = messages;
        }
    }

    static class Message {
        public String role;
        public String content;
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
        public String getContent() {
          return content;
        }
    }

    static class ChatResponse {
        private Choice[] choices;
        public Choice[] getChoices() { return choices; }
        public void setChoices(Choice[] choices) { this.choices = choices; }
    }

    static class Choice {
        private Message message;
        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }
    }
}

