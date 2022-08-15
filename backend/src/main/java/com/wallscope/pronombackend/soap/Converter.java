package com.wallscope.pronombackend.soap;

import com.wallscope.pronombackend.model.ByteSequence;
import com.wallscope.pronombackend.model.ExternalSignature;
import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.model.InternalSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.nationalarchives.pronom.signaturefile.ByteSequenceType;
import uk.gov.nationalarchives.pronom.signaturefile.FileFormatType;
import uk.gov.nationalarchives.pronom.signaturefile.InternalSignatureType;
import uk.gov.nationalarchives.pronom.signaturefile.SignatureFileType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Converter {
    static Logger logger = LoggerFactory.getLogger(Converter.class);

    public static SignatureFileType.FileFormatCollection convertFileFormatCollection(List<FileFormat> ffs) {
        SignatureFileType.FileFormatCollection collection = new SignatureFileType.FileFormatCollection();
        List<FileFormatType> listRef = collection.getFileFormat();
        ffs.forEach(ff -> {
            FileFormatType conv = new FileFormatType();
            conv.setID(new BigInteger(ff.getID()));
            conv.setMIMEType(ff.getMIMETypeList());
            conv.setName(ff.getName());
            conv.setPUID(ff.getFormattedPuid());
            conv.setVersion(ff.getVersion());
            List<JAXBElement<? extends Serializable>> children = conv.getInternalSignatureIDOrExtensionOrHasPriorityOverFileFormatID();
            // Add InternalSignatureID elements
            QName isID = new QName("InternalSignatureID");
            List<JAXBElement<BigInteger>> isIDs = ff.getInternalSignatures().stream()
                    .sorted(Comparator.comparingInt(s -> Integer.parseInt(s.getID())))
                    .map(is -> new JAXBElement<>(isID, BigInteger.class, new BigInteger(is.getID())))
                    .collect(Collectors.toList());
            children.addAll(isIDs);
            // Add Extension (External signatures) elements
            QName extID = new QName("Extension");
            List<JAXBElement<String>> exts = ff.getExternalSignatures().stream()
                    // get only the file extensions
                    .filter(es -> es.getSignatureType().equals("File extension"))
                    .sorted(Comparator.comparing(ExternalSignature::getName))
                    .map(es -> new JAXBElement<>(extID, String.class, es.getName()))
                    .collect(Collectors.toList());
            children.addAll(exts);
            // Add PriorityOver elements
            QName pID = new QName("HasPriorityOverFileFormatID");
            List<JAXBElement<BigInteger>> pIDs = ff.getHasPriorityOver().stream()
                    .sorted(Comparator.comparingInt(p -> Integer.parseInt(p.getTargetID())))
                    .map(ps -> new JAXBElement<>(pID, BigInteger.class, new BigInteger(ps.getTargetID())))
                    .collect(Collectors.toList());
            children.addAll(pIDs);

            listRef.add(conv);
        });
        return collection;
    }

    public static SignatureFileType.InternalSignatureCollection convertInternalSignatureCollection(List<InternalSignature> iss) throws JAXBException {
        return convertInternalSignatureCollection(iss, JAXBContext.newInstance(ByteSequenceType.class));
    }

    public static SignatureFileType.InternalSignatureCollection convertInternalSignatureCollection(List<InternalSignature> iss, JAXBContext jaxbContext) {
        SignatureFileType.InternalSignatureCollection collection = new SignatureFileType.InternalSignatureCollection();
        List<InternalSignatureType> listRef = collection.getInternalSignature();
        iss.forEach(is -> {
            InternalSignatureType conv = new InternalSignatureType();
            conv.setID(new BigInteger(is.getID()));
            conv.setSpecificity(is.getSpecificity());
            List<ByteSequenceType> bsList = conv.getByteSequence();

            bsList.addAll(convertByteSequenceList(is.getByteSequences(), jaxbContext));

            listRef.add(conv);
        });
        return collection;
    }

    private static List<ByteSequenceType> convertByteSequenceList(List<ByteSequence> bss, JAXBContext jaxbContext) {
        return bss.stream().map(bs -> {
            try {
                String XML = bs.toXML(false);

                XMLInputFactory xif = XMLInputFactory.newFactory();
                StreamSource xml = new StreamSource(new StringReader(XML));
                XMLStreamReader xsr = xif.createXMLStreamReader(xml);

                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                JAXBElement<ByteSequenceType> conv = jaxbUnmarshaller.unmarshal(xsr, ByteSequenceType.class);

                return conv.getValue();
            } catch (JAXBException | XMLStreamException e) {
                logger.error("ERROR PARSING CONVERTED XML FOR BYTE SEQUENCE " + bs.getURI() + ": " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
