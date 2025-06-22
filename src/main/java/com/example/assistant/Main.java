package com.example.assistant;

public class Main {
    public static void main(String[] args) {
        String endpoint = args.length > 0 ? args[0] : "http://localhost:8080/api";
        AssistantGUI.setEndpoint(endpoint);
        javafx.application.Application.launch(AssistantGUI.class, args);
    }
}
