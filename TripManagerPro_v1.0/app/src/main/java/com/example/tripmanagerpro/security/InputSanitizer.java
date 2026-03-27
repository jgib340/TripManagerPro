package com.example.tripmanagerpro.security;

public final class InputSanitizer {

    private InputSanitizer() {
        // Utility class
    }

    public static String sanitizeText(String input, int maxLength) {
        if (input == null) {
            return "";
        }

        String cleaned = input
                .replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")
                .replaceAll("\\s+", " ")
                .trim();

        cleaned = cleaned.replace("<", "")
                .replace(">", "")
                .replace("{", "")
                .replace("}", "");

        if (cleaned.length() > maxLength) {
            cleaned = cleaned.substring(0, maxLength);
        }

        return cleaned;
    }

    public static String sanitizeDate(String input) {
        if (input == null) {
            return "";
        }

        String cleaned = input
                .replaceAll("[^0-9/]", "")
                .trim();

        if (cleaned.length() > 10) {
            cleaned = cleaned.substring(0, 10);
        }

        return cleaned;
    }

    public static boolean isBlank(String input) {
        return input == null || input.trim().isEmpty();
    }
}