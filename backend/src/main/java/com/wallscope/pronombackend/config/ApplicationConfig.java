package com.wallscope.pronombackend.config;

public class ApplicationConfig {
    static String getEnvOr(String env, String or) {
        String v = System.getenv(env);
        return v == null ? or : v;
    }

    public static String MARKDOWN_DIR = getEnvOr("MARKDOWN_DIR","md");
}
