package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.dao.SearchDAO;
import com.wallscope.pronombackend.model.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/*
 * This controller handles all calls to pages where the content does not depend on fetching data from the database
 * Hence the name static. The actual content may change based on the Markdown workflow
 * */
@Controller
public class SearchController {
    Logger logger = LoggerFactory.getLogger(SearchController.class);

    @GetMapping("/search")
    public String fileFormatHandler(Model model, @RequestParam(required = false) String q, Optional<Integer> limit, Optional<Integer> offset, Optional<String> sort) {
        // if no query parameter is specified we early return the template
        if (q == null) return "search";

        Integer limitVal = limit.orElse(10);
        Integer offsetVal = offset.orElse(0);
        SearchDAO dao = new SearchDAO();
        List<SearchResult> results = dao.search(q, limitVal, offsetVal);
        // default is "relevance" which sorts by the score property
        switch (sort.orElse("score")) {
            case "name":
                results.sort(Comparator.comparing(SearchResult::getName));
                model.addAttribute("sort", "name");
                break;
            case "type":
                results.sort(Comparator.comparing(SearchResult::getStringType));
                model.addAttribute("sort", "type");
                break;
            case "puid":
                results.sort(Comparator.comparing(SearchResult::getPuid));
                model.addAttribute("sort", "puid");
                break;
            case "relevance":
            default:
                results.sort(Comparator.comparing(SearchResult::getScore));
                model.addAttribute("sort", "relevance");
                break;
        }
        model.addAttribute("results", results);
        return "search";
    }
}
