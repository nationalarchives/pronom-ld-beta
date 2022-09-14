package com.wallscope.pronombackend.soap;

import com.wallscope.pronombackend.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.nationalarchives.pronom.signaturefile.*;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Converter {
    static Logger logger = LoggerFactory.getLogger(Converter.class);

    public static SignatureFileType.FileFormatCollection convertFileFormatCollection(List<FileFormat> ffs) {
        SignatureFileType.FileFormatCollection collection = new SignatureFileType.FileFormatCollection();
        List<FileFormatType> listRef = collection.getFileFormat();
        ffs.forEach(ff -> {
            FileFormatType conv = new FileFormatType();
            conv.setID(ff.getID());
            conv.setMIMEType(ff.getMIMETypeList());
            conv.setName(ff.getName());
            conv.setPUID(ff.getFormattedPuid());
            conv.setVersion(ff.getVersion());
            List<JAXBElement<String>> children = conv.getInternalSignatureIDOrExtensionOrHasPriorityOverFileFormatID();
            // Add InternalSignatureID elements
            QName isID = new QName("InternalSignatureID");
            List<JAXBElement<String>> isIDs = ff.getInternalSignatures().stream()
                    .sorted(InternalSignature::compareTo)
                    .map(is -> new JAXBElement<>(isID, String.class, is.getID()))
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
            List<JAXBElement<String>> pIDs = ff.getHasPriorityOver().stream()
                    .sorted(Comparator.comparing(FileFormatRelationship::getTargetID))
                    .map(ps -> new JAXBElement<>(pID, String.class, ps.getTargetID()))
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
            conv.setID(is.getID());
            conv.setSpecificity(is.getSpecificity());
            List<ByteSequenceType> bsList = conv.getByteSequence();

            bsList.addAll(convertByteSequenceList(is.getByteSequences(), jaxbContext, false));

            listRef.add(conv);
        });
        return collection;
    }

    public static ContainerSignatureFileWrapper.ContainerSignatureCollection convertContainerSignatureCollection(List<ContainerSignature> css) throws JAXBException {
        return convertContainerSignatureCollection(css, JAXBContext.newInstance(ByteSequenceType.class));
    }

    public static ContainerSignatureFileWrapper.ContainerSignatureCollection convertContainerSignatureCollection(List<ContainerSignature> css, JAXBContext jaxbContext) {
        ContainerSignatureFileWrapper.ContainerSignatureCollection collection = new ContainerSignatureFileWrapper.ContainerSignatureCollection();
        List<ContainerSignatureFileWrapper.ContainerSignatureCollection.ContainerSignatureType> sigList = css.stream().map(cs -> {
            ContainerSignatureFileWrapper.ContainerSignatureCollection.ContainerSignatureType conv = new ContainerSignatureFileWrapper.ContainerSignatureCollection.ContainerSignatureType();
            conv.setID(cs.getID());
            conv.setContainerType(cs.getContainerTypeName());
            List<ContainerSignatureFileWrapper.ContainerSignatureCollection.ContainerSignatureType.FileType> fileList = cs.getFiles().stream().map(f -> {
                ContainerSignatureFileWrapper.ContainerSignatureCollection.ContainerSignatureType.FileType fileType = new ContainerSignatureFileWrapper.ContainerSignatureCollection.ContainerSignatureType.FileType();
                fileType.setPath(f.getPath());
                fileType.setByteSequence(convertByteSequenceList(f.getByteSequences().stream().filter(Objects::nonNull).sorted(ByteSequence::compareTo).collect(Collectors.toList()), jaxbContext, true));
                return fileType;
            }).collect(Collectors.toList());
            conv.setFiles(fileList);
            return conv;
        }).sorted(ContainerSignatureFileWrapper.ContainerSignatureCollection.ContainerSignatureType::compareTo).collect(Collectors.toList());
        collection.setContainerSignature(sigList);
        return collection;
    }

    public static List<ByteSequenceType> convertByteSequenceList(List<ByteSequence> bss, JAXBContext jaxbContext, boolean isContainer) {
        return bss.stream().map(bs -> convertByteSequence(bs, jaxbContext, isContainer)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static ByteSequenceType convertByteSequence(ByteSequence bs, JAXBContext jaxbContext, boolean isContainer) {
        try {
            String XML = bs.toXML(isContainer);
            if (XML == null) return null;
            XMLInputFactory xif = XMLInputFactory.newFactory();
            StreamSource xml = new StreamSource(new StringReader(XML));
            XMLStreamReader xsr = xif.createXMLStreamReader(xml);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<ByteSequenceType> elem = jaxbUnmarshaller.unmarshal(xsr, ByteSequenceType.class);
            if (elem == null) return null;
            ByteSequenceType converted = elem.getValue();
            Optional<SubSequenceType> firstSs = converted.getSubSequence().stream().filter(ss -> ss.getPosition().equals(new BigInteger("1"))).findFirst();
            firstSs.ifPresent(ss -> ss.setSubSeqMinOffset(intToBigIntOrNull(bs.getOffset())));
            firstSs.ifPresent(ss -> ss.setSubSeqMaxOffset(intToBigIntOrNull(bs.getMaxOffset())));

            return converted;
        } catch (JAXBException | XMLStreamException e) {
            logger.error("ERROR PARSING CONVERTED XML FOR BYTE SEQUENCE " + bs.getURI() + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String jaxbObjectToXML(JAXBElement<?> elem) {
        try {
            //Create JAXB Context
            JAXBContext jaxbContext = JAXBContext.newInstance(ByteSequenceType.class);

            //Create Marshaller
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            //Required formatting??
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            //Print XML String to Console
            StringWriter sw = new StringWriter();

            //Write XML to StringWriter
            jaxbMarshaller.marshal(elem, sw);


            return sw.toString();

        } catch (JAXBException e) {
            logger.error("ERROR CONVERTING GENERIC JAXB ELEMENT TO XML : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static BigInteger intToBigIntOrNull(Integer i) {
        if (i == null) return null;
        return new BigInteger(i.toString());
    }
}
