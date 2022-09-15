package com.wallscope.pronombackend.soap;

import com.wallscope.pronombackend.utils.SignatureStorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import uk.gov.nationalarchives.pronom.GetSignatureFileV1;
import uk.gov.nationalarchives.pronom.GetSignatureFileV1Response;
import uk.gov.nationalarchives.pronom.GetSignatureFileVersionV1Response;
import uk.gov.nationalarchives.pronom.Version;
import uk.gov.nationalarchives.pronom.signaturefile.SigFile;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.FileNotFoundException;

@Endpoint
public class Endpoints {

    private static final String NAMESPACE_URI = "http://pronom.nationalarchives.gov.uk";
    Logger logger = LoggerFactory.getLogger(Endpoints.class);

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSignatureFileV1")
    @ResponsePayload
    public GetSignatureFileV1Response getSignatureFileV1(@RequestPayload GetSignatureFileV1 request) throws DatatypeConfigurationException, JAXBException, FileNotFoundException {
        // Instantiate SOAP classes
        GetSignatureFileV1Response response = new GetSignatureFileV1Response();
        // Read in XML file
        BinarySignatureFileWrapper sig = SignatureStorageManager.readInXML(SignatureStorageManager.getLatestBinarySignature(), BinarySignatureFileWrapper.class);
        SigFile sigFile = new SigFile();
        sigFile.setFFSignatureFile(sig.toJAXBObject());
        response.setSignatureFile(sigFile);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSignatureFileVersionV1")
    @ResponsePayload
    public GetSignatureFileVersionV1Response getSignatureFileVersionV1Response() throws FileNotFoundException {
        GetSignatureFileVersionV1Response response = new GetSignatureFileVersionV1Response();

        Version version = new Version();
        Integer vNumber = SignatureStorageManager.versionFromFile(SignatureStorageManager.getLatestBinarySignature());
        if (vNumber != null) {
            version.setVersion(vNumber);
        }

        // prepare response data
        response.setVersion(version);
        response.setDeprecated(false);

        return response;
    }
}
