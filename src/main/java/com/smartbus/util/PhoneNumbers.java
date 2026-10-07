package com.smartbus.util;

import java.util.Optional;

public final class PhoneNumbers {
    private PhoneNumbers() {}

    public static String normalize(String phone) {
        String normalized = phone.trim().replaceAll("[\\s()\\-]", "");
        if (!normalized.matches("\\+?[0-9]{7,15}")) {
            throw new IllegalArgumentException("Enter a valid mobile number with 7 to 15 digits");
        }
        return normalized;
    }

    public static Optional<String> normalizeIfPhoneNumber(String value) {
        String normalized = value.trim().replaceAll("[\\s()\\-]", "");
        return normalized.matches("\\+?[0-9]{7,15}") ? Optional.of(normalized) : Optional.empty();
    }
}
