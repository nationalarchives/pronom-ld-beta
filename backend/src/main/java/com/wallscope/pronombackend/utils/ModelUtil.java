package com.wallscope.pronombackend.utils;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;

public class ModelUtil {
    Logger logger = LoggerFactory.getLogger(ModelUtil.class);
    private Model m;

    public ModelUtil(Model m) {
        this.m = m;
    }

    public RDFNode getOneObjectOrNull(Resource s, Property p) {
        try {
            return this.m.listObjectsOfProperty(s, p).nextNode();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public List<RDFNode> getAllObjects(Resource s, Property p) {
        return this.m.listObjectsOfProperty(s, p).toList();
    }
}
