package com.encoder.core;

public class PalindromeFinder {

    /**
     * Finds the longest palindromic substring in O(n) time using Manacher's Algorithm.
     */
    public static String findLongestPalindrome(String s) {
        if (s == null || s.isEmpty()) return "";

        // Transform s to t: "aba" -> "#a#b#a#"
        char[] t = new char[s.length() * 2 + 1];
        for (int i = 0; i < s.length(); i++) {
            t[i * 2] = '#';
            t[i * 2 + 1] = s.charAt(i);
        }
        t[t.length - 1] = '#';

        // P[i] = length of palindrome centered at i
        int[] P = new int[t.length];
        int C = 0, R = 0; // Center, Right boundary

        for (int i = 1; i < t.length - 1; i++) {
            int i_mirror = 2 * C - i; // mirror of i

            // If i is within the current palindrome, P[i] is at least min(R-i, P[i_mirror])
            P[i] = (R > i) ? Math.min(R - i, P[i_mirror]) : 0;

            // Attempt to expand palindrome centered at i
            try {
                while (t[i + 1 + P[i]] == t[i - 1 - P[i]]) {
                    P[i]++;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                // End of string, stop expanding
            }

            // If palindrome centered at i expands past R,
            // adjust center C and right boundary R
            if (i + P[i] > R) {
                C = i;
                R = i + P[i];
            }
        }

        // Find the maximum element in P
        int maxLen = 0;
        int centerIndex = 0;
        for (int i = 1; i < t.length - 1; i++) {
            if (P[i] > maxLen) {
                maxLen = P[i];
                centerIndex = i;
            }
        }
        
        // Convert back to original string
        int start = (centerIndex - maxLen) / 2;
        return s.substring(start, start + maxLen);
    }
}