package com.wallscope.pronombackend.utils;

import com.github.rjeschke.txtmark.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class TemplateUtils {
    Logger logger = LoggerFactory.getLogger(TemplateUtils.class);

    private final String mdDir;

    public TemplateUtils(String mdDir) {
        this.mdDir = mdDir;
    }

    public String md(String region) {
        try {
            File f = new File(mdDir, region + ".md");
            return Processor.process(f);
        } catch (IOException e) {
            logger.info("MD LOADER: No template for region: " + region);
            return "";
        }
    }

    public String raw(String region) {
        try {
            File f = new File(mdDir, region + ".md");
            return Files.readString(f.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.info("RAW LOADER: No template for region: " + region);
            return "";
        }
    }
}
