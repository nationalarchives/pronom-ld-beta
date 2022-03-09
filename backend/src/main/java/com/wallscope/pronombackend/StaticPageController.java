package com.wallscope.pronombackend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/*
* This controller handles all calls to pages where the content does not depend on fetching data from the database
* Hence the name static. The actual content may change based on the Markdown workflow
* */
@Controller
public class StaticPageController {

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }

    @GetMapping("/faq")
    public String faq(Model model) {
        return "faq";
    }

    @GetMapping("/external-projects")
    public String externalProjects(Model model) {
        return "external";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        return "contact";
    }

    @GetMapping("/droid")
    public String droid(Model model) {
        return "droid";
    }
}
