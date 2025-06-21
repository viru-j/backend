package com.example.assistant;

/**
 * Utility class to craft prompts for the Spirare API based on requested tasks.
 */
public class PromptCreator {

    /**
     * Creates a prompt for analysing a code snippet.
     * @param snippet the code snippet to analyze
     * @param instruction specific instruction for the model
     * @return combined prompt
     */
    public String createAnalysisPrompt(String snippet, String instruction) {
        return instruction + "\n\n" + snippet;
    }
}
