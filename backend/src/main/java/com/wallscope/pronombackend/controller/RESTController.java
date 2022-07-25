package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.dao.SearchDAO;
import com.wallscope.pronombackend.model.SearchResult;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.PRONOM;
import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

/*
 * This controller handles all calls to pages where the content does not depend on fetching data from the database
 * Hence the name static. The actual content may change based on the Markdown workflow
 * */
@RestController
public class RESTController {
    Logger logger = LoggerFactory.getLogger(RESTController.class);
    private static final Map<String, Resource> fieldMapping = Map.of(
            "ff", makeResource(PRONOM.FileFormat.type),
            "actor", makeResource(PRONOM.Actor.type)
    );

    @GetMapping(value = "/autocomplete/{field}", produces = "application/json")
    public List<Map<String, String>> relNotes(Model model, @PathVariable(required = false) String field, @RequestParam String term) {
        SearchDAO dao = new SearchDAO();
        if (!fieldMapping.containsKey(field)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid autocomplete field: " + field);
        }
        Resource fieldType = fieldMapping.get(field);
        List<SearchResult> results = dao.autocomplete(term, fieldType);
        return results.stream().map(r -> Map.of(
                "label", r.getName() + (r.getPuid() != null && !r.getPuid().isBlank() ? " (" + r.getPuid() + ")" : ""),
                "value", r.getURI().getURI()
        )).collect(Collectors.toList());
    }
}
