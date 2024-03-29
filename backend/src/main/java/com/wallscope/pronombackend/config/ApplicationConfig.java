package com.wallscope.pronombackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfig {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    static String getEnvOr(String env, String or) {
        String v = System.getenv(env);
        logger.trace("reading var: " + env + " value: " + v);
        if (v == null) {
            logger.trace("returning 'or' value: " + or);
            return or;
        }
        return v;
    }

    public static final String MARKDOWN_DIR = getEnvOr("MARKDOWN_DIR", "/md");
    public static final String SIGNATURE_DIR = getEnvOr("SIGNATURE_DIR", "/sig");
    public static final String TRIPLESTORE = getEnvOr("TRIPLESTORE", "http://localhost:3030/ds");
    public static final String BINARY_SIG_NAME = "DROID_SignatureFile_V%s.xml";
    public static final String CONTAINER_SIG_NAME = "container-signature-%s.xml";
}
