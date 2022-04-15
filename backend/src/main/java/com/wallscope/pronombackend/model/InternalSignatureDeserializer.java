package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class InternalSignatureDeserializer implements RDFDeserializer<InternalSignature> {
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
        List<ByteSequence> byteSequences = mu.buildFromModel(new ByteSequenceDeserializer(), byteSeqSubjects);
        return new InternalSignature(uri, name, note, updated, genericFlag, provenance, fileFormat, byteSequences);
    }
}
