package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class ByteSequenceDeserializer implements RDFDeserializer<ByteSequence> {
    @Override
    public Resource getRDFType() {
        return makeResource(PRONOM.ByteSequence.type);
    }

    @Override
    public ByteSequence fromModel(Resource uri, Model model) {
        ModelUtil mu = new ModelUtil(model);
        // Required
        Resource signature = mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.InternalSignature)).asResource();
        String sequence = mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.ByteSequence)).asLiteral().getString();
        Resource position = mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.ByteSequencePosition)).asResource();
        String positionName = mu.getOneObjectOrNull(position, makeProp(RDFS.label)).asLiteral().getString();
        // Optionals
        Integer offset = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.Offset)));
        Resource byteOrder = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.ByteOrder)));
        Integer maxOffset = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.MaxOffset)));
        Integer indirectOffsetLocation = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.IndirectOffsetLocation)));
        Integer indirectOffsetLength = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.IndirectOffsetLocation)));

        return new ByteSequence(uri, signature, position, positionName, offset, sequence, byteOrder, maxOffset, indirectOffsetLocation, indirectOffsetLength);
    }
}
