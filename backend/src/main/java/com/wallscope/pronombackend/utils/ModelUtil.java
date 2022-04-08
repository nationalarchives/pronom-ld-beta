package com.wallscope.pronombackend.utils;

import com.wallscope.pronombackend.model.RDFDeserializer;
import com.wallscope.pronombackend.model.RDFWritable;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class ModelUtil {
    Logger logger = LoggerFactory.getLogger(ModelUtil.class);
    private Model m;

    public ModelUtil(Model m) {
        this.m = m;
    }

    public ModelUtil(String rdf, Lang lang) {
        Model m = ModelFactory.createDefaultModel();
        StringReader r = new StringReader(rdf);
        RDFDataMgr.read(m, r, null, lang);
        this.m = m;
    }

    public ModelUtil(String rdf) {
        this(rdf, Lang.TURTLE);
    }

    public RDFNode getOneObjectOrNull(Resource s, Property p) {
        try {
            return this.m.listObjectsOfProperty(s, p).nextNode();
        } catch (NoSuchElementException e) {
            logger.debug("getOneObjectOrNull: returning null for (" + s.getURI() + "," + p.getURI() + ")");
            return null;
        }
    }

    public Model getModel() {
        return m;
    }

    public List<RDFNode> getAllObjects(Resource s, Property p) {
        return this.m.listObjectsOfProperty(s, p).toList();
    }

    public List<Resource> getSubjects() {
        return this.m.listSubjects().toList();
    }

    public List<Resource> getSubjects(Resource type) {
        return this.m.listSubjectsWithProperty(RDFUtil.makeProp(RDFUtil.RDF.type), type).toList();
    }

    public List<Resource> getSubjects(Property prop, Resource type) {
        return this.m.listSubjectsWithProperty(prop, type).toList();
    }

    public Model extractModel(Resource s, Property p, RDFNode o) {
        List<Statement> stmts = m.listStatements(s, p, o).toList();
        Model newModel = ModelFactory.createDefaultModel();
        newModel.add(stmts);
        return newModel;
    }

    public <T extends RDFWritable> List<T> buildAllFromModel(RDFDeserializer<T> deserializer) {
        return this.getSubjects(RDFUtil.makeProp(RDFUtil.RDF.type), deserializer.getRDFType()).stream()
                .map((s) -> deserializer.fromModel(s, m)).collect(Collectors.toList());

    }

    @Override
    public String toString() {
        return toString(Lang.NT);
    }

    public String toString(Lang l) {
        StringWriter b = new StringWriter();
        RDFDataMgr.write(b, m, l);
        return b.toString();
    }
}
