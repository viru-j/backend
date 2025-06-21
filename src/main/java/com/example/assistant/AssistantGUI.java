package com.example.assistant;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.example.assistant.JavaFileAnalyzer;

/**
 * Basic Swing-based UI for interacting with the assistant.
 */
public class AssistantGUI {
    private final SpirareClient client;
    private final ProjectStructureAnalyzer analyzer = new ProjectStructureAnalyzer();
    private final JavaFileAnalyzer fileAnalyzer = new JavaFileAnalyzer();

    public AssistantGUI(SpirareClient client) {
        this.client = client;
    }

    public void start() {
        JFrame frame = new JFrame("Local Code Assistant");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTextArea promptArea = new JTextArea();
        JTextArea responseArea = new JTextArea();
        JButton sendButton = new JButton("Send");
        JButton scanButton = new JButton("Scan Project");
        JButton analyzeButton = new JButton("Analyze File");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(promptArea), BorderLayout.NORTH);
        panel.add(sendButton, BorderLayout.CENTER);
        panel.add(new JScrollPane(responseArea), BorderLayout.SOUTH);

        JPanel south = new JPanel(new FlowLayout());
        south.add(scanButton);
        south.add(analyzeButton);
        frame.add(panel, BorderLayout.CENTER);
        frame.add(south, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> {
            try {
                String result = client.sendPrompt(promptArea.getText());
                responseArea.setText(result);
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
                responseArea.setText("Error: " + ex.getMessage());
            }
        });

        scanButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    Path output = Path.of("project_structure.txt");
                    analyzer.scan(chooser.getSelectedFile().getAbsolutePath(), output);
                    JOptionPane.showMessageDialog(frame, "Project structure saved to " + output);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Scan failed: " + ex.getMessage());
                }
            }
        });

        analyzeButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Java", "java"));
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    fileAnalyzer.parse(Paths.get(chooser.getSelectedFile().getAbsolutePath()));
                    String methods = String.join("\n", fileAnalyzer.listMethodSignatures());
                    responseArea.setText(methods);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Analyze failed: " + ex.getMessage());
                }
            }
        });

        frame.setVisible(true);
    }
}
