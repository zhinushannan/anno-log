package io.github.zhinushannan.annolog.log;

public class LogIdStorage {

    private static final ThreadLocal<String> logIdStorage = new ThreadLocal<String>();

    protected static void save(String logId) {
        logIdStorage.set(logId);
    }

    public static String get() {
        return logIdStorage.get();
    }

}
