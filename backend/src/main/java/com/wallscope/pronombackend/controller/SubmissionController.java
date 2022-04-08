package com.wallscope.pronombackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

/*
 * This controller handles all calls to form submission related pages.
 * This includes both GET operations for templates and POST operations with the data.
 * */
@Controller
public class SubmissionController {
    Logger logger = LoggerFactory.getLogger(SubmissionController.class);

    @GetMapping("/contribute")
    public String contribute(Model model) {
        return "contribute";
    }

    @GetMapping("/contribute/form/{puid}")
    public String formTemplate(Model model, @PathVariable(required = false) String puid) {
        if (puid == null) {
            return "form-choice";
        }
        if (puid.equals("new")) {
            model.addAttribute("isNew", true);
        }
        return "user-form";
    }

    @PostMapping("/contribute/form/{puid}")
    public String formSubmission(Model model, @PathVariable(required = false) String puid) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "not implemented");
    }
}
