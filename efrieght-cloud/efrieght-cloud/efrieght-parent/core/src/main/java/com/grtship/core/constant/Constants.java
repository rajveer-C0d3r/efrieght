package com.grtship.core.constant;

/**
 * Application constants.
 */
public final class Constants {
	
    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ALPHABETS_REGEX = "^[a-zA-Z]*$";
    public static final String ALPHABETS_SPECIAL_CHAR_REGEX = "^[a-zA-Z ()-]+$";
    public static final String ALPHA_NUMERIC_REGEX = "^[a-zA-Z0-9 ]+$";
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String ANONYMOUS_USER = "anonymoususer";

    private Constants() {
    }
}
