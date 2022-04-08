package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public interface RDFDeserializer<ToType extends RDFWritable > {
    public Resource getRDFType();

    public ToType fromModel(Resource uri, Model model);
}
