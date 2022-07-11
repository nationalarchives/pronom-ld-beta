package com.wallscope.pronombackend.controller;

import com.google.common.io.Resources;
import com.wallscope.pronombackend.config.ApplicationConfig;
import com.wallscope.pronombackend.utils.TemplateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This controller handles all calls to form submission related pages.
 * This includes both GET operations for templates and POST operations with the data.
 * */
@Controller
public class ContentController {
    Logger logger = LoggerFactory.getLogger(ContentController.class);

    @Value("classpath:templates/*")
    private Resource[] resources;

    private final String mdDir = ApplicationConfig.MARKDOWN_DIR;

    @GetMapping("/editorial/content")
    public String contribute(Model model, TemplateUtils templateUtils) throws IOException {
        ArrayList<String> regions = new ArrayList<>(getAvailableRegions());
        Collections.sort(regions);
        HashMap<String, Map<String, String>> contentMap = new HashMap<>();
        for (String r : regions) {
            String[] parts = r.split("_");
            if (parts.length != 2) {
                logger.debug("INVALID REGION NAME, IGNORING: " + r);
                continue;
            }
            if (!contentMap.containsKey(parts[0])) {
                contentMap.put(parts[0], new HashMap<>());
            }
            contentMap.get(parts[0]).put(parts[1], templateUtils.raw(r));
        }
        logger.trace("CONTENT MAP: "+ contentMap);
        model.addAttribute("contentMap", contentMap);
        // Adding the regions set allows for sorted iteration
        return "content-manager";
    }

    @RequestMapping(value = "/editorial/content/{region}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String saveContent(Model model, @PathVariable(required = true) String region, @RequestParam HashMap<String, String> formData) {
        try {
            if (!getAvailableRegions().contains(region)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid region: " + region);
            }
            File f = new File(mdDir, region + ".md");
            String content = formData.getOrDefault("content", null);
            if (content == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no content");
            }
            FileWriter f2 = new FileWriter(f, false);
            f2.write(content);
            f2.close();
            return "redirect:/editorial/content";
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "something went wrong");
        }
    }

    private final Pattern p = Pattern.compile("@templateUtils\\.(?:md|parseFAQ)\\('(?<region>[a-z_-]+)'\\)");

    private Set<String> getAvailableRegions() {
        try {
            Set<String> regions = new HashSet<>();
            for (final Resource res : resources) {
                URL url = res.getURL();
                // Get the contents of the template file
                String contents = Resources.toString(url, StandardCharsets.UTF_8);
                // Regex match all calls to the md function using the pre-compiled regex pattern
                Matcher m = p.matcher(contents);
                while (m.find()) {
                    if (m.groupCount() > 0) {
                        regions.add(m.group(1));
                    }
                }
            }
            return regions;
        } catch (IOException e) {
            // TODO: Handle exception
            return Collections.emptySet();
        }
    }
}
