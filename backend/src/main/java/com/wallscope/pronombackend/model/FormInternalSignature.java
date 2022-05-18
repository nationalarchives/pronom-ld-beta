package com.wallscope.pronombackend.model;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class FormInternalSignature {
    private String uri;
    private String name;
    private String note;
    private Instant updated;
    private Boolean genericFlag;
    private String provenance;
    private String fileFormat;
    private String byteOrder;
    private List<FormByteSequence> byteSequences;

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

    public List<FormByteSequence> getByteSequences() {
        return byteSequences;
    }

    public void setByteSequences(List<FormByteSequence> byteSequences) {
        this.byteSequences = byteSequences;
    }

    public String getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(String byteOrder) {
        this.byteOrder = byteOrder;
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
                ", byteOrder='" + byteOrder + '\'' +
                ", byteSequences=" + byteSequences +
                '}';
    }

    public static FormInternalSignature convert(InternalSignature is) {
        FormInternalSignature fis = new FormInternalSignature();
        fis.setName(is.getName());
        fis.setNote(is.getNote());
        fis.setUpdated(is.getUpdated());
        fis.setGenericFlag(is.isGeneric());
        fis.setProvenance(is.getProvenance());
        // fileFormat field is set at the parent
        // TODO: add byteOrder in the RDF model
        fis.setByteSequences(is.getByteSequences().stream().map(bs -> {
            FormByteSequence fbs = FormByteSequence.convert(bs);
            fbs.setSignature(is.getID());
            return fbs;
        }).collect(Collectors.toList()));
        return fis;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
