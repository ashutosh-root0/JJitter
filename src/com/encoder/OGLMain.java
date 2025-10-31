package com.encoder;

import com.encoder.core.LineEncoder;
import com.encoder.core.LineEncoder.Scheme;
import com.encoder.core.PalindromeFinder;
import com.encoder.core.Scrambler;
import com.encoder.graphics.OGLSignalPlotter;

import java.util.List;
import java.util.Scanner;

public class OGLMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Digital Line Encoding Simulator ---");
        System.out.println("Select input type:");
        System.out.println("1. Digital Input (Provide a binary string)");
        System.out.println("2. Analog Input (PCM/DM) [Not Implemented]");
        int inputType = getIntInput(scanner, 1, 2);

        String digitalData = "";

        if (inputType == 1) {
            System.out.println("Enter your digital data stream (e.g., 010011000000001):");
            digitalData = scanner.next();
        } else {
            System.out.println("PCM/DM is not implemented. Please enter a digital string to proceed:");
            digitalData = scanner.next();
        }

        // --- Line Encoding ---
        System.out.println("Select Line Encoding Scheme:");
        System.out.println("1. NRZ-L");
        System.out.println("2. NRZ-I");
        System.out.println("3. Manchester");
        System.out.println("4. Differential Manchester");
        System.out.println("5. AMI");
        int schemeChoice = getIntInput(scanner, 1, 5);

        Scheme selectedScheme = null;
        String schemeName = "";
        String scrambledData = "";
        String dataForEncoding = digitalData;

        switch (schemeChoice) {
            case 1: selectedScheme = Scheme.NRZ_L; schemeName = "NRZ-L"; break;
            case 2: selectedScheme = Scheme.NRZ_I; schemeName = "NRZ-I"; break;
            case 3: selectedScheme = Scheme.MANCHESTER; schemeName = "Manchester"; break;
            case 4: selectedScheme = Scheme.DIFF_MANCHESTER; schemeName = "Diff. Manchester"; break;
            case 5: 
                selectedScheme = Scheme.AMI;
                schemeName = "AMI";
                System.out.println("Enable Scrambling for AMI? (yes/no)");
                if (scanner.next().equalsIgnoreCase("yes")) {
                    System.out.println("Select Scrambling Type:");
                    System.out.println("1. B8ZS");
                    System.out.println("2. HDB3");
                    int scrambleType = getIntInput(scanner, 1, 2);
                    
                    if (scrambleType == 1) {
                        scrambledData = Scrambler.b8zs(digitalData);
                        schemeName = "AMI with B8ZS";
                    } else {
                        scrambledData = Scrambler.hdb3(digitalData);
                        schemeName = "AMI with HDB3";
                    }
                    dataForEncoding = scrambledData; // Use scrambled data for AMI
                }
                break;
        }

        // --- Process and Output ---
        System.out.println("\n--- Results ---");
        System.out.println("Original Data: " + digitalData);
        if (!scrambledData.isEmpty()) {
            System.out.println("Scrambled Data:  " + scrambledData);
        }

        // Longest Palindrome (on original data)
        String longestPalindrome = PalindromeFinder.findLongestPalindrome(digitalData);
        System.out.println("Longest Palindrome: " + longestPalindrome);

        // --- Encoding and Plotting ---
        List<Double> signalLevels = LineEncoder.encode(dataForEncoding, selectedScheme);
        System.out.println("\nGenerating plot for: " + schemeName);
        
        // Launch the JOGL window
        OGLSignalPlotter.plot(signalLevels, schemeName + " | " + digitalData);

        scanner.close();
    }

    // Helper for robust int input
    private static int getIntInput(Scanner scanner, int min, int max) {
        int choice;
        while (true) {
            try {
                choice = scanner.nextInt();
                if (choice >= min && choice <= max) {
                    break;
                } else {
                    System.out.println("Invalid choice. Please enter a number between " + min + " and " + max);
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear invalid input
            }
        }
        return choice;
    }
}