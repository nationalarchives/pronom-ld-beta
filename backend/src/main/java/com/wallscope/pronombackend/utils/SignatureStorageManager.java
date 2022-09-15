package com.wallscope.pronombackend.utils;

import com.wallscope.pronombackend.config.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class SignatureStorageManager {
    static Logger logger = LoggerFactory.getLogger(SignatureStorageManager.class);
    private static final Path sigRoot = Path.of(ApplicationConfig.SIGNATURE_DIR);
    private static final Path container = Path.of(sigRoot.toString(), "container");
    private static final Path binary = Path.of(sigRoot.toString(), "binary");

    public static void init() {
        if (!Files.exists(sigRoot)) {
            logger.debug("CREATING SIGNATURE ROOT DIRECTORY");
            if (sigRoot.toFile().mkdir()) {
                logger.debug("SIGNATURE ROOT DIRECTORY CREATED SUCCESSFULLY");
            }
        }
        if (!Files.exists(container)) {
            logger.debug("CREATING SIGNATURE CONTAINER DIRECTORY");
            if (container.toFile().mkdir()) {
                logger.debug("SIGNATURE CONTAINER DIRECTORY CREATED SUCCESSFULLY");
            }
        }
        if (!Files.exists(binary)) {
            logger.debug("CREATING SIGNATURE BINARY DIRECTORY");
            if (binary.toFile().mkdir()) {
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

    public static File getLatestSignature(Path dir) {
        if (dir == null) return null;
        File[] files = dir.toFile().listFiles();
        if (files == null) return null;
        return Arrays.stream(files).max(Comparator.comparingLong(File::lastModified)).orElse(null);
    }

    public static File getLatestBinarySignature() {
        return getLatestSignature(getBinaryDir());
    }

    public static File getLatestContainerSignature() {
        return getLatestSignature(getContainerDir());
    }

    @SuppressWarnings("unchecked")
    public static <T> T readInXML(File file, Class<T> clazz) throws JAXBException, FileNotFoundException {
        JAXBContext ctx = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        InputStream inStream = new FileInputStream(file);
        return (T) unmarshaller.unmarshal(inStream);
    }

    public static Integer versionFromFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // The container file contains a schemaVersion attribute, so we have to account for that.
            // Otherwise we could just check for Version
            if (line.contains("signatureVersion=\"")) {
                return Integer.parseInt(line.replaceAll(".*signatureVersion=\"(\\d+)\".*", "$1"));
            }
            if (line.contains("Version=\"")) {
                return Integer.parseInt(line.replaceAll(".*Version=\"(\\d+)\".*", "$1"));
            }
        }
        return null;
    }
}
