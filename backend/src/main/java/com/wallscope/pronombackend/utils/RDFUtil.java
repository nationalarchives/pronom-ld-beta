package com.wallscope.pronombackend.utils;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

public class RDFUtil {
    static Logger logger = LoggerFactory.getLogger(RDFUtil.class);
    public static final String PREFIXES = """
            prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>
            prefix pr: <http://www.nationalarchives.gov.uk/PRONOM/>
            """;

    public static Resource makeResource(String r) {
        return ResourceFactory.createResource(r);
    }

    public static Property makeProp(String p) {
        return ResourceFactory.createProperty(p);
    }

    public static Literal makeLiteral(String l) {
        return ResourceFactory.createStringLiteral(l);
    }

    public static Literal makeLiteral(boolean l) {
        return makeLiteral(String.valueOf(l), XSDDatatype.XSDboolean);
    }

    public static Literal makeLiteral(int l) {
        return makeLiteral(String.valueOf(l), XSDDatatype.XSDinteger);
    }

    public static Literal makeLiteral(String l, RDFDatatype type) {
        return ResourceFactory.createTypedLiteral(l, type);
    }

    public static Instant parseDate(Literal lit) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
            TemporalAccessor t = formatter.parse(lit.getString());
            return Instant.from(t);
        } catch (DateTimeParseException e) {
            logger.error("failed to parse literal as date (" + lit.getString() + "), error: " + e.getMessage());
            return null;
        }
    }

    public static Literal makeXSDDateTime(Instant i) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
        return makeXSDDateTime(formatter.format(i));
    }

    public static Literal makeXSDDateTime(String s) {
        return makeLiteral(s, XSDDatatype.XSDdateTime);
    }

    public static String trimOptionals(String query) {
        return query.replaceAll("OPTIONAL\\s?\\{\\s?\\?", "?")
                .replaceAll("\\}#END OPTIONAL", "");
    }

    public static Resource safelyGetResourceOrNull(RDFNode n) {
        if (n == null) return null;
        return n.asResource();
    }

    public static Literal safelyGetLiteralOrNull(RDFNode n) {
        if (n == null) return null;
        return n.asLiteral();
    }

    public static String safelyGetStringOrNull(RDFNode n) {
        Literal lit = safelyGetLiteralOrNull(n);
        if (lit == null) return null;
        return lit.getString();
    }

    public static Boolean safelyGetBooleanOrNull(RDFNode n) {
        Literal lit = safelyGetLiteralOrNull(n);
        if (lit == null) return null;
        return lit.getBoolean();
    }

    public static Integer safelyGetIntegerOrNull(RDFNode n) {
        Literal lit = safelyGetLiteralOrNull(n);
        if (lit == null) return null;
        return lit.getInt();
    }

    public static class RDF {
        public static final String uri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
        public static final String type = uri + "type";
    }

    public static class RDFS {
        public static final String uri = "http://www.w3.org/2000/01/rdf-schema#";
        public static final String label = uri + "label";
        public static final String comment = uri + "comment";
    }

    public static class PRONOM {
        public static final String uri = "http://www.nationalarchives.gov.uk/PRONOM/";

        // FileFormat sub
        public static class FileFormat {
            public static final String type = PRONOM.uri + "FileFormat";
            public static final String uri = PRONOM.uri + "fileFormat.";
            public static final String LastUpdatedDate = uri + "LastUpdatedDate";
            public static final String Version = uri + "Version";
            public static final String BinaryFlag = uri + "BinaryFlag";
            public static final String WithdrawnFlag = uri + "WithdrawnFlag";
            public static final String Puid = uri + "Puid";
            public static final String PuidTypeId = uri + "PuidTypeId";
            public static final String Classification = uri + "Classification";
            public static final String InternalSignature = uri + "InternalSignature";
            public static final String ExternalSignature = uri + "ExternalSignature";
            public static final String InFileFormatRelationship = uri + "In.FileFormatRelationship";
        }

        // FileFormatRelationship
        public static class FileFormatRelationship {
            public static final String type = PRONOM.uri + "FileFormatRelationship";
            public static final String uri = PRONOM.uri + "fileFormatRelationship.";
            public static final String Source = uri + "Source";
            public static final String Target = uri + "Target";
            public static final String FileFormatRelationshipType = uri + "FileFormatRelationshipType";
            public static final String Note = uri + "Note";
        }

        public static class FormatRelationshipType {
            public static final String type = PRONOM.uri + "FormatRelationshipType";
            public static final String uri = PRONOM.uri + "formatRelationshipType.";
            public static final String TypeName = uri + "TypeName";
            public static final String InverseTypeName = uri + "InverseTypeName";
            // Instances
            public static final String PriorityOver = PRONOM.uri + "id/FileFormatRelationshipType/7";
        }

        // PuidType sub
        public static class PuidType {
            public static final String type = PRONOM.uri + "PuidType";
            public static final String uri = PRONOM.uri + "puidType.";
        }

        // InternalSignature sub
        public static class InternalSignature {
            public static final String type = PRONOM.uri + "InternalSignature";
            public static final String uri = PRONOM.uri + "internalSignature.";
            public static final String Note = uri + "Note";
            public static final String SourceId = uri + "SourceId";
            public static final String SourceDate = uri + "SourceDate";
            public static final String LastUpdatedDate = uri + "LastUpdatedDate";
            public static final String Provenance = uri + "Provenance";
            public static final String GenericFlag = uri + "GenericFlag";
            public static final String FileFormat = uri + "FileFormat";
            public static final String ByteSequence = uri + "ByteSequence";
        }

        // ExternalSignature sub
        public static class ExternalSignature {
            public static final String type = PRONOM.uri + "ExternalSignature";
            public static final String uri = PRONOM.uri + "externalSignature.";
            public static final String SignatureType = uri + "SignatureType";
        }

        // ByteSequence sub
        public static class ByteSequence {
            public static final String type = PRONOM.uri + "ByteSequence";
            public static final String uri = PRONOM.uri + "byteSequence.";
            public static final String ByteSequencePosition = uri + "ByteSequencePosition";
            public static final String Offset = uri + "Offset";
            public static final String ByteSequence = uri + "ByteSequence";
            public static final String InternalSignature = uri + "InternalSignature";
            public static final String ByteOrder = uri + "ByteOrder";
            public static final String MaxOffset = uri + "MaxOffset";
            public static final String IndirectOffsetLocation = uri + "IndirectOffsetLocation";
            public static final String IndirectOffsetLength = uri + "IndirectOffsetLength";
            // Byte Sequence Positions
            public static final String BSPuri = PRONOM.uri + "id/ByteSequencePosition/";
            public static final String AbsoluteFromBOF = BSPuri + "1";
            public static final String AbsoluteFromEOF = BSPuri + "2";
            public static final String Variable = BSPuri + "3";
            public static final String IndirectFromBOF = BSPuri + "4";
            public static final String IndirectFromEOF = BSPuri + "5";
        }

        // ByteOrder Instances
        public static class ByteOrder {
            public static final String uri = PRONOM.uri + "id/ByteOrder/";
            public static final String littleEndian = uri + "littleEndian";
            public static final String bigEndian = uri + "bigEndian";
        }

        // Actor sub
        public static class Actor {
            public static final String type = PRONOM.uri + "Actor";
            public static final String uri = PRONOM.uri + "actor.";
            public static final String JobTitle = uri + "JobTitle";
            public static final String OrganisationName = uri + "OrganisationName";
            public static final String TypeId = uri + "TypeId";
            public static final String Telephone = uri + "Telephone";
            public static final String Website = uri + "Website";
            public static final String Country = uri + "Country";
            public static final String Support = uri + "Support";
            public static final String Email = uri + "Email";
            public static final String SourceId = uri + "SourceId";
            public static final String SourceDate = uri + "SourceDate";
            public static final String LastUpdated = uri + "LastUpdated";
            public static final String Provenance = uri + "Provenance";
            public static final String Address = uri + "Address";
            public static final String Authors = uri + "Authors";
            public static final String Develops = uri + "Develops";
            public static final String Maintains = uri + "Maintains";
            public static final String Publishes = uri + "Publishes";
            public static final String Supports = uri + "Supports";
        }
    }

}
