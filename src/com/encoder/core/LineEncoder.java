package com.encoder.core;

import java.util.ArrayList;
import java.util.List;

public class LineEncoder {

    public enum Scheme {
        NRZ_L,
        NRZ_I,
        MANCHESTER,
        DIFF_MANCHESTER,
        AMI
    }

    /**
     * Main encoding function.
     * Note: For AMI, this expects a pre-scrambled string if applicable.
     * It handles '0', '1', '+', and '-'.
     */
    public static List<Double> encode(String data, Scheme scheme) {
        switch (scheme) {
            case NRZ_L:
                return nrzl(data);
            case NRZ_I:
                return nrzi(data);
            case MANCHESTER:
                return manchester(data);
            case DIFF_MANCHESTER:
                return diffManchester(data);
            case AMI:
                return ami(data);
            default:
                return new ArrayList<>();
        }
    }

    // NRZ-L: 0 = High (+1), 1 = Low (-1)
    private static List<Double> nrzl(String data) {
        List<Double> levels = new ArrayList<>();
        for (char bit : data.toCharArray()) {
            levels.add(bit == '0' ? 1.0 : -1.0);
        }
        return levels;
    }

    // NRZ-I: 0 = No change, 1 = Invert
    private static List<Double> nrzi(String data) {
        List<Double> levels = new ArrayList<>();
        double currentLevel = 1.0; // Start at High
        for (char bit : data.toCharArray()) {
            if (bit == '1') {
                currentLevel *= -1.0; // Invert
            }
            levels.add(currentLevel);
        }
        return levels;
    }

    // Manchester: 0 = High-to-Low, 1 = Low-to-High
    private static List<Double> manchester(String data) {
        List<Double> levels = new ArrayList<>();
        for (char bit : data.toCharArray()) {
            if (bit == '0') {
                levels.add(1.0);
                levels.add(-1.0);
            } else {
                levels.add(-1.0);
                levels.add(1.0);
            }
        }
        return levels;
    }

    // Differential Manchester:
    // Always a mid-bit transition. 0 = Transition at start, 1 = No transition at start.
    private static List<Double> diffManchester(String data) {
        List<Double> levels = new ArrayList<>();
        double currentLevel = 1.0; // Start high
        
        for (char bit : data.toCharArray()) {
            if (bit == '0') {
                // 0: Transition at start
                currentLevel *= -1.0; 
            }
            // else 1: No transition at start, currentLevel remains the same
            
            // Mid-bit transition (for clocking)
            levels.add(currentLevel);
            currentLevel *= -1.0;
            levels.add(currentLevel);
        }
        return levels;
    }

    // AMI: 0 = Zero (0), 1 = Alternate +1 and -1
    // This version also handles pre-scrambled '+', '-' from B8ZS/HDB3
    private static List<Double> ami(String data) {
        List<Double> levels = new ArrayList<>();
        boolean isLastPulsePositive = false; // Start with negative pulse for first '1'
        
        for (char bit : data.toCharArray()) {
            switch (bit) {
                case '0':
                    levels.add(0.0);
                    break;
                case '1':
                    // Regular AMI '1'
                    if (isLastPulsePositive) {
                        levels.add(-1.0);
                        isLastPulsePositive = false;
                    } else {
                        levels.add(1.0);
                        isLastPulsePositive = true;
                    }
                    break;
                case '+':
                    // Scrambled pulse (Force Positive)
                    levels.add(1.0);
                    isLastPulsePositive = true;
                    break;
                case '-':
                    // Scrambled pulse (Force Negative)
                    levels.add(-1.0);
                    isLastPulsePositive = false;
                    break;
            }
        }
        return levels;
    }
}