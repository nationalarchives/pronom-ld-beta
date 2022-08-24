package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.dao.ContainerSignatureDAO;
import com.wallscope.pronombackend.dao.FileFormatDAO;
import com.wallscope.pronombackend.dao.GenericEntityDAO;
import com.wallscope.pronombackend.dao.SearchDAO;
import com.wallscope.pronombackend.model.ContainerSignature;
import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.model.InternalSignature;
import com.wallscope.pronombackend.model.SearchResult;
import com.wallscope.pronombackend.soap.Converter;
import com.wallscope.pronombackend.soap.SignatureFileWrapper;
import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.nationalarchives.pronom.signaturefile.ByteSequenceType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

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
        return results.stream().map(r -> {
            String label = r.getName() + (r.getPuid() != null && !r.getPuid().isBlank() ? " (" + r.getPuid() + ")" : "");
            if (fieldType.getURI().equals(PRONOM.Actor.type)) {
                String hiddenLabelStr = safelyGetStringOrDefault(r.getProperties().getOrDefault(SKOS.hiddenLabel, null), "");
                label = !hiddenLabelStr.isEmpty() ? label + " | " + hiddenLabelStr : label;
            }
            return Map.of("label", label, "value", r.getURI().getURI());
        }).collect(Collectors.toList());
    }

    @GetMapping(value = {"/signature.xml"}, produces = {"application/xml", "text/xml"})
    @ResponseBody
    public SignatureFileWrapper xmlAllSignatureHandler(Model model, @RequestParam(required = false) Boolean dev) throws DatatypeConfigurationException, JAXBException {
        FileFormatDAO dao = new FileFormatDAO();
        List<FileFormat> fs = dao.getAllForSignature();
        List<InternalSignature> signatures = fs.stream().flatMap(f -> f.getInternalSignatures().stream())
                .filter(distinctByKey(InternalSignature::getID))
                .sorted(Comparator.comparingInt(f -> Integer.parseInt(f.getID())))
                .collect(Collectors.toList());

        fs.sort(Comparator.comparingInt(f -> Integer.parseInt(f.getID())));

        SignatureFileWrapper wrapper = new SignatureFileWrapper();
        // Set Version: for now we hardcode at 100 which is what the data is based off of
        wrapper.setVersion(BigInteger.valueOf(100));
        // DateCreated is set to 'now'
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        XMLGregorianCalendar xCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal); // setDateCreated
        wrapper.setDateCreated(xCal);
        // Convert the FileFormat collection using the helper class
        wrapper.setFileFormatCollection(Converter.convertFileFormatCollection(fs));
        // Convert the InternalSignature collection using the helper class
        JAXBContext ctx = JAXBContext.newInstance(ByteSequenceType.class);
        wrapper.setInternalSignatureCollection(Converter.convertInternalSignatureCollection(signatures, ctx));
        return wrapper;
    }

    @GetMapping(value = {"/container-signature.xml"}, produces = {"application/xml", "text/xml"})
    public ModelAndView xmlContainerSignatureHandler(Model model, @RequestParam(required = false) Boolean dev) {
        ModelAndView view = new ModelAndView();
        view.setViewName("xml_container_signatures");
        ContainerSignatureDAO dao = new ContainerSignatureDAO();
        List<FileFormat> fs = dao.getAllFileFormats();
        logger.trace("FORMATS: " + fs);
        List<ContainerSignature> signatures = fs.stream().flatMap(f -> f.getContainerSignatures().stream())
                .filter(distinctByKey(ContainerSignature::getID))
                .sorted(Comparator.comparingInt(f -> Integer.parseInt(f.getID())))
                .collect(Collectors.toList());
        logger.trace("SIGNATURES: " + signatures);
        List<ContainerSignature.ContainerType> cts = dao.getTriggerPuids();

        // Sort based on the ID of the container signature
        fs.sort(Comparator.comparingInt(f -> {
            if (f.getContainerSignatures() == null || f.getContainerSignatures().isEmpty()) return Integer.MAX_VALUE;
            f.getContainerSignatures().sort(Comparator.comparingInt(cs -> Integer.parseInt(cs.getID())));
            return Integer.parseInt(f.getContainerSignatures().get(0).getID());
        }));
        view.addObject("formats", fs);
        view.addObject("containerSignatures", signatures);
        view.addObject("containerTypes", cts);
        view.addObject("dev", dev);
        return view;
    }

    @GetMapping(value = {"/rdf/fmt/{puid}.{format}", "/rdf/x-fmt/{puid}.{format}"}, produces = {"text/turtle", "application/n-triples", "application/rdf+xml"})
    @ResponseBody
    public String rdfExportHandler(Model model, HttpServletRequest request, HttpServletResponse response, @PathVariable String puid, @PathVariable String format) {
        logger.debug("REQUEST FOR RDF FORMAT: " + format);
        Lang rdfLang = langFromFormat(format);
        if (rdfLang == null) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "rdf format not supported: " + format);
        }
        response.setContentType(rdfLang.getHeaderString());
        String[] parts = request.getRequestURI().split("/");
        String puidType = parts[parts.length - 2];
        FileFormatDAO dao = new FileFormatDAO();
        FileFormat f = dao.getFileFormatByPuid(puid, puidType);
        if (f == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no File Format with puid: " + puid);
        }
        ModelUtil mu = new ModelUtil(f.toRDF());

        return mu.toString(rdfLang);
    }

    @GetMapping(value = {"/rdf/generic/{type}/{id}.{format}"}, produces = {"text/turtle", "application/n-triples", "application/rdf+xml"})
    @ResponseBody
    public String genericRDFHandler(Model model, HttpServletRequest request, HttpServletResponse response, @PathVariable String type, @PathVariable String id, @PathVariable String format) {
        logger.debug("REQUEST FOR GENERIC RDF ENTITY: " + type + '/' + id);
        Lang rdfLang = langFromFormat(format);
        if (rdfLang == null) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "rdf format not supported: " + format);
        }
        response.setContentType(rdfLang.getHeaderString());
        Resource uri = makeResource(PRONOM.uri + "id/" + type + '/' + id);
        GenericEntityDAO dao = new GenericEntityDAO();
        ModelUtil mu = new ModelUtil(dao.fetchForURI(uri));

        return mu.toString(rdfLang);
    }

    private Lang langFromFormat(String format) {
        return switch (format) {
            // Frontend supported RDF formats
            case "nt", "ntriples" -> Lang.NT;
            case "ttl", "turtle" -> Lang.TTL;
            case "rdf" -> Lang.RDFXML;
            // Other Jena supported RDF formats
            case "n3" -> Lang.N3;
            case "jsonld" -> Lang.JSONLD;
            default -> null;
        };
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
