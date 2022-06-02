package com.wallscope.pronombackend.soap;

import com.wallscope.pronombackend.model.ByteSequence;
import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.model.InternalSignature;
import uk.gov.nationalarchives.pronom.signaturefile.*;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.PRONOM;

public class Converter {
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
                    .map(is -> new JAXBElement<>(isID, BigInteger.class, new BigInteger(is.getID())))
                    .collect(Collectors.toList());
            children.addAll(isIDs);
            // Add Extension (External signatures) elements
            QName extID = new QName("Extension");
            List<JAXBElement<String>> exts = ff.getExternalSignatures().stream()
                    // get only the file extensions
                    .filter(es -> es.getSignatureType().equals(PRONOM.ExternalSignature.FileExtension))
                    .map(es -> new JAXBElement<>(extID, String.class, es.getName()))
                    .collect(Collectors.toList());
            children.addAll(exts);
            // Add PriorityOver elements
            QName pID = new QName("HasPriorityOverFileFormatID");
            List<JAXBElement<BigInteger>> pIDs = ff.getHasPriorityOver().stream()
                    .map(ps -> new JAXBElement<>(pID, BigInteger.class, new BigInteger(ps.getTargetID())))
                    .collect(Collectors.toList());
            children.addAll(pIDs);
            listRef.add(conv);
        });
        return collection;
    }

    public static SignatureFileType.InternalSignatureCollection convertInternalSignatureCollection(List<InternalSignature> iss) {
        SignatureFileType.InternalSignatureCollection collection = new SignatureFileType.InternalSignatureCollection();
        List<InternalSignatureType> listRef = collection.getInternalSignature();
        iss.forEach(is -> {
            InternalSignatureType conv = new InternalSignatureType();
            conv.setID(new BigInteger(is.getID()));
            conv.setSpecificity(is.getSpecificity());
            List<ByteSequenceType> bsList = conv.getByteSequence();
            bsList.addAll(convertByteSequenceList(is.getByteSequences()));
            listRef.add(conv);
        });
        return collection;
    }

    private static List<ByteSequenceType> convertByteSequenceList(List<ByteSequence> bss) {
        return bss.stream().map(bs -> {
            ByteSequenceType conv = new ByteSequenceType();
            conv.setEndianness(bs.getByteOrderName());
            conv.setReference(bs.getReference());
            if(bs.getIndirectOffsetLength() != null) conv.setIndirectOffsetLength(bs.getIndirectOffsetLength().toString());
            if(bs.getIndirectOffsetLocation() != null) conv.setIndirectOffsetLocation(bs.getIndirectOffsetLocation().toString());
            List<SubSequenceType> subSeqList = conv.getSubSequence();
            subSeqList.addAll(convertSubSequenceList(bs.getSubSequences()));
            return conv;
        }).collect(Collectors.toList());
    }

    private static List<SubSequenceType> convertSubSequenceList(List<ByteSequence.SubSequence> subs) {
        return subs.stream().map(ss -> {
            SubSequenceType conv = new SubSequenceType();
            if(ss.getPosition() != null) conv.setPosition(BigInteger.valueOf(ss.getPosition()));
            if(ss.getSubSeqMinOffset() != null) conv.setSubSeqMinOffset(BigInteger.valueOf(ss.getSubSeqMinOffset()));
            if(ss.getSubSeqMaxOffset() != null) conv.setSubSeqMaxOffset(BigInteger.valueOf(ss.getSubSeqMaxOffset()));
            if(ss.getMinFragLength() != null) conv.setMinFragLength(BigInteger.valueOf(ss.getMinFragLength()));
            if(ss.getSequence() != null) conv.setSequence(ss.getSequence());
            List<JAXBElement<?>> children = conv.getShiftOrLeftFragmentOrRightFragment();
            List<JAXBElement<FragmentType>> frags = ss.getFragments().stream().map(f -> {
                FragmentType frag = new FragmentType();
                frag.setPosition(BigInteger.valueOf(f.getPosition()));
                frag.setMinOffset(BigInteger.valueOf(f.getMinOffset()));
                frag.setMaxOffset(BigInteger.valueOf(f.getMaxOffset()));
                frag.setValue(f.getValue());
                QName tag = f.getType() == ByteSequence.FragmentType.LEFT ? new QName("LeftFragment") : new QName("RightFragment");
                return new JAXBElement<>(tag, FragmentType.class, frag);
            }).collect(Collectors.toList());
            children.addAll(frags);
            return conv;
        }).collect(Collectors.toList());
    }
}
