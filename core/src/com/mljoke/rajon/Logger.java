package com.mljoke.rajon;

public class Logger {
    public static final int INFO = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;
    static final int CRITICAL = 3;
    static final int DEBUG = 4;
    public static final int ELMAR = 0;
    public static final int SEBA = 1;
    public static final int ANDREAS = 2;

    private static final String[] LEVEL = {"INFO", "WARNING", "ERROR", "CRITICAL", "DEBUG"};
    private static final String[] USERS = {"ELM", "SEB", "AND"};

    public static void log(int user, int messageLevel, String message) {
        String userString = "N/A";
        if (user < USERS.length && user >= 0) userString = USERS[user];
        System.out.println("[" + userString + "][" + LEVEL[messageLevel] + "] " + message);

    }
}
