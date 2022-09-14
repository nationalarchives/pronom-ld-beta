package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class InternalSignature implements RDFWritable, Comparable<InternalSignature> {
    private final Resource uri;
    private final String name;
    private final String note;
    private final Instant updated;
    private final Boolean genericFlag;
    private final String provenance;
    private final Resource fileFormat;
    private final List<ByteSequence> byteSequences;

    public InternalSignature(Resource uri, String name, String note, Instant updated, Boolean genericFlag, String provenance, Resource fileFormat, List<ByteSequence> byteSequences) {
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
        if (genericFlag == null) return "Generic";
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
        if (name != null) m.add(uri, makeProp(RDFS.label), makeLiteral(name));
        if (updated != null) m.add(uri, makeProp(PRONOM.InternalSignature.LastUpdatedDate), makeXSDDateTime(updated));
        if (note != null) m.add(uri, makeProp(PRONOM.InternalSignature.Note), makeLiteral(note));
        if (provenance != null) m.add(uri, makeProp(PRONOM.InternalSignature.Provenance), makeLiteral(provenance));
        if (genericFlag != null) m.add(uri, makeProp(PRONOM.InternalSignature.GenericFlag), makeLiteral(genericFlag));
        if (fileFormat != null) m.add(uri, makeProp(PRONOM.InternalSignature.FileFormat), fileFormat);
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

    @Override
    public int compareTo(InternalSignature b) {
        boolean aNull = this.getURI() == null;
        boolean bNull = b.getURI() == null;
        if (aNull && !bNull) {
            return -1;
        } else if (bNull && !aNull) {
            return 1;
        } else if (aNull && bNull) {
            return 0;
        }

        int aInt = NumberUtils.toInt(this.getURI().getLocalName(), Integer.MAX_VALUE);
        int bInt = NumberUtils.toInt(b.getURI().getLocalName(), Integer.MAX_VALUE);
        return aInt - bInt;
    }

    public InternalSignature replaceFileFormat(Resource newUri) {
        return new InternalSignature(uri, name, note, updated, genericFlag, provenance, newUri, byteSequences);
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
            String name = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(RDFS.label)));
            Instant updated = safelyParseDateOrNull(safelyGetLiteralOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.InternalSignature.LastUpdatedDate))));
            Boolean genericFlag = safelyGetBooleanOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.InternalSignature.GenericFlag)));
            String note = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.InternalSignature.Note)));
            String provenance = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.InternalSignature.Provenance)));
            Resource fileFormat = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.InternalSignature.FileFormat)));

            List<Resource> byteSeqSubjects = mu.getAllObjects(uri, makeProp(PRONOM.InternalSignature.ByteSequence)).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<ByteSequence> byteSequences = mu.buildFromModel(new ByteSequence.Deserializer(), byteSeqSubjects);

            return new InternalSignature(uri, name, note, updated, genericFlag, provenance, fileFormat, byteSequences);
        }
    }
}
