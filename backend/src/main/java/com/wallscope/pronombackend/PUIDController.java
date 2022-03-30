package com.wallscope.pronombackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/*
 * This controller handles all calls to pages where the content does not depend on fetching data from the database
 * Hence the name static. The actual content may change based on the Markdown workflow
 * */
@Controller
public class PUIDController {
    Logger logger = LoggerFactory.getLogger(PUIDController.class);

    @GetMapping("/{puid:(?:x-)?fmt\\/\\d+}")
    public String fileFormatHandler(Model model, @PathVariable(required = false) String puid) {
        return "file-format";
    }

    @GetMapping("/(?:x-)?(?:chr|fmt|sfw|cmp)\\/\\d+")
    public String genericPUIDHandler(Model model, @PathVariable(required = false) String puid) {
        return "generic-puid";
    }
}
