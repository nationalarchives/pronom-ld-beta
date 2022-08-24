package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.dao.FileFormatDAO;
import com.wallscope.pronombackend.dao.FormOptionsDAO;
import com.wallscope.pronombackend.dao.SubmissionDAO;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

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
    public RedirectView newFormSubmission(Model model, @ModelAttribute FormFileFormat ff, RedirectAttributes redir) {
        logger.debug("FORM RECEIVED: " + ff);
        // For new file formats generate random UUID based URIs for all the top level entities
        ff.randomizeURIs();
        ff.removeEmpties();
        List<FormValidationException> errors = ff.validate(false);
        if (!errors.isEmpty()) {
            redir.addFlashAttribute("errors", errors.stream().map(Exception::getMessage));
            redir.addFlashAttribute("ff", ff);
            return new RedirectView("/contribute/form/new");
        }
        FileFormatDAO ffDao = new FileFormatDAO();
        List<LabeledURI> cs = ffDao.getClassifications(ff.getClassifications());
        // Convert to a FileFormat object
        FileFormat f = ff.toObject(null, null, Instant.now(), Instant.now(), null, cs);
        FormSubmittedBy submitter = ff.getSubmittedBy();
        Contributor contributor = submitter.toObject(false);
        SubmissionDAO subDao = new SubmissionDAO();
        // source == null because it's a new file format therefore there is no existing one to compare
        Submission sub = new Submission(makeResource(PRONOM.Submission.id + UUID.randomUUID()),
                makeResource(PRONOM.Submission.UserSubmission),
                makeResource(PRONOM.Submission.StatusWaiting),
                contributor,
                null,
                null,
                new TentativeFileFormat(f.getURI(), f),
                Instant.now(),
                null);
        subDao.saveSubmission(sub);
        redir.addFlashAttribute("feedback", new Feedback(Feedback.Status.SUCCESS, "Submission for new file format created successfully."));
        return new RedirectView("/contribute/form");
    }

    @PostMapping("/contribute/form/{puidType}/{puid}")
    public RedirectView formSubmission(Model model, @ModelAttribute FormFileFormat ff, @PathVariable String puidType, @PathVariable String puid, RedirectAttributes redir) {
        ff.setUri(PRONOM.TentativeFileFormat.id + UUID.randomUUID());
        ff.randomizeURIs();
        ff.removeEmpties();
        FileFormatDAO ffDao = new FileFormatDAO();
        // For existing file formats get the current object
        FileFormat existing = ffDao.getFileFormatByPuid(puid, puidType);
        String fullPuid = puidType + "/" + puid;
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no File Format with puid: " + fullPuid);
        }

        List<LabeledURI> cs = ffDao.getClassifications(ff.getClassifications());
        // Convert to a FileFormat object
        FileFormat f = ff.toObject(existing.getPuid(), existing.getPuidType(), Instant.now(), Instant.now(), null, cs);
        FormSubmittedBy submitter = ff.getSubmittedBy();
        submitter.setUri(PRONOM.Contributor.id + UUID.randomUUID());
        SubmissionDAO subDao = new SubmissionDAO();
        Submission sub = new Submission(makeResource(PRONOM.Submission.id + UUID.randomUUID()),
                makeResource(PRONOM.Submission.UserSubmission),
                makeResource(PRONOM.Submission.StatusWaiting),
                submitter.toObject(false),
                null,
                existing,
                new TentativeFileFormat(f.getURI(), f),
                Instant.now(),
                null);
        subDao.saveSubmission(sub);
        redir.addFlashAttribute("feedback", new Feedback(Feedback.Status.SUCCESS, "Submission for file format " + fullPuid + " created successfully."));
        return new RedirectView("/contribute/form");
    }

    // Editorial form submissions

    @GetMapping("/editorial/form/{submission}")
    public String editorialSubmissionForm(Model model, @PathVariable String submission, HttpServletResponse response) {
        // set empty actor for the add-actor functionality
        model.addAttribute("actor", new FormActor());
        model.addAttribute("editorial", true);
        model.addAttribute("edit", true);
        SubmissionDAO subDao = new SubmissionDAO();
        Resource submissionUri = makeResource(PRONOM.Submission.id + submission);
        Submission sub = subDao.getSubmissionByURI(submissionUri);
        if (sub == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no Submission with id: " + submission);
        }
        FormFileFormat compare = new FormFileFormat();
        if (sub.getSource() != null && sub.getSource().getURI() != null) {
            FileFormatDAO ffDao = new FileFormatDAO();
            FileFormat src = ffDao.getFileFormatByURI(sub.getSource().getURI());
            compare = FormFileFormat.convert(src);
        }

        model.addAttribute("ff", FormFileFormat.convert(sub.getFormat()));
        model.addAttribute("compare", compare);
        model.addAttribute("submissionId", submission);
        setFormOptions(model);
        return "internal-form";
    }

    @PostMapping("/editorial/form/{submission}")
    public String submissionForm(Model model, @PathVariable String submission, @ModelAttribute FormFileFormat ff) {
        ff.removeEmpties();
        SubmissionDAO subDao = new SubmissionDAO();
        Resource submissionUri = makeResource(PRONOM.Submission.id + submission);
        Submission sub = subDao.getSubmissionByURI(submissionUri);
        if (sub == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no Submission with id: " + submission);
        }
        FileFormatDAO ffDao = new FileFormatDAO();
        Contributor reviewer = new Contributor(makeResource("mailto:test-user@pronom.com"),
                "Pronom test user",
                "PRONOM",
                "test-user@pronom.com",
                "",
                "",
                false,
                true);
        List<LabeledURI> cs = ffDao.getClassifications(ff.getClassifications());
        FileFormat old = sub.getFormat();
        Submission newSub = new Submission(sub.getURI(),
                makeResource(PRONOM.Submission.InternalSubmission),
                sub.getSubmissionStatus(),
                sub.getSubmitter(),
                reviewer,
                sub.getSource(),
                new TentativeFileFormat(old.getURI(), ff.toObject(old.getPuid(), old.getPuidType(), Instant.now(), Instant.now(), null, cs)),
                sub.getCreated(),
                Instant.now());
        subDao.deleteSubmission(sub.getURI());
        subDao.saveSubmission(newSub);
        return "redirect:/editorial";
    }

    @GetMapping("/editorial/form/{puidType}/{puid}")
    public String editorialFormTemplate(Model model, @PathVariable String puidType, @PathVariable String puid, HttpServletResponse response) {
        // set empty actor for the add-actor functionality
        model.addAttribute("actor", new FormActor());
        model.addAttribute("edit", true);
        model.addAttribute("editorial", true);
        FileFormatDAO dao = new FileFormatDAO();
        FileFormat f = dao.getFileFormatByPuid(puid, puidType);
        if (f == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no File Format with puid: " + puid);
        }
        model.addAttribute("ff", FormFileFormat.convert(f));
        setFormOptions(model);
        return "internal-form";
    }

    @PostMapping("/editorial/form/{puidType}/{puid}")
    public String editorialFormSubmission(Model model, @ModelAttribute FormFileFormat ff) {
        logger.debug("Received FormFileFormat:\n" + ff);
        return "redirect: /editorial/form";
    }

    @GetMapping("/editorial/form/new")
    public String editorialNewFormTemplate(Model model, HttpServletResponse response) {
        // set empty actor for the add-actor functionality
        model.addAttribute("actor", new FormActor());
        model.addAttribute("editorial", true);
        model.addAttribute("edit", false);
        FormFileFormat compare = new FormFileFormat();
        model.addAttribute("compare", compare);
        // test prepare file format
        FormFileFormat ff = new FormFileFormat();
        model.addAttribute("ff", ff);
        setFormOptions(model);
        logger.debug("Sending FormFileFormat:\n" + ff);
        return "internal-form";
    }

    @PostMapping("/editorial/form/new")
    public RedirectView editorialNewFormSubmission(Model model, @ModelAttribute FormFileFormat ff, RedirectAttributes redir) {
        logger.debug("FORM RECEIVED: " + ff);
        // For new file formats generate random UUID based URIs for all the top level entities
        ff.randomizeURIs();
        ff.removeEmpties();
        FileFormatDAO ffDao = new FileFormatDAO();
        List<LabeledURI> cs = ffDao.getClassifications(ff.getClassifications());
        // Convert to a FileFormat object
        FileFormat f = ff.toObject(null, null, Instant.now(), Instant.now(), null, cs);
        FormSubmittedBy submitter = ff.getSubmittedBy();
        SubmissionDAO subDao = new SubmissionDAO();
        // source == null because it's a new file format therefore there is no existing one to compare
        Submission sub = new Submission(makeResource(PRONOM.Submission.id + UUID.randomUUID()),
                makeResource(PRONOM.Submission.InternalSubmission),
                makeResource(PRONOM.Submission.StatusWaiting),
                submitter.toObject(true),
                null,
                null,
                new TentativeFileFormat(f.getURI(), f),
                Instant.now(),
                null);
        subDao.saveSubmission(sub);
        redir.addFlashAttribute("feedback", new Feedback(Feedback.Status.SUCCESS, "Submission for new file format created successfully."));
        return new RedirectView("/editorial");
    }

    // Helpers
    private void setFormOptions(Model model) {
        FormOptionsDAO dao = new FormOptionsDAO();
        Map<String, List<LabeledURI>> options = dao.getOptionsOfType(List.of(
                makeResource(PRONOM.Classification.type),
                makeResource(PRONOM.ByteOrder.type),
                makeResource(PRONOM.FormatIdentifierType.type),
                makeResource(PRONOM.ByteSequence.BSPType),
                makeResource(PRONOM.FormatRelationshipType.type),
                makeResource(PRONOM.FileFormatFamily.type),
                makeResource(PRONOM.CompressionType.type)
        ));
        model.addAttribute("classificationOptions", options.get(PRONOM.Classification.type));
        model.addAttribute("byteOrderOptions", options.get(PRONOM.ByteOrder.type));
        model.addAttribute("formatIdentifierOptions", options.get(PRONOM.FormatIdentifierType.type));
        List<LabeledURI> sortedPosTypes = options.get(PRONOM.ByteSequence.BSPType).stream()
                .sorted(Comparator.comparing(l -> safelyGetUriOrNull(l.getURI())))
                .collect(Collectors.toList());
        model.addAttribute("positionTypeOptions", sortedPosTypes);
        List<LabeledURI> relTypes = options.get(PRONOM.FormatRelationshipType.type);
        model.addAttribute("relationshipTypeOptions", relTypes);
        List<LabeledURI> noPriority = relTypes.stream()
                .filter(o -> o != null && o.getURI() != null && !o.getURI().getURI().equals(PRONOM.FormatRelationshipType.PriorityOver))
                .collect(Collectors.toList());
        model.addAttribute("relationshipTypeNoPriorityOptions", noPriority);
        model.addAttribute("formatFamilyOptions", options.get(PRONOM.FileFormatFamily.type));
        model.addAttribute("compressionTypeOptions", options.get(PRONOM.CompressionType.type));
    }
}
