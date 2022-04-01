package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public interface RDFDeserializer<ToType> {
    public Resource getRDFType();

    public FileFormat fromModel(Resource uri, Model model);
}
