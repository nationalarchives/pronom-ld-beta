package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.PRONOM;
import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class FormFormatIdentifier {
    private String id;
    private String name;
    private String type;

    public FormFormatIdentifier() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static FormFormatIdentifier convert(FormatIdentifier fi) {
        FormFormatIdentifier ffi = new FormFormatIdentifier();
        ffi.setName(fi.getName());
        ffi.setType(fi.getType().getURI());
        ffi.setId(fi.getID());
        return ffi;
    }

    public FormatIdentifier toObject() {
        Resource uri = makeResource(PRONOM.FormatIdentifier.id + getId());
        return new FormatIdentifier(uri, getName(), makeResource(getType()), null);
    }
}
