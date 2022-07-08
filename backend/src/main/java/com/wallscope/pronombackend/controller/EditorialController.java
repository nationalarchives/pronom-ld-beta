package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.dao.FileFormatDAO;
import com.wallscope.pronombackend.dao.FormOptionsDAO;
import com.wallscope.pronombackend.dao.SubmissionDAO;
import com.wallscope.pronombackend.model.FormOption;
import com.wallscope.pronombackend.model.PUID;
import com.wallscope.pronombackend.model.Submission;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.PRONOM;
import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

/*
 * This controller handles all calls to pages in the editorial interface
 * */
@Controller
public class EditorialController {
    Logger logger = LoggerFactory.getLogger(EditorialController.class);

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @GetMapping("/editorial/search")
    public String searchHandler(
            Model model,
            // The search query
            @RequestParam(required = false) String q,
            // limit and offset for pagination
            Optional<Integer> offset,
            // Sort by field
            Optional<String> sort,
            // Search field filters
            Optional<Boolean> f_name,
            Optional<Boolean> f_ext,
            Optional<Boolean> f_desc,
            Optional<Boolean> f_puid,
            Optional<Integer> pageSize
    ) {
        // reuse the search controller code to populate the search results
        model.addAttribute("editorial", true);
        new SearchController().searchHandler(model, q, offset, sort, f_name, f_ext, f_desc, f_puid, pageSize);
        return "internal-search";
    }

    @GetMapping("/editorial/id/{type}/{id}")
    public String uriHandler(Model model, @PathVariable(required = true) String type, @PathVariable(required = true) String id) {
        Resource uri = makeResource(PRONOM.uri + "id/" + type + '/' + id);
        switch (type) {
            case "FileFormat":
                FileFormatDAO dao = new FileFormatDAO();
                PUID puid = dao.getPuidForURI(uri);
                return "redirect:/editorial/form/" + puid.type.trim() + "/" + puid.puid;
            case "Actor":
                return "redirect:/actor/" + id;
            default:
                return "index";
        }
    }

    @PostMapping("/editorial/move-submission")
    public String moveSubmission(Model model, @RequestParam String uri, @RequestParam String to) {
        if (!uri.startsWith(PRONOM.Submission.id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid submission uri: " + uri);
        }
        String statusUri = switch (to) {
            case "Waiting" -> PRONOM.Submission.StatusWaiting;
            case "NextRelease" -> PRONOM.Submission.StatusNextRelease;
            case "WIP" -> PRONOM.Submission.StatusWIP;
            case "Testing" -> PRONOM.Submission.StatusTesting;
            case "Ready" -> PRONOM.Submission.StatusReady;
            default -> null;
        };
        if (statusUri == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid status: " + to);
        }
        SubmissionDAO dao = new SubmissionDAO();
        dao.moveSubmission(makeResource(uri), makeResource(statusUri));
        return "redirect:/editorial";
    }

    @PostMapping("/editorial/delete-submission")
    public String deleteSubmission(Model model, @RequestParam String uri) {
        if (!uri.startsWith(PRONOM.Submission.id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid submission uri: " + uri);
        }
        SubmissionDAO dao = new SubmissionDAO();
        dao.deleteSubmission(makeResource(uri));
        return "redirect:/editorial";
    }

    @GetMapping("/editorial")
    public String dashboard(Model model) {
        SubmissionDAO dao = new SubmissionDAO();
        List<Submission> subs = dao.getAllSubmissions();
        Map<String, List<Submission>> subsMap = subs.stream().collect(Collectors.groupingBy(s -> s.getSubmissionStatus().getLocalName()));
        model.addAttribute("submissions", subs);
        model.addAttribute("submissionMap", subsMap);
        logger.trace("SUBMISSIONS: " + subs);
        logger.trace("SUBMISSIONS MAP: " + subsMap);
        return "dashboard";
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