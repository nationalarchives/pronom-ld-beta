package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.dao.FileFormatDAO;
import com.wallscope.pronombackend.dao.GenericEntityDAO;
import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

/*
 * This controller handles all calls to pages where the content does not depend on fetching data from the database
 * Hence the name static. The actual content may change based on the Markdown workflow
 * */
@Controller
public class PUIDController {
    Logger logger = LoggerFactory.getLogger(PUIDController.class);

    @GetMapping(value = {"/fmt/{puid}", "/x-fmt/{puid}"})
    public String fileFormatHandler(Model model, HttpServletRequest request, @PathVariable(required = false) String puid, @RequestParam(required = false, name = "format") String ext) {
        String[] parts = request.getRequestURI().split("/");
        String puidType = parts[parts.length - 2];
        // If there's a ?format= query param we append the file extension to the puid to be handled by the RDF matcher
        if (ext != null && !ext.isBlank()) {
            puid = puid + "." + ext;
        }
        // Respond to RDF request
        if (puid.contains(".")) {
            return "forward:/rdf/" + puidType + "/" + puid;
        }
        FileFormatDAO dao = new FileFormatDAO();
        FileFormat f = dao.getFileFormatByPuid(puid, puidType);
        if (f == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no File Format with puid: " + puid);
        }
        logger.trace("File format: " + f);
        model.addAttribute("ff", f);
        return "file-format";
    }

    @GetMapping(value = {"/fmt/{puid}.xml", "/x-fmt/{puid}.xml"}, produces = "text/xml")
    public String xmlSingleSignatureHandler(Model model, HttpServletRequest request, HttpServletResponse response, @PathVariable(required = false) String puid) {
        String[] parts = request.getRequestURI().split("/");
        String puidType = parts[parts.length - 2];
        FileFormatDAO dao = new FileFormatDAO();
        FileFormat f = dao.getFileFormatByPuid(puid, puidType);
        if (f == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no File Format with puid: " + puid);
        }
        model.addAttribute("f", f);
        String version = f.getVersion() != null && !f.getVersion().isBlank() ? " (" + f.getVersion() + ")" : "";
        String fileName = f.getName() != null && !f.getName().isBlank() ? f.getName() + version : "DetailedFileFormatReport";
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName + ".xml");
        return "xml_fileformat";
    }

    @GetMapping(value = {"/chr/{puid}", "/x-chr/{puid}", "/sfw/{puid}", "/x-sfw/{puid}", "/cmp/{puid}", "/x-cmp/{puid}"})
    public String genericPUIDHandler(Model model, HttpServletRequest request, @PathVariable(required = false) String puid, @RequestParam(required = false, name = "format") String ext) {
        String[] parts = request.getRequestURI().split("/");
        String puidType = parts[parts.length - 2];
        // If there's a ?format= query param we append the file extension to the puid to be handled by the RDF matcher
        if (ext != null && !ext.isBlank()) {
            puid = puid + "." + ext;
        }
        // Respond to RDF request
        if (puid.contains(".")) {
            return "forward:/rdf/" + puidType + "/" + puid;
        }
        GenericEntityDAO dao = new GenericEntityDAO();
        Resource uri = dao.getURIForPuid(puidType, puid);
        if (uri == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no entity with puid: " + puidType + "/" + puid);
        }
        ModelUtil mu = new ModelUtil(dao.fetchForURI(uri));
        model.addAttribute("isPuid", true);
        Map<String, List<RDFNode>> props = mu.getPropertyMap(uri);
        model.addAttribute("props", props);
        if (props.containsKey(RDFS.label)) {
            model.addAttribute("name", safelyGetStringOrNull(props.get(RDFS.label).get(0)));
        }
        if (props.containsKey(SKOS.notation)) {
            model.addAttribute("puid", safelyGetStringOrNull(props.get(SKOS.notation).get(0)));
        }
        return "generic-puid";
    }
}
