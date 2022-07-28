package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.dao.ActorDAO;
import com.wallscope.pronombackend.dao.FileFormatDAO;
import com.wallscope.pronombackend.dao.SubmissionDAO;
import com.wallscope.pronombackend.model.*;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
                return "redirect:/editorial/actor/" + id;
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

    @GetMapping("/editorial/actor/{id}")
    public String actorDisplay(Model model, @PathVariable(required = true) String id) {
        if (id.equals("new")) {
            model.addAttribute("actor", new FormActor());
            return "actor";
        }
        ActorDAO dao = new ActorDAO();
        Actor actor = dao.getActorByURI(makeResource(PRONOM.Actor.id + id));
        if (actor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no Actor with id: " + id);
        }
        logger.debug("Actor: " + actor);
        model.addAttribute("actor", actor.convert());
        return "actor";
    }

    @PostMapping("/editorial/actor/{id}")
    public RedirectView submissionForm(Model model, @PathVariable String id, @ModelAttribute FormActor fa, RedirectAttributes redir) {
        ActorDAO dao = new ActorDAO();
        if (id.equals("new")) {
            fa.setUri(PRONOM.Actor.id + UUID.randomUUID());
        }
        if (fa.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The name field is required");
        }
        if (!id.equals("new")) {
            dao.deleteActor(makeResource(PRONOM.Actor.id + id));
        }
        Actor act = fa.toObject();
        dao.saveActor(act);
        redir.addFlashAttribute("feedback", new Feedback(Feedback.Status.SUCCESS, "Actor " + act.getDisplayName() + "(id:" + act.getID() + ") saved successfully."));
        return new RedirectView("/editorial/actor/" + act.getID());
    }
}
