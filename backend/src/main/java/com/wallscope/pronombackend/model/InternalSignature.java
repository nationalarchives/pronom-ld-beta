package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import java.time.Instant;
import java.util.List;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class InternalSignature implements RDFWritable {
    private final Resource uri;
    private final String name;
    private final String note;
    private final Instant updated;
    private final Boolean genericFlag;
    private final String provenance;
    private final Resource fileFormat;
    private final List<ByteSequence> byteSequences;

    public InternalSignature(Resource uri, String name, String note, Instant updated, boolean genericFlag, String provenance, Resource fileFormat, List<ByteSequence> byteSequences) {
        this.uri = uri;
        this.name = name;
        this.note = note;
        this.updated = updated;
        this.genericFlag = genericFlag;
        this.provenance = provenance;
        this.fileFormat = fileFormat;
        this.byteSequences = byteSequences;
    }

    public String getName() {
        return name;
    }

    public Instant getUpdated() {
        return updated;
    }

    public List<ByteSequence> getByteSequences() {
        return byteSequences;
    }

    public Resource getFileFormat() {
        return fileFormat;
    }

    public String getNote() {
        return note;
    }

    public String getProvenance() {
        return provenance;
    }

    public Boolean isGeneric() {
        return genericFlag;
    }

    @Override
    public Resource getURI() {
        return this.uri;
    }

    @Override
    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, makeProp(RDF.type), makeResource(PRONOM.InternalSignature.type));
        m.add(uri, makeProp(RDFS.label), makeLiteral(name));
        m.add(uri, makeProp(PRONOM.InternalSignature.Note), makeLiteral(note));
        m.add(uri, makeProp(PRONOM.InternalSignature.LastUpdatedDate), makeXSDDateTime(updated));
        m.add(uri, makeProp(PRONOM.InternalSignature.Provenance), makeLiteral(provenance));
        m.add(uri, makeProp(PRONOM.InternalSignature.GenericFlag), makeLiteral(genericFlag));
        m.add(uri, makeProp(PRONOM.InternalSignature.FileFormat), fileFormat);
        for (ByteSequence b : byteSequences) {
            m.add(uri, makeProp(PRONOM.InternalSignature.ByteSequence), b.getURI());
            m.add(b.toRDF());
        }

        return m;
    }
}
