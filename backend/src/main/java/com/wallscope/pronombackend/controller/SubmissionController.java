package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.dao.FileFormatDAO;
import com.wallscope.pronombackend.dao.FormOptionsDAO;
import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.model.FormFileFormat;
import com.wallscope.pronombackend.model.FormOption;
import com.wallscope.pronombackend.utils.RDFUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

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

    @GetMapping("/contribute/form")
    public String formChoice() {
        return "form-choice";
    }

    @GetMapping("/contribute/form/{puidType}/{puid}")
    public String formTemplate(Model model, @PathVariable String puidType, @PathVariable String puid) {
        model.addAttribute("edit", true);
        FileFormatDAO dao = new FileFormatDAO();
        FileFormat f = dao.getFileFormatByPuid(puid, puidType);
        if (f == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no File Format with puid: " + puid);
        }
        model.addAttribute("ff", FormFileFormat.convert(f));
        setFormOptions(model);
        return "user-form";
    }

    @GetMapping("/contribute/form/new")
    public String newFormTemplate(Model model) {
        model.addAttribute("edit", false);
        FormFileFormat compare = new FormFileFormat();
        compare.setVersion("3.2.1");
        model.addAttribute("compare", compare);
        model.addAttribute("ff", new FormFileFormat());
        setFormOptions(model);
        return "user-form";
    }

    @PostMapping("/contribute/form/new")
    public String newFormSubmission(Model model, @ModelAttribute FormFileFormat ff) {

        logger.debug("Received FormFileFormat:\n" + ff);
        return "redirect:/contribute/form";
    }

    @PostMapping("/contribute/form/{puidType}/{puid}")
    public String formSubmission(Model model, @ModelAttribute FormFileFormat ff) {

        logger.debug("Received FormFileFormat:\n" + ff);
        return "redirect:/contribute/form";
    }

    private void setFormOptions(Model model) {
        FormOptionsDAO dao = new FormOptionsDAO();
        Map<String, List<FormOption>> options = dao.getOptionsOfType(List.of(
                makeResource(RDFUtil.PRONOM.ByteOrder.type),
                makeResource(RDFUtil.PRONOM.FormatIdentifierType.type)
        ));
        model.addAttribute("byteOrderOptions", options.get(RDFUtil.PRONOM.ByteOrder.type));
        model.addAttribute("formatIdentifierOptions", options.get(RDFUtil.PRONOM.FormatIdentifierType.type));
    }
}
