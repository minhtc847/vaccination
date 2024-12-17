package com.vaccination.BE.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
@Component
public class GeneratePassword {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String ALL_CHARACTERS = LOWERCASE + UPPERCASE + DIGITS;
    private static final int PASSWORD_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        // Add at least one lowercase character
        password.append(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));

        // Add at least one uppercase character
        password.append(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));

        // Add at least one digit
        password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));

        // Fill the rest of the password with random characters
        for (int i = 3; i < PASSWORD_LENGTH; i++) {
            password.append(ALL_CHARACTERS.charAt(RANDOM.nextInt(ALL_CHARACTERS.length())));
        }

        // Shuffle the characters to ensure randomness
        return shuffleString(password.toString());
    }

    private static String shuffleString(String string) {
        char[] characters = string.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int index = RANDOM.nextInt(i + 1);
            char temp = characters[index];
            characters[index] = characters[i];
            characters[i] = temp;
        }
        return new String(characters);
    }
}
