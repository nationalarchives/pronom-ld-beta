package com.wallscope.pronombackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfig {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    static String getEnvOr(String env, String or) {
        String v = System.getenv(env);
        logger.debug("reading var: " + env + " value: " + v);
        if (v == null) {
            logger.debug("returning 'or' value: " + or);
            return or;
        }
        return v;
    }

    public static String MARKDOWN_DIR = getEnvOr("MARKDOWN_DIR", "/md");
    public static String TRIPLESTORE = getEnvOr("TRIPLESTORE", "http://localhost:3030/ds");
}
