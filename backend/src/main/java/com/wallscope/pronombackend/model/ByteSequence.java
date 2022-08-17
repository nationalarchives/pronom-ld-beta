package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import net.byteseek.compiler.CompileException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.nationalarchives.droid.core.signature.compiler.ByteSequenceAnchor;
import uk.gov.nationalarchives.droid.core.signature.compiler.ByteSequenceCompiler;
import uk.gov.nationalarchives.droid.core.signature.compiler.ByteSequenceSerializer;
import uk.gov.nationalarchives.droid.core.signature.compiler.SignatureType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class ByteSequence implements RDFWritable {
    Logger logger = LoggerFactory.getLogger(ByteSequence.class);
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

    public String getReference() {
        if (position == null) return null;
        return switch (position.getURI()) {
            case PRONOM.ByteSequence.AbsoluteFromBOF, PRONOM.ByteSequence.IndirectFromBOF -> "BOFoffset";
            case PRONOM.ByteSequence.AbsoluteFromEOF, PRONOM.ByteSequence.IndirectFromEOF -> "EOFoffset";
            case PRONOM.ByteSequence.Variable -> "Variable";
            default -> null;
        };
    }

    public boolean isBOFOffset() {
        if(position == null) return false;
        return switch (position.getURI()) {
            case PRONOM.ByteSequence.AbsoluteFromBOF, PRONOM.ByteSequence.IndirectFromBOF -> true;
            default -> false;
        };
    }

    public boolean isEOFOffset() {
        if(position == null) return false;
        return switch (position.getURI()) {
            case PRONOM.ByteSequence.AbsoluteFromEOF, PRONOM.ByteSequence.IndirectFromEOF -> true;
            default -> false;
        };
    }

    public String getID() {
        String[] parts = uri.getURI().split("/");
        return parts[parts.length - 1];
    }

    // In order to avoid clashes with existing byte sequences, the container byte sequence URIs
    // are based on the container signature ID and the byte sequence ID. Here we split it back into just the byte sequence
    // ID so it can be used in the XML template
    public String getContainerID() {
        String[] parts = uri.getURI().split("\\.");
        return parts[parts.length - 1];
    }

    public String getByteOrderName() {
        if (byteOrder == null) return null;
        return switch (byteOrder.getURI()) {
            case PRONOM.ByteOrder.littleEndian -> "Little-endian";
            case PRONOM.ByteOrder.bigEndian -> "Big-endian";
            default -> null;
        };
    }

    public String toXML(boolean isContainer) {
        try {
            ByteSequenceAnchor a = isEOFOffset() ? ByteSequenceAnchor.EOFOffset : ByteSequenceAnchor.BOFOffset;
            SignatureType sigType = isContainer ? SignatureType.CONTAINER : SignatureType.BINARY;
            return ByteSequenceSerializer.SERIALIZER.toXML(getSequence(), a, ByteSequenceCompiler.CompileType.PRONOM, sigType);
        } catch (CompileException e) {
            logger.error("ERROR COMPILING BYTE SEQUENCE ID " + getURI() + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    public Resource getURI() {
        return uri;
    }

    @Override
    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, makeProp(RDF.type), makeResource(PRONOM.ByteSequence.type));
        m.add(uri, makeProp(PRONOM.ByteSequence.InternalSignature), signature);
        if (position != null) m.add(uri, makeProp(PRONOM.ByteSequence.ByteSequencePosition), position);
        if (byteOrder != null) m.add(uri, makeProp(PRONOM.ByteSequence.ByteOrder), byteOrder);
        if (offset != null) m.add(uri, makeProp(PRONOM.ByteSequence.Offset), makeLiteral(offset));
        if (sequence != null) m.add(uri, makeProp(PRONOM.ByteSequence.ByteSequence), makeLiteral(sequence));
        if (maxOffset != null) m.add(uri, makeProp(PRONOM.ByteSequence.MaxOffset), makeLiteral(maxOffset));
        if (indirectOffsetLocation != null)
            m.add(uri, makeProp(PRONOM.ByteSequence.IndirectOffsetLocation), makeLiteral(indirectOffsetLocation));
        if (indirectOffsetLength != null)
            m.add(uri, makeProp(PRONOM.ByteSequence.IndirectOffsetLength), makeLiteral(indirectOffsetLength));
        return m;
    }

    @Override
    public String toString() {
        return "ByteSequence{" +
                "uri=" + uri +
                ", signature=" + signature +
                ", position=" + position +
                ", positionName='" + positionName + '\'' +
                ", offset=" + offset +
                ", sequence='" + sequence + '\'' +
                ", byteOrder=" + byteOrder +
                ", maxOffset=" + maxOffset +
                ", indirectOffsetLocation=" + indirectOffsetLocation +
                ", indirectOffsetLength=" + indirectOffsetLength +
                '}';
    }

    public static class Deserializer implements RDFDeserializer<ByteSequence> {
        @Override
        public Resource getRDFType() {
            return makeResource(PRONOM.ByteSequence.type);
        }

        @Override
        public ByteSequence fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            // Required (at least one)
            Resource signature = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.InternalSignature)));
            if (signature == null) {
                signature = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.ContainerFile)));
            }
            // Optionals
            String sequence = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.ByteSequence)));
            Resource position = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.ByteSequencePosition)));
            String positionName = null;
            if (position != null) {
                positionName = safelyGetStringOrNull(mu.getOneObjectOrNull(position, makeProp(RDFS.label)));
            }
            Integer offset = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.Offset)));
            Resource byteOrder = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.ByteOrder)));
            Integer maxOffset = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.MaxOffset)));
            Integer indirectOffsetLocation = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.IndirectOffsetLocation)));
            Integer indirectOffsetLength = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.IndirectOffsetLocation)));

            return new ByteSequence(uri, signature, position, positionName, offset, sequence, byteOrder, maxOffset, indirectOffsetLocation, indirectOffsetLength);
        }
    }
}
