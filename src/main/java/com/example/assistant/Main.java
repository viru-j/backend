package com.example.assistant;

public class Main {
    public static void main(String[] args) {
        String endpoint = args.length > 0 ? args[0] : "http://localhost:8080/api";
        SpirareClient client = new SpirareClient(endpoint);
        AssistantGUI gui = new AssistantGUI(client);
        gui.start();
    }
}
