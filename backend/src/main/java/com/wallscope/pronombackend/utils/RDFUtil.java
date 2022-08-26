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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RDFUtil {
    static Logger logger = LoggerFactory.getLogger(RDFUtil.class);
    public static final String PREFIXES = """
            prefix xsd: <http://www.w3.org/2001/XMLSchema#>
            prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>
            prefix skos: <http://www.w3.org/2004/02/skos/core#>
            prefix pr: <http://www.nationalarchives.gov.uk/PRONOM/>
            prefix ff: <http://www.nationalarchives.gov.uk/PRONOM/fileFormat.>
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

    public static Resource safelyGetResourceOrDefault(RDFNode n, Resource def) {
        if (n == null) return def;
        return n.asResource();
    }

    public static Literal safelyGetLiteralOrDefault(RDFNode n, Literal def) {
        if (n == null) return def;
        return n.asLiteral();
    }

    public static String safelyGetStringOrDefault(RDFNode n, String def) {
        Literal lit = safelyGetLiteralOrNull(n);
        if (lit == null) return def;
        return lit.getString();
    }

    public static Boolean safelyGetBooleanOrDefault(RDFNode n, boolean def) {
        Literal lit = safelyGetLiteralOrNull(n);
        if (lit == null) return def;
        return lit.getBoolean();
    }

    public static Integer safelyGetIntegerOrDefault(RDFNode n, int def) {
        Literal lit = safelyGetLiteralOrNull(n);
        if (lit == null) return def;
        return lit.getInt();
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

    public static String safelyGetUriOrNull(Resource r) {
        if (r == null) return null;
        return r.getURI();
    }

    public static String safelyGetUriOrNull(RDFNode r) {
        if (!r.isURIResource()) return null;
        return safelyGetUriOrNull(r.asResource());
    }

    public static Instant safelyParseDateOrNull(RDFNode n) {
        Literal lit = safelyGetLiteralOrNull(n);
        if (lit == null) return null;
        return parseDate(lit);
    }

    public static String createValuesString(List<String> rs) {
        return rs.stream().map(r -> "(<" + r + ">)").collect(Collectors.joining(" "));
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

    public static final class SKOS {
        public static final String uri = "http://www.w3.org/2004/02/skos/core#";
        public static final String notation = uri + "notation";
        public static final String hiddenLabel = uri + "hiddenLabel";
    }

    public static class PRONOM {
        public static final String uri = "http://www.nationalarchives.gov.uk/PRONOM/";
        public static final String id = uri + "id/";

        // Global sub
        public static class Global {
            public static final String uri = PRONOM.uri + "global.";
            public static final String Puid = uri + "Puid";
            public static final String PuidTypeId = uri + "PuidTypeId";
        }

        // SearchResult sub
        public static class SearchResult {
            public static final String type = PRONOM.uri + "SearchResult";
            public static final String uri = PRONOM.uri + "search.";
            public static final String Score = uri + "Score";
            public static final String Match = uri + "Match";
            public static final String Field = uri + "Field";
        }

        // FileFormat sub
        public static class FileFormat {
            public static final String type = PRONOM.uri + "FileFormat";
            public static final String uri = PRONOM.uri + "fileFormat.";
            public static final String id = PRONOM.uri + "id/FileFormat/";
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
            public static final String ReleaseDate = uri + "ReleaseDate";
            public static final String WithdrawnDate = uri + "WithdrawnDate";
            public static final String ContainerSignature = uri + "ContainerSignature";
            public static final String ByteOrder = uri + "ByteOrder";
            public static final String Documentation = uri + "Documentation";
            public static final String DevelopedBy = uri + "DevelopedBy.Actor";
            public static final String SupportedBy = uri + "SupportedBy.Actor";
            public static final String FormatFamily = uri + "FormatFamily";
            public static final String CompressionType = uri + "CompressionType";
            public static final String Alias = uri + "Alias";
        }

        public static class FormatAlias {
            public static final String type = PRONOM.uri + "FormatAlias";
            public static final String uri = PRONOM.uri + "formatAlias.";
            public static final String id = PRONOM.uri + "id/FormatAlias/";
            public static final String Version = uri + "Version";
        }

        public static class Documentation {
            public static final String type = PRONOM.uri + "Documentation";
            public static final String uri = PRONOM.uri + "documentation.";
            public static final String id = PRONOM.uri + "id/Documentation/";
            public static final String Author = uri + "Author";
            public static final String PublicationDate = uri + "PublicationDate";
            public static final String Identifiers = uri + "Identifiers";
            public static final String DocumentType = uri + "DocumentType";
            public static final String Note = uri + "Note";
        }

        // TentativeFileFormat sub
        public static class TentativeFileFormat {
            public static final String type = PRONOM.uri + "TentativeFileFormat";
            public static final String uri = PRONOM.uri + "tentativeFileFormat.";
            public static final String id = PRONOM.uri + "id/TentativeFileFormat/";
        }

        // Submitter sub
        public static class Contributor {
            public static final String type = PRONOM.uri + "Contributor";
            public static final String uri = PRONOM.uri + "contributor.";
            public static final String id = PRONOM.uri + "id/Contributor/";
            public static final String Name = uri + "Name";
            public static final String Organisation = uri + "Organisation";
            public static final String Email = uri + "Email";
            public static final String Country = uri + "Country";
            public static final String Comment = uri + "Comment";
            public static final String Anonymous = uri + "Anonymous";
            public static final String Internal = uri + "Internal";
        }

        public static class Submission {
            public static final String type = PRONOM.uri + "Submission";
            public static final String uri = PRONOM.uri + "submission.";
            public static final String id = PRONOM.uri + "id/Submission/";
            public static final String Reviewer = uri + "Reviewer";
            public static final String Updated = uri + "Updated";
            public static final String Created = uri + "Created";
            public static final String Source = uri + "Source";
            public static final String FileFormat = uri + "FileFormat";
            public static final String Contributor = uri + "Contributor";
            public static final String SubmissionType = uri + "SubmissionType";
            private static final String typeId = PRONOM.uri + "id/SubmissionType/";
            public static final String UserSubmission = typeId + "UserSubmission";
            public static final String InternalSubmission = typeId + "InternalSubmission";
            public static final String SubmissionStatus = uri + "SubmissionStatus";
            private static final String statusId = PRONOM.uri + "id/SubmissionStatus/";
            public static final String StatusWaiting = statusId + "Waiting";
            public static final String StatusNextRelease = statusId + "NextRelease";
            public static final String StatusWIP = statusId + "WIP";
            public static final String StatusTesting = statusId + "Testing";
            public static final String StatusReady = statusId + "Ready";
        }

        public static class Classification {
            public static final String type = PRONOM.uri + "Classification";
            public static final String uri = PRONOM.uri + "classification.";
            public static final String id = PRONOM.uri + "id/Classification/";
        }

        // FileFormatRelationship
        public static class FileFormatRelationship {
            public static final String type = PRONOM.uri + "FileFormatRelationship";
            public static final String uri = PRONOM.uri + "fileFormatRelationship.";
            public static final String id = PRONOM.uri + "id/FileFormatRelationship/";
            public static final String Source = uri + "Source";
            public static final String Target = uri + "Target";
            public static final String FileFormatRelationshipType = uri + "FileFormatRelationshipType";
            public static final String Note = uri + "Note";
            public static final String Version = uri + "Version";
        }

        public static class FormatRelationshipType {
            public static final String type = PRONOM.uri + "FileFormatRelationshipType";
            public static final String uri = PRONOM.uri + "formatRelationshipType.";
            public static final String id = PRONOM.uri + "id/FileFormatRelationshipType/";
            public static final String TypeName = uri + "TypeName";
            public static final String InverseTypeName = uri + "InverseTypeName";
            // Instances
            public static final String PriorityOver = id + "7";
        }

        public static class FileFormatFamily {
            public static final String type = PRONOM.uri + "FileFormatFamily";
            public static final String uri = PRONOM.uri + "fileFormatFamily.";
            public static final String id = PRONOM.uri + "id/FileFormatFamily/";
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
            public static final String id = PRONOM.uri + "id/InternalSignature/";
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
            public static final String id = PRONOM.uri + "id/ExternalSignature/";
            public static final String SignatureType = uri + "SignatureType";
            public static final String FileFormat = uri + "FileFormat";
        }

        public static class ContainerSignature {
            public static final String type = PRONOM.uri + "ContainerSignature";
            public static final String uri = PRONOM.uri + "containerSignature.";
            public static final String id = PRONOM.uri + "id/ContainerSignature/";
            public static final String FileFormat = uri + "FileFormat";
            public static final String ContainerFile = uri + "ContainerFile";
            public static final String ContainerType = uri + "ContainerType";
        }

        public static class ContainerType {
            public static final String type = PRONOM.uri + "ContainerType";
            public static final String uri = PRONOM.uri + "containerType.";
            public static final String id = PRONOM.uri + "id/ContainerType/";
            public static final String FileFormat = uri + "FileFormat";
            // Instances
            public static final String ZIP = id + "ZIP";
            public static final String OLE2 = id + "OLE2";
        }

        public static class ContainerFile {
            public static final String type = PRONOM.uri + "ContainerFile";
            public static final String uri = PRONOM.uri + "containerFile.";
            public static final String id = PRONOM.uri + "id/ContainerFile/";
            public static final String ByteSequence = uri + "ByteSequence";
            public static final String FilePath = uri + "FilePath";
            public static final String ContainerSignature = uri + "ContainerSignature";
        }

        // ByteSequence sub
        public static class ByteSequence {
            public static final String type = PRONOM.uri + "ByteSequence";
            public static final String uri = PRONOM.uri + "byteSequence.";
            public static final String id = PRONOM.uri + "id/ByteSequence/";
            public static final String ByteSequencePosition = uri + "ByteSequencePosition";
            public static final String Offset = uri + "Offset";
            public static final String ByteSequence = uri + "ByteSequence";
            public static final String ByteOrder = uri + "ByteOrder";
            public static final String MaxOffset = uri + "MaxOffset";
            public static final String IndirectOffsetLocation = uri + "IndirectOffsetLocation";
            public static final String IndirectOffsetLength = uri + "IndirectOffsetLength";
            // these two types can contain byte signatures
            public static final String InternalSignature = uri + "InternalSignature";
            public static final String ContainerFile = uri + "ContainerFile";
            // Byte Sequence Positions
            public static final String BSPType = PRONOM.uri + "PositionType";
            public static final String BSPid = PRONOM.uri + "id/ByteSequencePosition/";
            public static final String AbsoluteFromBOF = BSPid + "1";
            public static final String AbsoluteFromEOF = BSPid + "2";
            public static final String Variable = BSPid + "3";
            public static final String IndirectFromBOF = BSPid + "4";
            public static final String IndirectFromEOF = BSPid + "5";
        }

        // ByteOrder Instances
        public static class ByteOrder {
            public static final String id = PRONOM.uri + "id/ByteOrder/";
            public static final String type = PRONOM.uri + "ByteOrder";
            public static final String littleEndian = id + "littleEndian";
            public static final String bigEndian = id + "bigEndian";
        }

        public static class CompressionType {
            public static final String id = PRONOM.uri + "id/CompressionType/";
            public static final String type = PRONOM.uri + "CompressionType";
            public static final String uri = PRONOM.uri + "compressionType.";
            public static final String ReleaseDate = uri + "ReleaseDate";
            public static final String Lossiness = uri + "Lossiness";
        }

        // FormatIdentifier
        public static class FormatIdentifier {
            public static final String id = PRONOM.uri + "id/FormatIdentifier/";
            public static final String type = PRONOM.uri + "FormatIdentifier";
            public static final String uri = PRONOM.uri + "formatIdentifier.";
            public static final String FileFormat = uri + "FileFormat";
            public static final String FormatIdentifierType = uri + "FormatIdentifierType";
        }

        // FormatIdentifierType
        public static class FormatIdentifierType {
            public static final String id = PRONOM.uri + "id/FormatIdentifierType/";
            public static final String type = PRONOM.uri + "FormatIdentifierType";
            public static final String uri = PRONOM.uri + "formatIdentifierType.";
            public static final String MIME = id + "3";
        }

        // Actor sub
        public static class Actor {
            public static final String id = PRONOM.uri + "id/Actor/";
            public static final String type = PRONOM.uri + "Actor";
            public static final String ActorContributorType = PRONOM.uri + "ActorContributor";
            public static final String uri = PRONOM.uri + "actor.";
            public static final String OrganisationName = uri + "OrganisationName";
            public static final String Email = uri + "Email";
            public static final String Website = uri + "Website";
            public static final String JobTitle = uri + "JobTitle";
            public static final String Type = uri + "Type";
            public static final String Telephone = uri + "Telephone";
            public static final String Country = uri + "Country";
            public static final String CountryId = uri + "CountryId";
            public static final String Support = uri + "Support";
            public static final String SourceId = uri + "SourceId";
            public static final String SourceDate = uri + "SourceDate";
            public static final String LastUpdated = uri + "LastUpdated";
            public static final String Provenance = uri + "Provenance";
            public static final String Address = uri + "Address";
            public static final String DevelopsFileFormat = uri + "Develops.FileFormat";
            public static final String SupportsFileFormat = uri + "Supports.FileFormat";
            public static final String SupportWebsite = uri + "Support.Website";
            public static final String AuthorsDocument = uri + "Authors.Document";
            public static final String DevelopsCompressionType = uri + "Develops.CompressionType";
            public static final String MaintainsSoftware = uri + "Maintains.Software";
            public static final String PublishesDocument = uri + "Publishes.Document";
            public static final String DevelopsSoftware = uri + "Develops.Software";
        }

        // Software sub
        public static class Software {
            public static final String type = PRONOM.uri + "Software";
            public static final String uri = PRONOM.uri + "software.";
            public static final String id = PRONOM.uri + "id/Software/";
            public static final String LastUpdatedDate = uri + "LastUpdatedDate";
            public static final String DevelopmentActor = uri + "Development.Actor";
            public static final String Version = uri + "Version";
            public static final String PuidTypeId = uri + "PuidTypeId";
            public static final String ReleaseDate = uri + "ReleaseDate";
            public static final String MaintenanceActor = uri + "Maintenance.Actor";
            public static final String Puid = uri + "Puid";
            public static final String SourceId = uri + "SourceId";
            public static final String SourceDate = uri + "SourceDate";
        }

        // Character Encoding
        public static class CharacterEncoding {
            public static final String type = PRONOM.uri + "CharacterEncoding";
            public static final String uri = PRONOM.uri + "characterEncoding.";
            public static final String id = PRONOM.uri + "id/CharacterEncoding/";
        }

        // Classes only defined to reuse their RDF type
        public static class ActorType {
            public static final String type = PRONOM.uri + "ActorType";
            public static final String uri = PRONOM.uri + "actorType.";
            public static final String ActorType = uri + "ActorType";
        }

        public static class Country {
            public static final String type = PRONOM.uri + "Country";
        }
    }

    public static final Map<String, String> labelMap = Map.<String, String>ofEntries(
            // Byte orders
            Map.entry(PRONOM.ByteOrder.littleEndian, "Little-Endian"),
            Map.entry(PRONOM.ByteOrder.bigEndian, "Big-Endian"),
            // Submissions status
            Map.entry(PRONOM.Submission.StatusNextRelease, "Next Release"),
            Map.entry(PRONOM.Submission.StatusReady, "Ready for Release"),
            Map.entry(PRONOM.Submission.StatusWaiting, "Waiting"),
            Map.entry(PRONOM.Submission.StatusWIP, "Work in progress"),
            Map.entry(PRONOM.Submission.StatusTesting, "Testing"),
            // Submission types
            Map.entry(PRONOM.Submission.InternalSubmission, "Internal Submission"),
            Map.entry(PRONOM.Submission.UserSubmission, "User Submission"),
            // Search result types
            Map.entry(PRONOM.FileFormat.type, "File Format"),
            Map.entry(PRONOM.CompressionType.type, "Compression"),
            Map.entry(PRONOM.Software.type, "Software"),
            Map.entry(PRONOM.CharacterEncoding.type, "Character Encoding"),
            // Generic entity Properties
            // non-specific
            Map.entry(RDF.type, "Type"),
            Map.entry(RDFS.comment, "Description"),
            Map.entry(RDFS.label, "Name"),
            Map.entry(SKOS.notation, "Main Identifier"),
            // PRONOM specific: Global
            Map.entry(PRONOM.Global.Puid, "PUID"),
            Map.entry(PRONOM.Global.PuidTypeId, "PUID Type"),
            // PRONOM specific: Software
            Map.entry(PRONOM.Software.LastUpdatedDate, "Last updated"),
            Map.entry(PRONOM.Software.DevelopmentActor, "Developed by"),
            Map.entry(PRONOM.Software.Version, "Version"),
            Map.entry(PRONOM.Software.PuidTypeId, "PUID Type"),
            Map.entry(PRONOM.Software.ReleaseDate, "Released"),
            Map.entry(PRONOM.Software.MaintenanceActor, "Maintained by"),
            Map.entry(PRONOM.Software.Puid, "PUID"),
            Map.entry(PRONOM.Software.SourceId, "Source"),
            Map.entry(PRONOM.Software.SourceDate, "Source date"),
            // PRONOM specific: Actor
            Map.entry(PRONOM.Actor.type, "Vendor"),
            Map.entry(PRONOM.ActorType.type, "Type of actor"),
            Map.entry(PRONOM.ActorType.ActorType, "Type of actor"),
            Map.entry(PRONOM.Actor.OrganisationName, "Organisational Name"),
            Map.entry(PRONOM.Actor.Email, "Email"),
            Map.entry(PRONOM.Actor.Website, "Website"),
            Map.entry(PRONOM.Actor.JobTitle, "Job title"),
            Map.entry(PRONOM.Actor.Type, "Type"),
            Map.entry(PRONOM.Actor.Telephone, "Telephone"),
            Map.entry(PRONOM.Actor.Country, "Country"),
            Map.entry(PRONOM.Actor.CountryId, "Country Identifier"),
            Map.entry(PRONOM.Actor.Support, "Supports"),
            Map.entry(PRONOM.Actor.SourceId, "Source"),
            Map.entry(PRONOM.Actor.SourceDate, "Source date"),
            Map.entry(PRONOM.Actor.LastUpdated, "Last updated"),
            Map.entry(PRONOM.Actor.Provenance, "Provenance"),
            Map.entry(PRONOM.Actor.Address, "Address"),
            Map.entry(PRONOM.Actor.DevelopsFileFormat, "Developer of File Format(s)"),
            Map.entry(PRONOM.Actor.SupportsFileFormat, "Supporter of File Format(s)"),
            Map.entry(PRONOM.Actor.SupportWebsite, "Support Website"),
            Map.entry(PRONOM.Actor.AuthorsDocument, "Author of Document"),
            Map.entry(PRONOM.Actor.DevelopsCompressionType, "Developer of Compression Type"),
            Map.entry(PRONOM.Actor.DevelopsSoftware, "Developer of Software"),
            Map.entry(PRONOM.Actor.MaintainsSoftware, "Maintainer of Software"),
            Map.entry(PRONOM.Actor.PublishesDocument, "Publisher of Document")
    );

    public static final String WITH_STATEMENT = "\nWITH<" + PRONOM.uri + ">\n";

}
