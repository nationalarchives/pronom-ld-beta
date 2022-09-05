package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;
import static com.wallscope.pronombackend.utils.RDFUtil.safelyGetUriOrNull;

public class FormInternalSignature {
    private final Logger logger = LoggerFactory.getLogger(FormInternalSignature.class);
    private String uri;
    private String name;
    private String note;
    private Instant updated;
    private Boolean genericFlag;
    private String provenance;
    private String fileFormat;
    private ArrayList<FormByteSequence> byteSequences;

    public FormInternalSignature() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public Boolean getGenericFlag() {
        return genericFlag;
    }

    public void setGenericFlag(Boolean genericFlag) {
        this.genericFlag = genericFlag;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public ArrayList<FormByteSequence> getByteSequences() {
        return byteSequences;
    }

    public void setByteSequences(ArrayList<FormByteSequence> byteSequences) {
        this.byteSequences = byteSequences;
    }

    @Override
    public String toString() {
        return "FormInternalSignature{" +
                "name='" + name + '\'' +
                ", note='" + note + '\'' +
                ", updated=" + updated +
                ", genericFlag=" + genericFlag +
                ", provenance='" + provenance + '\'' +
                ", fileFormat='" + fileFormat + '\'' +
                ", byteSequences=" + byteSequences +
                '}';
    }

    public static FormInternalSignature convert(InternalSignature is) {
        FormInternalSignature fis = new FormInternalSignature();
        fis.setUri(safelyGetUriOrNull(is.getURI()));
        fis.setName(is.getName());
        fis.setNote(is.getNote());
        fis.setUpdated(is.getUpdated());
        fis.setGenericFlag(is.isGeneric());
        fis.setProvenance(is.getProvenance());
        // fileFormat field is set at the parent
        fis.setByteSequences(is.getByteSequences().stream().map(bs -> {
            FormByteSequence fbs = FormByteSequence.convert(bs);
            fbs.setSignature(is.getURI().getURI());
            return fbs;
        }).sorted(Comparator.comparing(FormByteSequence::getSequence)).collect(Collectors.toCollection(ArrayList::new)));
        return fis;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public InternalSignature toObject(Resource formatUri, Instant updated) {
        Resource uri = makeResource(getUri());
        return new InternalSignature(uri,
                getName(),
                getNote(),
                updated,
                getGenericFlag(),
                getProvenance(),
                formatUri,
                byteSequences.stream().map(bs -> bs.toObject(uri)).collect(Collectors.toList())
        );
    }

    public boolean isNotEmpty() {
        boolean val = uri != null && !uri.isBlank()
                && name != null && !name.isBlank()
                && byteSequences != null && byteSequences.stream().anyMatch(FormByteSequence::isNotEmpty);
        logger.debug("EMPTY CHECK INTERNAL SIGNATURE (" + val + "): " + uri);
        return val;

    }

    public void removeEmpties() {
        if (byteSequences != null)
            byteSequences = byteSequences.stream().filter(FormByteSequence::isNotEmpty).collect(Collectors.toCollection(ArrayList::new));
    }
}
