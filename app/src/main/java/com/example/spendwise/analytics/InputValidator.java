package com.example.spendwise.analytics;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class InputValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private InputValidator() {
    }

    public static boolean isValidEmail(String email) {
        return email != null
                && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public static boolean isValidAmount(String amount) {
        if (amount == null || amount.trim().isEmpty()) {
            return false;
        }

        try {
            BigDecimal value = new BigDecimal(amount.trim());
            return value.compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    public static boolean isValidDescription(String description) {
        return description != null
                && description.trim().length() <= 255;
    }

}
