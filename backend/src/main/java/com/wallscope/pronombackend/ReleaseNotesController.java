package com.wallscope.pronombackend;

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

    @GetMapping("/release-notes/{version}")
    public String relNotes(Model model, @PathVariable(required = false) String version) {
        if (version == null) {
            return "rel-notes-list";
        }
        return "rel-notes-single";
    }
}
