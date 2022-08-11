package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.dao.ContainerSignatureDAO;
import com.wallscope.pronombackend.dao.FileFormatDAO;
import com.wallscope.pronombackend.dao.SearchDAO;
import com.wallscope.pronombackend.model.ContainerSignature;
import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.model.InternalSignature;
import com.wallscope.pronombackend.model.SearchResult;
import com.wallscope.pronombackend.soap.Converter;
import com.wallscope.pronombackend.soap.SignatureFileWrapper;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.nationalarchives.pronom.signaturefile.ByteSequenceType;
import uk.gov.nationalarchives.pronom.signaturefile.SignatureFileType;

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
        SignatureFileType signatureFileType = new SignatureFileType();
        // Set Version: for now we hardcode at 100 which is what the data is based off of
        signatureFileType.setVersion(BigInteger.valueOf(100));
        // DateCreated is set to 'now'
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        XMLGregorianCalendar xCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal); // setDateCreated
        signatureFileType.setDateCreated(xCal);
        // Convert the FileFormat collection using the helper class
        signatureFileType.setFileFormatCollection(Converter.convertFileFormatCollection(fs));
        // Convert the InternalSignature collection using the helper class
        JAXBContext ctx = JAXBContext.newInstance(ByteSequenceType.class);
        signatureFileType.setInternalSignatureCollection(Converter.convertInternalSignatureCollection(signatures, ctx));
        wrapper.setSignatureFile(signatureFileType);
        return wrapper;
    }

    @GetMapping(value = {"/container-signature.xml"}, produces = "text/xml")
    public String xmlContainerSignatureHandler(Model model, @RequestParam(required = false) Boolean dev) {
        ContainerSignatureDAO dao = new ContainerSignatureDAO();
        List<FileFormat> fs = dao.getAllFileFormats();
        List<ContainerSignature> signatures = fs.stream().flatMap(f -> f.getContainerSignatures().stream())
                .filter(distinctByKey(ContainerSignature::getID))
                .sorted(Comparator.comparingInt(f -> Integer.parseInt(f.getID())))
                .collect(Collectors.toList());
        List<ContainerSignature.ContainerType> cts = dao.getTriggerPuids();

        fs.sort(Comparator.comparingInt(f -> Integer.parseInt(f.getID())));
        model.addAttribute("formats", fs);
        model.addAttribute("containerSignatures", signatures);
        model.addAttribute("containerTypes", cts);
        model.addAttribute("dev", dev);
        return "xml_container_signatures";
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
