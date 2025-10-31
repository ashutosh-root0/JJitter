package com.encoder;

import com.encoder.core.LineEncoder;
import com.encoder.core.LineEncoder.Scheme;
import com.encoder.core.PalindromeFinder;
import com.encoder.core.Scrambler;
import com.encoder.graphics.SignalChartPanel; // This is the new Swing panel

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * A Swing-based UI for the Digital Line Encoding Simulator.
 * This class replaces the original console-based Main.java.
 */
public class SwingMain {

    private JFrame frame;
    private JTextField dataField;
    private JComboBox<String> schemeComboBox;
    private JComboBox<String> scrambleComboBox;
    private JLabel scrambleLabel;
    private JButton generateButton;
    private JTextArea resultsArea;
    private SignalChartPanel digitalChartPanel;
    private SignalChartPanel analogChartPanel;

    public static void main(String[] args) {
        // Run the UI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SwingMain().createAndShowGUI();
            }
        });
    }

    private void createAndShowGUI() {
        frame = new JFrame("Digital and Analog Signal Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null); // Center window

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- Tab 1: Digital Line Encoding ---
        tabbedPane.addTab("Digital Line Encoding", createDigitalPanel());

        // --- Tab 2: Analog Signal Demo ---
        tabbedPane.addTab("Analog Signals (Demo)", createAnalogPanel());

        frame.getContentPane().add(tabbedPane);
        frame.setVisible(true);
    }

    private JPanel createDigitalPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Input Panel ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Inputs"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Data Stream
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Data Stream (e.g., 010011000000001):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dataField = new JTextField("010011000000001");
        inputPanel.add(dataField, gbc);

        // Encoding Scheme
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        inputPanel.add(new JLabel("Encoding Scheme:"), gbc);

        gbc.gridx = 1;
        String[] schemes = {"NRZ-L", "NRZ-I", "Manchester", "Differential Manchester", "AMI"};
        schemeComboBox = new JComboBox<>(schemes);
        inputPanel.add(schemeComboBox, gbc);

        // Scrambling (for AMI)
        gbc.gridx = 0;
        gbc.gridy = 2;
        scrambleLabel = new JLabel("Scrambling:");
        inputPanel.add(scrambleLabel, gbc);

        gbc.gridx = 1;
        String[] scrambles = {"None", "B8ZS", "HDB3"};
        scrambleComboBox = new JComboBox<>(scrambles);
        inputPanel.add(scrambleComboBox, gbc);

        // Initially hide scrambling
        scrambleLabel.setVisible(false);
        scrambleComboBox.setVisible(false);

        // Generate Button
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        generateButton = new JButton("Generate Plot");
        inputPanel.add(generateButton, gbc);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // --- Center Panel (Results and Chart) ---
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        // Results Area
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultsArea.setBorder(BorderFactory.createTitledBorder("Results"));
        centerPanel.add(new JScrollPane(resultsArea));

        // Chart Panel
        digitalChartPanel = new SignalChartPanel();
        digitalChartPanel.setBorder(BorderFactory.createTitledBorder("Signal Plot"));
        centerPanel.add(digitalChartPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- Event Listeners ---
        schemeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isAMI = "AMI".equals(schemeComboBox.getSelectedItem());
                scrambleLabel.setVisible(isAMI);
                scrambleComboBox.setVisible(isAMI);
            }
        });

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateDigitalPlot();
            }
        });

        // Initial plot
        generateDigitalPlot();

        return mainPanel;
    }

    private JPanel createAnalogPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.add(new JLabel("Analog signal generation (PCM, DM) logic was not implemented in the provided project files."));

        JButton demoButton = new JButton("Show Analog Demo (Sine Wave)");
        infoPanel.add(demoButton);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        analogChartPanel = new SignalChartPanel();
        analogChartPanel.setBorder(BorderFactory.createTitledBorder("Analog Signal (Demo)"));
        mainPanel.add(analogChartPanel, BorderLayout.CENTER);

        demoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Generate a demo sine wave
                List<Double> sineWave = new ArrayList<>();
                for (int i = 0; i < 400; i++) {
                    sineWave.add(Math.sin(i * Math.PI / 50.0) * 1.5); // 1.5V amplitude
                }
                analogChartPanel.setAnalogSignal(sineWave, "Demo Sine Wave");
            }
        });

        // Initial demo plot
        demoButton.doClick();

        return mainPanel;
    }

    private void generateDigitalPlot() {
        String digitalData = dataField.getText().trim();
        if (digitalData.isEmpty() || !digitalData.matches("[01]+")) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid binary string (0s and 1s only).", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedSchemeName = (String) schemeComboBox.getSelectedItem();
        String selectedScramble = (String) scrambleComboBox.getSelectedItem();

        Scheme selectedScheme = null;
        String plotTitle = selectedSchemeName;
        String dataForEncoding = digitalData;
        String scrambledData = "";

        switch (selectedSchemeName) {
            case "NRZ-L": selectedScheme = Scheme.NRZ_L; break;
            case "NRZ-I": selectedScheme = Scheme.NRZ_I; break;
            case "Manchester": selectedScheme = Scheme.MANCHESTER; break;
            case "Differential Manchester": selectedScheme = Scheme.DIFF_MANCHESTER; break;
            case "AMI":
                selectedScheme = Scheme.AMI;
                if ("B8ZS".equals(selectedScramble)) {
                    scrambledData = Scrambler.b8zs(digitalData);
                    dataForEncoding = scrambledData;
                    plotTitle = "AMI with B8ZS";
                } else if ("HDB3".equals(selectedScramble)) {
                    scrambledData = Scrambler.hdb3(digitalData);
                    dataForEncoding = scrambledData;
                    plotTitle = "AMI with HDB3";
                }
                break;
        }

        // --- Process and Output ---
        StringBuilder results = new StringBuilder();
        results.append("Original Data:    ").append(digitalData).append("\n");
        if (!scrambledData.isEmpty()) {
            results.append("Scrambled Data:   ").append(scrambledData).append("\n");
        }

        // Longest Palindrome (on original data)
        String longestPalindrome = PalindromeFinder.findLongestPalindrome(digitalData);
        results.append("Longest Palindrome: ").append(longestPalindrome).append("\n");

        resultsArea.setText(results.toString());

        // --- Encoding and Plotting ---
        List<Double> signalLevels = LineEncoder.encode(dataForEncoding, selectedScheme);

        // Pass data to the panel and repaint
        // Pass the *original* data string for bit labeling,
        // but the scrambled data for encoding (if AMI)
        digitalChartPanel.setDigitalSignal(signalLevels, digitalData, dataForEncoding, plotTitle);
    }
}