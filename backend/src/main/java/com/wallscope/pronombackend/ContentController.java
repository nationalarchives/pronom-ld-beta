package com.wallscope.pronombackend;

import com.google.common.io.Resources;
import com.wallscope.pronombackend.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This controller handles all calls to form submission related pages.
 * This includes both GET operations for templates and POST operations with the data.
 * */
@RestController
public class ContentController {
    @Value("classpath:templates/*")
    private Resource[] resources;

    private String mdDir = ApplicationConfig.MARKDOWN_DIR;

    private final Pattern p = Pattern.compile("@templateUtils\\.md\\('(?<region>[a-z_-]+)'\\)");

    @GetMapping("/content/list")
    public List<String> contribute(Model model) throws IOException {
        return getAvailableRegions();
    }

    @RequestMapping(value = "/content/{region}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
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
            return "{\"status\": \"ok\"}";
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "something went wrong");
        }
    }

    private List<String> getAvailableRegions() {
        try {
            List<String> regions = new ArrayList<>();
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
            return Collections.emptyList();
        }
    }
}
