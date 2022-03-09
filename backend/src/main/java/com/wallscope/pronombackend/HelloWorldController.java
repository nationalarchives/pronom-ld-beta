package com.wallscope.pronombackend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloWorldController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "helloworld");
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }
}
