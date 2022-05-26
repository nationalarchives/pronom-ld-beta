package com.wallscope.pronombackend;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import uk.gov.nationalarchives.pronom.*;
import uk.gov.nationalarchives.pronom.signaturefile.SigFile;
import uk.gov.nationalarchives.pronom.signaturefile.SignatureFileType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;

@Endpoint
public class EndpointSOAP {

    private static final String NAMESPACE_URI = "http://pronom.nationalarchives.gov.uk";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSignatureFileV1")
    @ResponsePayload
    public GetSignatureFileV1Response getSignatureFileV1(@RequestPayload GetSignatureFileV1 request) throws DatatypeConfigurationException {
        GetSignatureFileV1Response response = new GetSignatureFileV1Response();

        SignatureFileType signatureFileType = new SignatureFileType();

        // dummy data
        BigInteger bigInt = BigInteger.valueOf(1); // setVersion

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        XMLGregorianCalendar xCal = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(cal); // setDateCreated

        SignatureFileType.FileFormatCollection fileFormatCollection =
                new SignatureFileType.FileFormatCollection(); // setFileFormatCollection

        SignatureFileType.InternalSignatureCollection internalSignatureCollection =
                new SignatureFileType.InternalSignatureCollection(); // InternalSignatureCollection

        // prepare response data
        signatureFileType.setVersion(bigInt);
        signatureFileType.setDateCreated(xCal);
        signatureFileType.setFileFormatCollection(fileFormatCollection);
        signatureFileType.setInternalSignatureCollection(internalSignatureCollection);
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
}
