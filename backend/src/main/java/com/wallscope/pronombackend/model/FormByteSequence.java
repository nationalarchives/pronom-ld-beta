package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.controller.EditorialController;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;
import static com.wallscope.pronombackend.utils.RDFUtil.safelyGetUriOrNull;

public class FormByteSequence {
    private final Logger logger = LoggerFactory.getLogger(FormByteSequence.class);
    private String uri;
    private String signature;
    private String position;
    private Integer offset;
    private String sequence;
    private String byteOrder;
    private Integer maxOffset;
    private Integer indirectOffsetLocation;
    private Integer indirectOffsetLength;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(String byteOrder) {
        this.byteOrder = byteOrder;
    }

    public Integer getMaxOffset() {
        return maxOffset;
    }

    public void setMaxOffset(Integer maxOffset) {
        this.maxOffset = maxOffset;
    }

    public Integer getIndirectOffsetLocation() {
        return indirectOffsetLocation;
    }

    public void setIndirectOffsetLocation(Integer indirectOffsetLocation) {
        this.indirectOffsetLocation = indirectOffsetLocation;
    }

    public Integer getIndirectOffsetLength() {
        return indirectOffsetLength;
    }

    public void setIndirectOffsetLength(Integer indirectOffsetLength) {
        this.indirectOffsetLength = indirectOffsetLength;
    }

    @Override
    public String toString() {
        return "FormByteSequence{" +
                "signature='" + signature + '\'' +
                ", position='" + position + '\'' +
                ", offset=" + offset +
                ", sequence='" + sequence + '\'' +
                ", byteOrder='" + byteOrder + '\'' +
                ", maxOffset=" + maxOffset +
                ", indirectOffsetLocation=" + indirectOffsetLocation +
                ", indirectOffsetLength=" + indirectOffsetLength +
                '}';
    }

    public static FormByteSequence convert(ByteSequence bs) {
        FormByteSequence fbs = new FormByteSequence();
        // signature is set at the parent
        fbs.setUri(safelyGetUriOrNull(bs.getURI()));
        fbs.setPosition(safelyGetUriOrNull(bs.getPosition()));
        fbs.setOffset(bs.getOffset());
        fbs.setSequence(bs.getSequence());
        fbs.setByteOrder(safelyGetUriOrNull(bs.getByteOrder()));
        fbs.setMaxOffset(bs.getMaxOffset());
        fbs.setIndirectOffsetLength(bs.getIndirectOffsetLength());
        fbs.setIndirectOffsetLocation(bs.getIndirectOffsetLocation());
        return fbs;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public ByteSequence toObject(Resource signature) {
        Resource uri = makeResource(getUri());
        return new ByteSequence(uri,
                signature,
                makeResource(position),
                null,
                offset,
                sequence,
                makeResource(byteOrder),
                maxOffset,
                indirectOffsetLocation,
                indirectOffsetLength);
    }

    public boolean isNotEmpty() {
        boolean val = uri != null && !uri.isBlank()
                && sequence != null && !sequence.isBlank();
        logger.debug("EMPTY CHECK BYTE SEQUENCE ("+val+"): "+ uri);
        return val;
    }
}
