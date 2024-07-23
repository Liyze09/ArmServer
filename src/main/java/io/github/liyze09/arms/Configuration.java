package io.github.liyze09.arms;

public final class Configuration {
    private static final Configuration instance = new Configuration();
    public int port = 25565;

    private Configuration() {
    }

    public static Configuration getInstance() {
        return instance;
    }
}
