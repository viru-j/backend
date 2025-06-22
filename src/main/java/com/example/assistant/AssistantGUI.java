package com.example.assistant;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;



import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * JavaFX based UI for interacting with the assistant.
 */
public class AssistantGUI extends Application {
    private static String endpoint = "http://localhost:8080/api";

    private SpirareClient client;
    private final ProjectStructureAnalyzer analyzer = new ProjectStructureAnalyzer();
    private final JavaFileAnalyzer fileAnalyzer = new JavaFileAnalyzer();

    private final CodeSearcher codeSearcher = new CodeSearcher();
    private final CodeChunker chunker = new CodeChunker();


    public static void setEndpoint(String ep) {
        endpoint = ep;
    }

    @Override
    public void start(Stage stage) {
        client = new SpirareClient(endpoint);

        TextArea promptArea = new TextArea();
        TextArea responseArea = new TextArea();
        Button sendButton = new Button("Send");
        Button scanButton = new Button("Scan Project");
        Button showStructureButton = new Button("Show Structure");
        Button analyzeButton = new Button("Analyze File");
        Button testButton = new Button("Generate Tests");

        TextField searchField = new TextField();
        searchField.setPromptText("Search keyword");
        Button searchButton = new Button("Search");
        Button chunkButton = new Button("Chunk File");

        sendButton.setOnAction(e -> {

            try {
                String result = client.sendPrompt(promptArea.getText());
                responseArea.setText(result);
            } catch (IOException | InterruptedException ex) {

                responseArea.setText("Error: " + ex.getMessage());
            }
        });

        scanButton.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            var dir = chooser.showDialog(stage);
            if (dir != null) {
                try {
                    Path output = Path.of("project_structure.txt");
                    analyzer.scan(dir.getAbsolutePath(), output);
                    responseArea.setText("Project structure saved to " + output);
                } catch (IOException ex) {
                    responseArea.setText("Scan failed: " + ex.getMessage());
                }
            }
        });

        showStructureButton.setOnAction(e -> {
            String last = analyzer.getLastScan();
            if (last == null) {
                responseArea.setText("No project scanned yet.");
            } else {
                responseArea.setText(last);
            }
        });

        analyzeButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java", "*.java"));
            var file = chooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    fileAnalyzer.parse(Paths.get(file.getAbsolutePath()));
                    String methods = String.join("\n", fileAnalyzer.listMethodSignatures());
                    responseArea.setText(methods);
                } catch (Exception ex) {
                    responseArea.setText("Analyze failed: " + ex.getMessage());
                }
            }
        });

        testButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java", "*.java"));
            var file = chooser.showOpenDialog(stage);
            if (file != null) {
                DirectoryChooser dirChooser = new DirectoryChooser();
                dirChooser.setTitle("Select Project Root");
                var projectDir = dirChooser.showDialog(stage);
                if (projectDir != null) {
                    try {
                        new JUnitTestGenerator().generateTests(
                                Paths.get(file.getAbsolutePath()),
                                Paths.get(projectDir.getAbsolutePath()));
                        responseArea.setText("Test generated for " + file.getName());
                    } catch (IOException ex) {
                        responseArea.setText("Test generation failed: " + ex.getMessage());
                    }
                }
            }
        });


        searchButton.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            var dir = chooser.showDialog(stage);
            if (dir != null) {
                try {
                    var results = codeSearcher.search(dir.toPath(), searchField.getText());
                    String text = results.isEmpty() ? "No matches" : String.join("\n", results.stream().map(Path::toString).toList());
                    responseArea.setText(text);
                } catch (IOException ex) {
                    responseArea.setText("Search failed: " + ex.getMessage());
                }
            }
        });

        chunkButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java", "*.java"));
            var file = chooser.showOpenDialog(stage);
            if (file != null) {
                var chunks = chunker.chunkMethods(file.toPath());
                String text = String.join("\n\n", chunks.values());
                responseArea.setText(text);
            }
        });

        HBox buttons = new HBox(10, sendButton, scanButton, showStructureButton, analyzeButton, testButton, searchField, searchButton, chunkButton);

        VBox center = new VBox(10, promptArea, buttons, responseArea);
        center.setPadding(new Insets(10));

        Scene scene = new Scene(new BorderPane(center), 800, 600);
        stage.setTitle("Local Code Assistant");
        stage.setScene(scene);
        stage.show();

    }
}
