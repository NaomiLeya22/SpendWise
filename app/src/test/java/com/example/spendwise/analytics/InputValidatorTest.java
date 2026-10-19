package com.example.spendwise.analytics;

import org.junit.Test;

import static org.junit.Assert.*;

public class InputValidatorTest {

    @Test
    public void acceptsValidEmail() {
        assertTrue(InputValidator.isValidEmail("naomi@example.com"));
    }

    @Test
    public void rejectsInvalidEmail() {
        assertFalse(InputValidator.isValidEmail("naomi-at-example"));
    }

    @Test
    public void acceptsPasswordWithEightCharacters() {
        assertTrue(InputValidator.isValidPassword("Spend123"));
    }

    @Test
    public void rejectsShortPassword() {
        assertFalse(InputValidator.isValidPassword("abc123"));
    }

    @Test
    public void acceptsPositiveAmount() {
        assertTrue(InputValidator.isValidAmount("150.50"));
    }

    @Test
    public void rejectsZeroAmount() {
        assertFalse(InputValidator.isValidAmount("0"));
    }

    @Test
    public void rejectsTextAsAmount() {
        assertFalse(InputValidator.isValidAmount("abc"));
    }

    @Test
    public void rejectsNullDescription() {
        assertFalse(InputValidator.isValidDescription(null));
    }

}
