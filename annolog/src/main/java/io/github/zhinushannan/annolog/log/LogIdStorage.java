package io.github.zhinushannan.annolog.log;

public class LogIdStorage {

    private static ThreadLocal<String> logIdStorage = new ThreadLocal<>();

    protected static void save(String logId) {
        logIdStorage.set(logId);
    }

    public static String get() {
        return logIdStorage.get();
    }

}
