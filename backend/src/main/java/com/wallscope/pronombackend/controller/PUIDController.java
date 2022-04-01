package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.dao.FileFormatDAO;
import com.wallscope.pronombackend.model.FileFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

/*
 * This controller handles all calls to pages where the content does not depend on fetching data from the database
 * Hence the name static. The actual content may change based on the Markdown workflow
 * */
@Controller
public class PUIDController {
    Logger logger = LoggerFactory.getLogger(PUIDController.class);

    @GetMapping(value = {"/fmt/{puid}", "/x-fmt/{puid}"})
    public String fileFormatHandler(Model model, @PathVariable(required = false) String puid) {
        FileFormatDAO dao = new FileFormatDAO();
        FileFormat f = dao.getFileFormatByPuid(puid);
        if (f == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no File Format with puid: " + puid);
        }
        model.addAttribute("fileFormat", f);
        logger.info("returning File Format <" + f.getURI().getURI() + "> with PUID: " + f.getPuid());
        logger.debug(f.toString());
        return "file-format";
    }

    @GetMapping(value = {"/chr/{puid}", "/x-chr/{puid}", "/sfw/{puid}", "/x-sfw/{puid}", "/cmp/{puid}", "/x-cmp/{puid}"})
    public String genericPUIDHandler(Model model, @PathVariable(required = false) String puid) {
        return "generic-puid";
    }
}
