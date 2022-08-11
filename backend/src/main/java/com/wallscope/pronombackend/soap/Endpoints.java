package com.wallscope.pronombackend.soap;

import com.wallscope.pronombackend.dao.FileFormatDAO;
import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.model.InternalSignature;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import uk.gov.nationalarchives.pronom.GetSignatureFileV1;
import uk.gov.nationalarchives.pronom.GetSignatureFileV1Response;
import uk.gov.nationalarchives.pronom.GetSignatureFileVersionV1Response;
import uk.gov.nationalarchives.pronom.Version;
import uk.gov.nationalarchives.pronom.signaturefile.SigFile;
import uk.gov.nationalarchives.pronom.signaturefile.SignatureFileType;

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

@Endpoint
public class Endpoints {

    private static final String NAMESPACE_URI = "http://pronom.nationalarchives.gov.uk";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSignatureFileV1")
    @ResponsePayload
    public GetSignatureFileV1Response getSignatureFileV1(@RequestPayload GetSignatureFileV1 request) throws DatatypeConfigurationException, JAXBException {
        // Fetch data using DAO
        FileFormatDAO dao = new FileFormatDAO();
        List<FileFormat> fs = dao.getAllForSignature();
        List<InternalSignature> signatures = fs.stream().flatMap(f -> f.getInternalSignatures().stream())
                .filter(distinctByKey(InternalSignature::getID))
                .sorted(Comparator.comparingInt(f -> Integer.parseInt(f.getID())))
                .collect(Collectors.toList());

        fs.sort(Comparator.comparingInt(f -> Integer.parseInt(f.getID())));

        // Instantiate SOAP classes
        GetSignatureFileV1Response response = new GetSignatureFileV1Response();
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
        signatureFileType.setInternalSignatureCollection(Converter.convertInternalSignatureCollection(signatures));
        SigFile sigFile = new SigFile();
        sigFile.setFFSignatureFile(signatureFileType);
        response.setSignatureFile(sigFile);

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSignatureFileVersionV1")
    @ResponsePayload
    public GetSignatureFileVersionV1Response getSignatureFileVersionV1Response() {
        GetSignatureFileVersionV1Response response = new GetSignatureFileVersionV1Response();

        // dummy data
        Version version = new Version();
        version.setVersion(1);
        Boolean deprecated = false;

        // prepare response data
        response.setVersion(version);
        response.setDeprecated(deprecated);

        return response;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
