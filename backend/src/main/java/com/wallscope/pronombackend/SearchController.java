package com.wallscope.pronombackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/*
 * This controller handles all calls to pages where the content does not depend on fetching data from the database
 * Hence the name static. The actual content may change based on the Markdown workflow
 * */
@Controller
public class SearchController {
    Logger logger = LoggerFactory.getLogger(SearchController.class);

    @GetMapping("/search")
    public String fileFormatHandler(Model model, @RequestParam String q) {
        return "search";
    }
}
