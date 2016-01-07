package com.jksoft.utils;

/**
 * Created by Jackey on 2015/6/18.
 */
public class LogUtil {

    static String logType;

    static {
        logType = DBNames.LOGTYPE.CONSOLE;
    }


    public static void log(String logStr) {
        if (DBNames.LOGTYPE.CONSOLE.equals(logType)) {
            logConsole(logStr);
        } else if (DBNames.LOGTYPE.FILE.equals(logType)) {
            logFile(logStr);
        } else if (DBNames.LOGTYPE.DATABASE.equals(logType)) {
            logDB(logStr);
        }
    }

    public static void logConsole(String logStr) {
        System.out.println(logStr);
    }

    public static void logFile(String logStr) {

    }

    public static void logDB(String logStr) {

    }
}
