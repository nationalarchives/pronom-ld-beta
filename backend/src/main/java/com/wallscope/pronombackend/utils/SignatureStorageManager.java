package com.wallscope.pronombackend.utils;

import com.wallscope.pronombackend.config.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public class SignatureStorageManager {
    static Logger logger = LoggerFactory.getLogger(SignatureStorageManager.class);
    private static final Path sigRoot = Path.of(ApplicationConfig.SIGNATURE_DIR);
    private static final Path container = Path.of(sigRoot.toString(), "container");
    private static final Path binary = Path.of(sigRoot.toString(), "binary");

    public static void init() {
        if (!Files.exists(sigRoot)) {
            logger.debug("CREATING SIGNATURE ROOT DIRECTORY");
            if(sigRoot.toFile().mkdir()){
                logger.debug("SIGNATURE ROOT DIRECTORY CREATED SUCCESSFULLY");
            }
        }
        if (!Files.exists(container)) {
            logger.debug("CREATING SIGNATURE CONTAINER DIRECTORY");
            if(container.toFile().mkdir()){
                logger.debug("SIGNATURE CONTAINER DIRECTORY CREATED SUCCESSFULLY");
            }
        }
        if (!Files.exists(binary)) {
            logger.debug("CREATING SIGNATURE BINARY DIRECTORY");
            if(binary.toFile().mkdir()){
                logger.debug("SIGNATURE BINARY DIRECTORY CREATED SUCCESSFULLY");
            }
        }
    }

    public static Path getContainerDir() {
        return container;
    }

    public static Path getBinaryDir() {
        return binary;
    }
}
