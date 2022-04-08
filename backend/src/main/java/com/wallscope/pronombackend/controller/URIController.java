package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.dao.FileFormatDAO;
import com.wallscope.pronombackend.model.PUID;
import com.wallscope.pronombackend.utils.RDFUtil;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/*
 * This controller handles all calls to pages where the url is actually an entity URI and it serves as a de-referencing handler
 * So the user is redirected to different HTML handlers depending on the type of item that was requested.
 * */
@Controller
public class URIController {
    Logger logger = LoggerFactory.getLogger(URIController.class);

    @GetMapping("/id/{type}/{id}")
    public String uriHandler(Model model, @PathVariable(required = true) String type, @PathVariable(required = true) String id) {
        Resource uri = RDFUtil.makeResource(RDFUtil.PRONOM.uri + "id/" + id);
        switch (type) {
            case "FileFormat":
                FileFormatDAO dao = new FileFormatDAO();
                PUID puid = dao.getPuidForURI(uri);
                return "redirect:/" + puid.type.trim() + "/" + puid;
            case "Actor":
                return "redirect:/actor/" + id;
            default:
                return "index";
        }
    }
}
