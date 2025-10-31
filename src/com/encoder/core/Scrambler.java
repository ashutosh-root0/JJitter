package com.encoder.core;

public class Scrambler {

    /**
     * Scrambles a data stream using B8ZS.
     * Replaces "00000000" with a pattern based on the last '1' pulse.
     * We use '+' and '-' to represent the polarity, which the AMI encoder understands.
     * @return Scrambled string
     */
    public static String b8zs(String data) {
        StringBuilder scrambled = new StringBuilder();
        char lastPolarity = '-'; // Assume last pulse was negative
        int zeroCount = 0;

        for (int i = 0; i < data.length(); i++) {
            char bit = data.charAt(i);

            if (bit == '1') {
                zeroCount = 0;
                lastPolarity = (lastPolarity == '-') ? '+' : '-';
                scrambled.append(lastPolarity);
            } else { // bit == '0'
                zeroCount++;
                scrambled.append('0');

                if (zeroCount == 8) {
                    // 8 zeros found. We need to replace them.
                    // Backtrack 8 characters
                    scrambled.setLength(scrambled.length() - 8);
                    
                    if (lastPolarity == '-') {
                        // Pattern: 000+-0-+
                        scrambled.append("000+-0-+");
                    } else { // lastPolarity == '+'
                        // Pattern: 000-+0+-
                        scrambled.append("000-+0+-");
                    }
                    // B8ZS does not change the polarity state for the next '1'
                    zeroCount = 0;
                }
            }
        }
        return scrambled.toString();
    }

    /**
     * Scrambles a data stream using HDB3.
     * Replaces "0000" based on last '1' polarity AND parity of '1's since last sub.
     * @return Scrambled string
     */
    public static String hdb3(String data) {
        StringBuilder scrambled = new StringBuilder();
        char lastPolarity = '-';
        int zeroCount = 0;
        int onesSinceLastSub = 0;

        for (int i = 0; i < data.length(); i++) {
            char bit = data.charAt(i);

            if (bit == '1') {
                zeroCount = 0;
                onesSinceLastSub++;
                lastPolarity = (lastPolarity == '-') ? '+' : '-';
                scrambled.append(lastPolarity);
            } else { // bit == '0'
                zeroCount++;
                scrambled.append('0');

                if (zeroCount == 4) {
                    // 4 zeros found. Backtrack 4 chars.
                    scrambled.setLength(scrambled.length() - 4);
                    
                    if (onesSinceLastSub % 2 == 1) {
                        // Odd '1's: Replace with 000V
                        if (lastPolarity == '-') {
                            scrambled.append("000-");
                            lastPolarity = '-'; // V = -
                        } else {
                            scrambled.append("000+");
                            lastPolarity = '+'; // V = +
                        }
                    } else {
                        // Even '1's: Replace with B00V
                        if (lastPolarity == '-') {
                            scrambled.append("+00+"); // B = +, V = +
                            lastPolarity = '+';
                        } else {
                            scrambled.append("-00-"); // B = -, V = -
                            lastPolarity = '-';
                        }
                    }
                    zeroCount = 0;
                    onesSinceLastSub = 0; // Reset parity count
                }
            }
        }
        return scrambled.toString();
    }
}