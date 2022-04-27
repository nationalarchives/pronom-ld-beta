package com.wallscope.pronombackend.utils;

import com.github.rjeschke.txtmark.Processor;
import com.wallscope.pronombackend.config.ApplicationConfig;
import com.wallscope.pronombackend.model.FAQCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TemplateUtils {
    Logger logger = LoggerFactory.getLogger(TemplateUtils.class);

    private final String mdDir;

    public TemplateUtils() {
        this.mdDir = ApplicationConfig.MARKDOWN_DIR;
        logger.info("loading markdown directory: " + mdDir);
    }

    public String md(String region) {
        try {
            File f = new File(mdDir, region + ".md");
            return Processor.process(f);
        } catch (IOException e) {
            logger.debug("MD LOADER: No template for region: " + region);
            return "";
        }
    }

    public String mdString(String md) {
        return Processor.process(md);
    }

    public String raw(String region) {
        try {
            File f = new File(mdDir, region + ".md");
            return Files.readString(f.toPath(), StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            logger.info("RAW LOADER: No template for region: " + region);
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String humaniseRegion(String region) {
        // regions names are the following format: page_region-name
        // so by splitting on "_" and replacing "-" with " " and then joining again on ": "
        // we turn it into: "page: region name"
        // the last step capitalises the page name: "Page: region name"
        try {
            String name = Arrays.stream(region.split("_"))
                    .map(s -> s.replaceAll("-", " "))
                    .collect(Collectors.joining(": "));
            return StringUtils.capitalize(name);
        } catch (Exception e) {
            logger.info("HUMANISER: Failed to transform region name: " + region);
            return "";
        }
    }

    private static final Pattern catRegex = Pattern.compile("^## ", Pattern.MULTILINE);
    private static final Pattern itemRegex = Pattern.compile("^### ", Pattern.MULTILINE);

    public List<FAQCategory> parseFAQ(String region) {
        String md = this.raw(region);
        List<FAQCategory> FAQs = new ArrayList<>();
        // break into category blocks
        Arrays.stream(catRegex.split(md)).forEach(c -> {
            int firstBreak = c.indexOf("\n");
            if (firstBreak < 0) return;
            String catTitle = c.substring(0, firstBreak).trim();
            List<FAQCategory.FAQItem> items = new ArrayList<>();
            Arrays.stream(itemRegex.split(c.substring(firstBreak + 1))).forEach(it -> {
                int itFirstBreak = it.indexOf("\n");
                if (itFirstBreak < 0) return;
                String itemTitle = it.substring(0, itFirstBreak).trim();
                String text = it.substring(itFirstBreak + 1);
                items.add(new FAQCategory.FAQItem(itemTitle, text));
            });
            FAQs.add(new FAQCategory(catTitle, items));
        });
        return FAQs;
    }
}
