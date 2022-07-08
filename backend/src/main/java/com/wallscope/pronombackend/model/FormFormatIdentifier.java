package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.PRONOM;
import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class FormFormatIdentifier {
    private String uri;
    private String name;
    private String type;

    public FormFormatIdentifier() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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
        ffi.setUri(fi.getID());
        return ffi;
    }

    public FormatIdentifier toObject() {
        Resource uri = makeResource(PRONOM.FormatIdentifier.id + getUri());
        return new FormatIdentifier(uri, getName(), makeResource(getType()), null);
    }

    public boolean isNotEmpty() {
        return uri != null && !uri.isBlank()
                && name != null && !name.isBlank()
                && type != null && !type.isBlank();
    }
}
