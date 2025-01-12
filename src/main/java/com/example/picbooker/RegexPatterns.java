package com.example.picbooker;

public class RegexPatterns {
    public static final String emailRegex = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
    public static final String phoneRegex = "^\\+970\\d{8,9}$";
    public static final String passwordRegex = "^(?=.*[a-zA-Z])(?=.*\\d).+$";
    public static final String alphaNumeric = "^[a-zA-Z]+\\d+$";
}
