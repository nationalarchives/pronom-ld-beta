package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.dao.ActorDAO;
import com.wallscope.pronombackend.dao.GenericEntityDAO;
import com.wallscope.pronombackend.dao.SubmissionDAO;
import com.wallscope.pronombackend.model.*;
import org.apache.jena.rdf.model.Resource;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
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
            , Principal principal) {
        model.addAttribute("user", hydrateUser(principal));
        // reuse the search controller code to populate the search results
        model.addAttribute("editorial", true);
        new SearchController().searchHandler(model, q, offset, sort, f_name, f_ext, f_desc, f_puid, pageSize);
        return "internal-search";
    }

    @GetMapping("/editorial/id/{type}/{id}")
    public String uriHandler(Model model, @PathVariable String type, @PathVariable String id, @RequestParam(required = false, name = "format") String ext, Principal principal) {
        if (ext != null && !ext.isBlank()) {
            id = id + "." + ext;
        }
        String formatExt = "";
        if (id.contains(".")) {
            String[] parts = id.split("\\.");
            if (parts.length == 2) {
                id = parts[0];
                formatExt = "." + parts[1];
            }
        }
        Resource uri = makeResource(PRONOM.uri + "id/" + type + '/' + id);
        List<String> puidTypes = List.of("FileFormat", "TentativeFileFormat","Software", "Encoding", "CompressionType");
        if (puidTypes.contains(type)) {
            GenericEntityDAO dao = new GenericEntityDAO();
            PUID puid = dao.getPuidForURI(uri);
            if (puid == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no entity with id: " + type + "/" + id);
            }
            return "redirect:/" + puid.type.trim() + "/" + puid.puid + formatExt;
        }
        if (!formatExt.isBlank()) {
            return "forward:/rdf/generic/" + type + "/" + id + formatExt;
        }
        model.addAttribute("user", hydrateUser(principal));
        return "forward:/generic/" + type + "/" + id;
    }

    @PostMapping("/editorial/move-submission")
    public RedirectView moveSubmission(Model model, @RequestParam String uri, @RequestParam String to, RedirectAttributes redir, Principal principal) {
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
        if (statusUri.equals(PRONOM.Submission.StatusReady)) {
            Submission sub = dao.getSubmissionByURI(makeResource(uri));
            if (sub == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid submission: " + uri);
            }
            TentativeFileFormat tff = sub.getFormat();
            if (tff == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid submission: " + uri);
            }
            if (tff.getFormattedPuid() == null) {
                redir.addFlashAttribute("feedback", new Feedback(Feedback.Status.ERROR, "A file format needs a PUID before it can be marked as Ready"));
                return new RedirectView("/editorial");
            }
        }
        dao.moveSubmission(makeResource(uri), makeResource(statusUri));
        model.addAttribute("user", hydrateUser(principal));
        return new RedirectView("/editorial");
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
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("user", hydrateUser(principal));
        logger.debug("PRINCIPAL: " + principal);
        logger.debug("HYDRATED: " + hydrateUser(principal));
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
    public String actorDisplay(Model model, @PathVariable(required = true) String id, Principal principal) {
        model.addAttribute("user", hydrateUser(principal));
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
    public RedirectView submissionForm(Model model, @PathVariable String id, @ModelAttribute FormActor fa, RedirectAttributes redir, Principal principal) {
        model.addAttribute("user", hydrateUser(principal));
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

    @GetMapping("/editorial/logout")
    public RedirectView logout(HttpServletRequest request) {
        try {
            request.logout();
        } catch (ServletException e) {
            logger.error("ERROR LOGGING OUT: " + e);
        }
        return new RedirectView("/");
    }

    public static class User {
        private final String name;

        protected User(String name) {
            this.name = name;
        }

        protected User(AccessToken token) {
            if (token.getGivenName() != null && !token.getGivenName().isBlank() && token.getFamilyName() != null && !token.getFamilyName().isBlank()) {
                this.name = token.getGivenName() + " " + token.getFamilyName();
                return;
            }
            if (token.getName() != null && !token.getName().isBlank()) {
                this.name = token.getName();
                return;
            }
            if (token.getPreferredUsername() != null && !token.getPreferredUsername().isBlank()) {
                this.name = token.getPreferredUsername();
                return;
            }
            if (token.getEmail() != null && !token.getEmail().isBlank()) {
                this.name = token.getEmail();
                return;
            }
            this.name = "Unknown";
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    public static User hydrateUser(Principal principal) {
        KeycloakAuthenticationToken auth = (KeycloakAuthenticationToken) principal;
        if (auth == null) return null;
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) auth.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();
        return new User(token);
    }
}
