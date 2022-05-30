package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    public String getID() {
        String[] parts = uri.getURI().split("/");
        return parts[parts.length - 1];
    }

    public String getSpecificity() {
        return genericFlag ? "Generic" : "Specific";
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

    @Override
    public String toString() {
        return "InternalSignature{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
                ", updated=" + updated +
                ", genericFlag=" + genericFlag +
                ", provenance='" + provenance + '\'' +
                ", fileFormat=" + fileFormat +
                ", byteSequences=" + byteSequences +
                '}';
    }

    public static class Deserializer implements RDFDeserializer<InternalSignature> {
        @Override
        public Resource getRDFType() {
            return makeResource(PRONOM.InternalSignature.type);
        }

        @Override
        public InternalSignature fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            // Required
            String name = mu.getOneObjectOrNull(uri, makeProp(RDFS.label)).asLiteral().getString();
            Literal updatedLit = mu.getOneObjectOrNull(uri, makeProp(PRONOM.InternalSignature.LastUpdatedDate)).asLiteral();
            Instant updated = parseDate(updatedLit);
            boolean genericFlag = mu.getOneObjectOrNull(uri, makeProp(PRONOM.InternalSignature.GenericFlag)).asLiteral().getBoolean();
            // Optional
            String note = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.InternalSignature.Note)));
            String provenance = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.InternalSignature.Provenance)));
            Resource fileFormat = mu.getOneObjectOrNull(uri, makeProp(PRONOM.InternalSignature.FileFormat)).asResource();
            List<Resource> byteSeqSubjects = mu.getAllObjects(uri, makeProp(PRONOM.InternalSignature.ByteSequence)).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<ByteSequence> byteSequences = mu.buildFromModel(new ByteSequence.Deserializer(), byteSeqSubjects)
                    .stream().sorted(Comparator.comparingInt(bs -> Integer.parseInt(bs.getID()))).collect(Collectors.toList());

            return new InternalSignature(uri, name, note, updated, genericFlag, provenance, fileFormat, byteSequences);
        }
    }
}
