package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public class ExternalSignature implements RDFWritable {
    private final Resource uri;
    private final String name;
    private final String signatureType;

    public ExternalSignature(Resource uri, String name, String signatureType) {
        this.uri = uri;
        this.name = name;
        this.signatureType = signatureType;
    }

    public String getName() {
        return name;
    }

    public String getSignatureType() {
        return signatureType;
    }

    @Override
    public Resource getURI() {
        return uri;
    }

    @Override
    public Model toRDF() {
        return null;
    }

    @Override
    public String toString() {
        return "ExternalSignature{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                ", signatureType='" + signatureType + '\'' +
                '}';
    }
}
