package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.dao.FormOptionsDAO;
import com.wallscope.pronombackend.dao.GenericEntityDAO;
import com.wallscope.pronombackend.model.PUID;
import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

/*
 * This controller handles all calls to pages where the url is actually an entity URI and it serves as a de-referencing handler
 * So the user is redirected to different HTML handlers depending on the type of item that was requested.
 * */
@Controller
public class URIController {
    Logger logger = LoggerFactory.getLogger(URIController.class);

    @GetMapping("/id/{type}/{id}")
    public String uriHandler(Model model, @PathVariable String type, @PathVariable String id, @RequestParam(required = false, name = "format") String ext) {
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
        List<String> puidTypes = List.of("FileFormat", "Software", "Encoding", "CompressionType");
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
        return "forward:/generic/" + type + "/" + id;
    }

    @GetMapping(value = {"/generic/{type}/{id}"})
    public String genericEntityHandler(Model model, @PathVariable String type, @PathVariable String id) {
        Resource uri = makeResource(PRONOM.uri + "id/" + type + '/' + id);
        GenericEntityDAO dao = new GenericEntityDAO();
        ModelUtil mu = new ModelUtil(dao.fetchForURI(uri));
        model.addAttribute("isPuid", false);
        model.addAttribute("type", type);
        model.addAttribute("id", id);
        Map<String, List<RDFNode>> props = mu.getPropertyMap(uri);
        if (props.containsKey(RDFS.label)) {
            model.addAttribute("name", props.get(RDFS.label).stream().findFirst().orElse(makeLiteral("")));
        }
        model.addAttribute("props", props);
        // Fetch and add extra labels that are not hard coded
        FormOptionsDAO labelsDao = new FormOptionsDAO();
        Map<String, String> extra = new HashMap<>();
        labelsDao.getOptionsOfType(List.of(
                makeResource(PRONOM.ByteSequence.BSPType),
                makeResource(PRONOM.ActorType.type),
                makeResource(PRONOM.PuidType.type)
        )).values().forEach(list -> list.forEach(lu -> extra.put(lu.getURI().getURI(), lu.getLabel())));
        model.addAttribute("extra", extra);
        return "generic-puid";
    }
}
