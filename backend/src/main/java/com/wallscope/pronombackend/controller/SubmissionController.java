package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.dao.FileFormatDAO;
import com.wallscope.pronombackend.dao.FormOptionsDAO;
import com.wallscope.pronombackend.model.*;
import org.apache.jena.rdf.model.Resource;
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

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.PRONOM;
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no File Format with puid: " + puidType + "/" + puid);
        }
        model.addAttribute("ff", FormFileFormat.convert(f));
        setFormOptions(model);
        return "user-form";
    }

    @GetMapping("/contribute/form/new")
    public String newFormTemplate(Model model) {
        model.addAttribute("edit", false);
        FormFileFormat compare = new FormFileFormat();
        model.addAttribute("compare", compare);
        // test prepare file format
        FormFileFormat ff = new FormFileFormat();
        model.addAttribute("ff", ff);
        setFormOptions(model);
        logger.debug("Sending FormFileFormat:\n" + ff);
        return "user-form";
    }

    @PostMapping("/contribute/form/new")
    public String newFormSubmission(Model model, @ModelAttribute FormFileFormat ff) {
        // For new file formats generate random UUID based URIs for all the top level entities
        ff.randomizeURIs();
        FileFormatDAO dao = new FileFormatDAO();
        // Extract puid and puidType
        String[] puidParts = ff.getPuid().split("/");
        Integer puid = null;
        Resource puidType = null;
        if (puidParts.length == 2) {
            puid = Integer.parseInt(puidParts[1]);
            puidType = dao.getURIFromLabel(puidParts[0]);
        }
        List<Classification> cs = dao.getClassifications(ff.getClassifications());
        // Convert to a FileFormat object
        FileFormat f = ff.toObject(puid, puidType, Instant.now(), cs);
        // For now we hardcode this, must be hooked into user system when implemented
        String author = "pronom";
        FormSubmittedBy submitter = ff.getSubmittedBy();
        // source == null because it's a new file format therefore there is no existing one to compare
        TentativeFileFormat tff = new TentativeFileFormat(f.getURI(), f, author, submitter.toObject(), null);
        dao.saveTentativeFormat(tff);
        return "redirect:/contribute/form";
    }

    @PostMapping("/contribute/form/{puidType}/{puid}")
    public String formSubmission(Model model, @ModelAttribute FormFileFormat ff, @PathVariable String puidType, @PathVariable String puid) {
        ff.setUri(PRONOM.FileFormat.id + UUID.randomUUID());
        FileFormatDAO dao = new FileFormatDAO();
        // For existing file formats get the current object
        FileFormat existing = dao.getFileFormatByPuid(puid, puidType);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no File Format with puid: " + puidType + "/" + puid);
        }
        // For now we hardcode this, must be hooked into user system when implemented
        String author = "pronom";
        List<Classification> cs = dao.getClassifications(ff.getClassifications());
        // Convert to a FileFormat object
        FileFormat f = ff.toObject(existing.getPuid(), existing.getPuidType(), Instant.now(), cs);
        FormSubmittedBy submitter = ff.getSubmittedBy();
        submitter.setUri(PRONOM.Submitter.id + UUID.randomUUID());
        TentativeFileFormat tff = new TentativeFileFormat(f.getURI(), f, author, submitter.toObject(), existing.getURI());
        dao.saveTentativeFormat(tff);
        return "redirect:/contribute/form";
    }

    private void setFormOptions(Model model) {
        FormOptionsDAO dao = new FormOptionsDAO();
        Map<String, List<FormOption>> options = dao.getOptionsOfType(List.of(
                makeResource(PRONOM.ByteOrder.type),
                makeResource(PRONOM.FormatIdentifierType.type),
                makeResource(PRONOM.ByteSequence.BSPType),
                makeResource(PRONOM.FormatRelationshipType.type),
                makeResource(PRONOM.FileFormatFamily.type),
                makeResource(PRONOM.CompressionType.type)
        ));
        model.addAttribute("byteOrderOptions", options.get(PRONOM.ByteOrder.type));
        model.addAttribute("formatIdentifierOptions", options.get(PRONOM.FormatIdentifierType.type));
        List<FormOption> sortedPosTypes = options.get(PRONOM.ByteSequence.BSPType).stream().sorted(Comparator.comparing(FormOption::getValue)).collect(Collectors.toList());
        model.addAttribute("positionTypeOptions", sortedPosTypes);
        model.addAttribute("relationshipTypeOptions", options.get(PRONOM.FormatRelationshipType.type));
        model.addAttribute("formatFamilyOptions", options.get(PRONOM.FileFormatFamily.type));
        model.addAttribute("compressionTypeOptions", options.get(PRONOM.CompressionType.type));
    }
}
