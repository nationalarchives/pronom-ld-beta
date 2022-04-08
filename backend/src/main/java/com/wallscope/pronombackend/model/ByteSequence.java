package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class ByteSequence implements RDFWritable {
    private final Resource uri;
    private final Resource signature;
    private final Resource position;
    private final String positionName;
    private final Integer offset;
    private final String sequence;
    private final Resource byteOrder;
    private final Integer maxOffset;
    private final Integer indirectOffsetLocation;
    private final Integer indirectOffsetLength;

    public ByteSequence(Resource uri, Resource signature, Resource position, String positionName, Integer offset, String sequence, Resource byteOrder, Integer maxOffset, Integer indirectOffsetLocation, Integer indirectOffsetLength) {
        this.uri = uri;
        this.signature = signature;
        this.position = position;
        this.positionName = positionName;
        this.offset = offset;
        this.sequence = sequence;
        this.byteOrder = byteOrder;
        this.maxOffset = maxOffset;
        this.indirectOffsetLocation = indirectOffsetLocation;
        this.indirectOffsetLength = indirectOffsetLength;
    }

    public Integer getOffset() {
        return offset;
    }

    public Resource getPosition() {
        return position;
    }

    public String getPositionName() {
        return positionName;
    }

    public Resource getSignature() {
        return signature;
    }

    public Resource getByteOrder() {
        return byteOrder;
    }

    public String getSequence() {
        return sequence;
    }

    public Integer getIndirectOffsetLength() {
        return indirectOffsetLength;
    }

    public Integer getIndirectOffsetLocation() {
        return indirectOffsetLocation;
    }

    public Integer getMaxOffset() {
        return maxOffset;
    }

    @Override
    public Resource getURI() {
        return uri;
    }

    @Override
    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, makeProp(RDF.type), makeResource(PRONOM.ByteSequence.type));
        m.add(uri, makeProp(PRONOM.ByteSequence.ByteSequencePosition), position);
        m.add(uri, makeProp(PRONOM.ByteSequence.Offset), makeLiteral(offset));
        m.add(uri, makeProp(PRONOM.ByteSequence.ByteSequence), makeLiteral(sequence));
        m.add(uri, makeProp(PRONOM.ByteSequence.MaxOffset), makeLiteral(maxOffset));
        m.add(uri, makeProp(PRONOM.ByteSequence.InternalSignature), signature);
        m.add(uri, makeProp(PRONOM.ByteSequence.ByteOrder), byteOrder);
        m.add(uri, makeProp(PRONOM.ByteSequence.IndirectOffsetLocation), makeLiteral(indirectOffsetLocation));
        m.add(uri, makeProp(PRONOM.ByteSequence.IndirectOffsetLength), makeLiteral(indirectOffsetLength));
        return m;
    }
}
