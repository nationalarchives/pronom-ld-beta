package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public class Actor implements RDFWritable {
    private final Resource uri;

    public Actor(Resource uri) {
        this.uri = uri;
    }

    @Override
    public Resource getURI() {
        return uri;
    }

    @Override
    public Model toRDF() {
        return null;
    }
}
