package com.pragma.usuarios.infrastructure.constant;

import java.util.regex.Pattern;

public final class ValidationConstants {

    private ValidationConstants() {
        throw new AssertionError("Cannot instantiate ValidationConstants");
    }

    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z\\d+_.-]+@[A-Za-z\\d.-]+\\.[A-Za-z]{2,}$"
    );

    public static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?\\d{1,12}$"
    );

    public static final Pattern DOCUMENT_PATTERN = Pattern.compile(
            "^\\d+$"
    );

    public static final Pattern NAME_PATTERN = Pattern.compile(
            "^[A-Za-zÁáÉéÍíÓóÚúÑñ\\s]+$"
    );

    public static final int MAX_PHONE_LENGTH = 13;
    public static final int MAX_FIRST_NAME_LENGTH = 100;
    public static final int MAX_LAST_NAME_LENGTH = 100;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final int MAX_DOCUMENT_LENGTH = 20;
    public static final int MIN_FIRST_NAME_LENGTH = 1;
    public static final int MIN_LAST_NAME_LENGTH = 1;
}
