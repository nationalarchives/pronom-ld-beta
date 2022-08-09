package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Resource;

public class LabeledURI {
    private final Resource uri;
    private final String label;


    public LabeledURI(Resource uri, String label) {
        this.uri = uri;
        this.label = label;
    }

    public Resource getUri() {
        return uri;
    }

    public String getLabel() {
        return label;
    }

    public FormLabeledURI convert() {
        FormLabeledURI flu = new FormLabeledURI();
        flu.setUri(uri.getURI());
        flu.setLabel(label);
        return flu;
    }
}
