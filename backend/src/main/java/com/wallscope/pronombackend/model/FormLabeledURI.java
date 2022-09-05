package com.wallscope.pronombackend.model;

import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class FormLabeledURI {
    private String uri;
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public LabeledURI toObject() {
        return new LabeledURI(makeResource(uri), label);
    }

    @Override
    public String toString() {
        return "FormLabeledURI{" +
                "uri='" + uri + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
