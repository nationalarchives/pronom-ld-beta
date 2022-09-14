package com.wallscope.pronombackend.controller;

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
public class ReleaseNotesController {
    Logger logger = LoggerFactory.getLogger(ReleaseNotesController.class);

    @GetMapping("/release-notes/{version}")
    public String relNotesVersion(Model model, @PathVariable(required = false) String version) {
        return "rel-notes-single";
    }
    @GetMapping("/release-notes")
    public String relNotes(Model model) {
        return "rel-notes-list";
    }
}
