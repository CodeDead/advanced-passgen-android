package com.codedead.advancedpassgen.domain;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class PasswordGenerator {

    /**
     * Generate a {@link List} of {@link PasswordItem} objects
     *
     * @param minLength            The minimum length of the password
     * @param maxLength            The maximum length of the password
     * @param amount               The amount of passwords to generate
     * @param useLowercase         Whether to use lowercase characters
     * @param useUppercase         Whether to use uppercase characters
     * @param useNumbers           Whether to use numbers
     * @param useSpecialCharacters Whether to use special characters
     * @param useBrackets          Whether to use brackets
     * @param useSpaces            Whether to use spaces
     * @param customCharacterSet   The custom character set to use
     * @return A {@link List} of {@link PasswordItem} objects
     */
    public static List<PasswordItem> generatePasswords(final int minLength, final int maxLength, final int amount, final boolean useLowercase, final boolean useUppercase, final boolean useNumbers, final boolean useSpecialCharacters, final boolean useBrackets, final boolean useSpaces, String customCharacterSet) {
        String characterSet = "";

        if (customCharacterSet == null || customCharacterSet.isEmpty()) {
            if (useLowercase) {
                characterSet = "abcdefghijklmnopqrstuvwxyz";
            }
            if (useUppercase) {
                characterSet += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            }
            if (useNumbers) {
                characterSet += "0123456789";
            }
            if (useSpecialCharacters) {
                characterSet += "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
            }
            if (useBrackets) {
                characterSet += "[](){}<>_";
            }
            if (useSpaces) {
                characterSet += " ";
            }
        } else {
            characterSet = customCharacterSet;
        }

        final SecureRandom secureRandom = new SecureRandom();
        final List<PasswordItem> items = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            final StringBuilder password = new StringBuilder();
            final int passwordLength = minLength + secureRandom.nextInt(maxLength - minLength + 1);
            for (int j = 0; j < passwordLength; j++) {
                final int randomIndex = secureRandom.nextInt(characterSet.length());
                password.append(characterSet.charAt(randomIndex));
            }
            final String passwordString = password.toString();
            items.add(new PasswordItem(passwordString, calculateStrength(passwordString)));
        }

        return items;
    }

    /**
     * Calculate the strength of a password
     *
     * @param password The password to calculate the strength of
     * @return The strength of the password
     */
    public static int calculateStrength(final String password) {
        int strength = 0;
        final int length = password.length();
        final boolean hasNumber = password.matches(".*\\d.*");
        final boolean hasLower = password.matches(".*[a-z].*");
        final boolean hasUpper = password.matches(".*[A-Z].*");
        final boolean hasSymbol = password.matches(".*[!@#€£µ$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
        final boolean hasRepetition = password.matches(".*([a-zA-Z0-9])\\1{2,}.*");

        if (length > 4) {
            strength += 10;
        }
        if (length > 8) {
            strength += 10;
        }
        if (length > 12) {
            strength += 10;
        }
        if (length > 14) {
            strength += 10;
        }
        if (length > 16) {
            strength += 10;
        }
        if (hasNumber) {
            strength += 10;
        }
        if (hasLower) {
            strength += 10;
        }
        if (hasUpper) {
            strength += 10;
        }
        if (hasSymbol) {
            strength += 10;
        }
        if (hasRepetition) {
            strength -= 10;
        }

        strength = (int) ((strength / 90.0) * 100);
        return strength;
    }
}
