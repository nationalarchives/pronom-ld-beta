package com.wallscope.pronombackend.utils;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
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
        }

        // PuidType sub
        public static class PuidType {
            public static final String type = PRONOM.uri + "PuidType";
            public static final String uri = PRONOM.uri + "puidType.";
            public static String PuidType = uri + "PuidType";
        }
    }

}
