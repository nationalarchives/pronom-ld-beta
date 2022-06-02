package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.dao.SearchDAO;
import com.wallscope.pronombackend.model.PaginationHelper;
import com.wallscope.pronombackend.model.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
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

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @GetMapping("/search")
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
        // if no query parameter is specified we early return the template with only the default variables set
        Integer limitVal = pageSize.orElse(10);
        model.addAttribute("pageSize", limitVal);
        if (q == null || q.isBlank()) return "search";
        Integer offsetVal = offset.orElse(0);
        SearchDAO dao = new SearchDAO();
        SearchDAO.Filters filters = new SearchDAO.Filters(f_name.orElse(false), f_ext.orElse(false), f_desc.orElse(false), f_puid.orElse(false));
        Integer totalResults = dao.count(q, limitVal, offsetVal, filters);
        List<SearchResult> results = dao.search(q, limitVal, offsetVal, filters, sort.orElse("score"));
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
                results.sort(Comparator.comparing(SearchResult::getPuidSortNumber));
                model.addAttribute("sort", "puid");
                break;
            case "relevance":
            default:
                results.sort(Comparator.comparing(SearchResult::getScore));
                model.addAttribute("sort", "relevance");
                break;
        }
        // Pagination
        List<PaginationHelper> pages = new ArrayList<>();
        int current = (offsetVal / limitVal) + 1;
        if (current > 1) pages.add(new PaginationHelper("Previous", Math.max(offsetVal - limitVal, 0), false));
        if (current <= 2) {
            pages.add(new PaginationHelper("1", 0, current == 1));
            if (totalResults > limitVal) pages.add(new PaginationHelper("2", limitVal, current == 2));
            if (totalResults > limitVal * 2) pages.add(new PaginationHelper("3", limitVal * 2, false));
        } else {
            pages.add(new PaginationHelper("" + (current - 1), (current - 2) * limitVal, false));
            pages.add(new PaginationHelper("" + current, (current - 1) * limitVal, true));
            if (totalResults > limitVal * current)
                pages.add(new PaginationHelper("" + (current + 1), current * limitVal, false));
        }
        if (totalResults > limitVal * current) pages.add(new PaginationHelper("Next", offsetVal + limitVal, false));
        model.addAttribute("pages", pages);

        logger.trace("SERVING SEARCH RESULTS: " + results);

        // set the search and filter parameters as set by the user before rendering the template
        model.addAttribute("q", q);
        model.addAttribute("offset", offsetVal);
        model.addAttribute("totalResults", totalResults);
        model.addAttribute("f_name", filters.name);
        model.addAttribute("f_ext", filters.extension);
        model.addAttribute("f_desc", filters.description);
        model.addAttribute("f_puid", filters.extension);
        model.addAttribute("results", results);
        return "search";
    }
}
