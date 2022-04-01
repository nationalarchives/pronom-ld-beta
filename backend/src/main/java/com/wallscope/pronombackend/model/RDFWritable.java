package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public interface RDFWritable {
    public Resource getURI();

    public Model toRDF();
}
