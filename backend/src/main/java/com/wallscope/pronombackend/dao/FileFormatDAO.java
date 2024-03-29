package com.wallscope.pronombackend.dao;

import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.model.Submission;
import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.TriplestoreUtil;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.dao.ContainerSignatureDAO.FORM_CONTAINER_SIG_SUB_QUERY;
import static com.wallscope.pronombackend.dao.InternalSignatureDAO.FORM_INTERNAL_SIG_SUB_QUERY;
import static com.wallscope.pronombackend.dao.InternalSignatureDAO.INTERNAL_SIG_SUB_QUERY;
import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class FileFormatDAO {
    Logger logger = LoggerFactory.getLogger(FileFormatDAO.class);
    public static final String EXTERNAL_SIGNATURE_SUB_QUERY = "?extSig a pr:ExternalSignature ; pr:externalSignature.FileFormat ?f ; rdfs:label ?extSigName ; pr:externalSignature.SignatureType ?extSigType .";
    public static final String DOCUMENTATION_SUB_QUERY = """
            ?f pr:fileFormat.Documentation ?fDocumentation .
            ?fDocumentation a pr:Documentation ;
              rdfs:label ?fDocumentationLabel ;
              .
            OPTIONAL{ ?fDocumentation pr:documentation.Author ?fDocumentationAuthor .
              OPTIONAL { ?fDocumentationAuthor rdfs:label ?fDocumentationAuthorLabel . }#END OPTIONAL
              OPTIONAL { ?fDocumentationAuthor pr:actor.OrganisationName ?fDocumentationAuthorOrgName . }#END OPTIONAL
            }#END OPTIONAL
            OPTIONAL{ ?fDocumentation pr:documentation.Identifiers ?fDocumentationIdentifiers . }#END OPTIONAL
            OPTIONAL{ ?fDocumentation pr:documentation.PublicationDate ?fDocumentationDate . }#END OPTIONAL
            OPTIONAL{ ?fDocumentation pr:documentation.DocumentType ?fDocumentationType . }#END OPTIONAL
            OPTIONAL{ ?fDocumentation pr:documentation.Note ?fDocumentationNote . }#END OPTIONAL
            """;

    public static final String COMPRESSION_TYPES_SUB_QUERY = """
            ?f pr:fileFormat.CompressionType ?fCompressionType .
                        
            ?fCompressionType a pr:CompressionType ;
              rdfs:label ?fCompressionTypeLabel ;
              .
            OPTIONAL{ ?fCompressionType pr:compressionType.Lossiness ?fCompressionTypeLossiness .
              OPTIONAL { ?fCompressionTypeLossiness rdfs:label ?fCompressionTypeLossinessLabel . }#END OPTIONAL
            }#END OPTIONAL
                        
            OPTIONAL{ ?fCompressionType rdfs:comment ?fCompressionTypeDescription . }#END OPTIONAL
            OPTIONAL{ ?fCompressionType pr:compressionType.ReleaseDate ?fCompressionTypeReleaseDate . }#END OPTIONAL
            """;
    public static final String FORMAT_IDENTIFIER_SUB_QUERY = """
            ?fId a pr:FormatIdentifier ; pr:formatIdentifier.FileFormat ?f .
            ?fId rdfs:label ?fIdName ; pr:formatIdentifier.FormatIdentifierType ?fIdType .
            ?fIdType rdfs:label ?fIdTypeName .
            """;
    // This sub-query is used to gather file-format related data in non-file format
    public static final String MINIMAL_FILE_FORMAT_SUB_QUERY = """
                ?f a pr:FileFormat ;
                    rdfs:label ?fLabel ;
                    ff:Puid ?fPuid ;
                    ff:PuidTypeId ?fPuidType ;
                    ff:LastUpdatedDate ?fUpdated .
                ?f ff:PuidTypeId/rdfs:label ?puidTypeName .
                OPTIONAL { ?f pr:fileFormat.Version ?version . }#END OPTIONAL
                
            """;
    public static final String BYTE_SEQUENCE_SUB_QUERY = ByteSequenceDAO.BYTE_SEQUENCE_SUB_QUERY
            .replaceAll("\\?byteSeq pr:byteSequence\\.ContainerFile \\?contSigFile \\.", "");
    public static final String FORMAT_RELATIONSHIPS_SUB_QUERY = """
            ?fRel pr:fileFormatRelationship.FileFormatRelationshipType ?fRelType ;
              pr:fileFormatRelationship.Source ?fRelSource ;
              pr:fileFormatRelationship.Target ?fRelTarget ;
              .
              OPTIONAL { ?fRel pr:fileFormatRelationship.Note ?fRelNote . }#END OPTIONAL
              OPTIONAL { ?fRel pr:fileFormatRelationship.Version ?fRelVersion . }#END OPTIONAL
              
              ?fRelSource rdfs:label ?fRelSourceName .
              ?fRelTarget rdfs:label ?fRelTargetName .
             
            ?fRelType pr:formatRelationshipType.TypeName ?fRelTypeName ;
              pr:formatRelationshipType.InverseTypeName ?fRelInverseTypeName .
            """;
    public static final String FILE_FORMAT_SUB_QUERY = """
            ?f a pr:FileFormat .
            ?f rdfs:label ?label .
            ?f rdfs:comment ?comment .
            ?f pr:fileFormat.Puid ?puid .
            ?f pr:fileFormat.PuidTypeId ?puidType .
            ?f pr:fileFormat.LastUpdatedDate ?updated .
                        
            # Links
            ?puidType rdfs:label ?puidTypeName .
                        
            # Non-required fields
            OPTIONAL { ?f pr:fileFormat.Classification ?classification .
               ?classification rdfs:label ?classificationName .
            }#END OPTIONAL
            OPTIONAL { ?f pr:fileFormat.ByteOrder ?byteOrder .
               ?byteOrder rdfs:label ?byteOrderName .
            }#END OPTIONAL
            OPTIONAL { ?f pr:fileFormat.Version ?version . }#END OPTIONAL
            OPTIONAL { ?f pr:fileFormat.BinaryFlag ?binaryFlag . }#END OPTIONAL
            OPTIONAL { ?f pr:fileFormat.WithdrawnFlag ?withdrawn . }#END OPTIONAL
            OPTIONAL { ?f pr:fileFormat.ReleaseDate ?releaseDate . }#END OPTIONAL
            OPTIONAL { ?f pr:fileFormat.WithdrawnDate ?withdrawnDate . }#END OPTIONAL

            # Compression Types
            OPTIONAL{
               """ + COMPRESSION_TYPES_SUB_QUERY + """
            }#END OPTIONAL
            # Format Identifiers
            OPTIONAL{
               """ + FORMAT_IDENTIFIER_SUB_QUERY + """
            }#END OPTIONAL
            # Documentation
            OPTIONAL{
               """ + DOCUMENTATION_SUB_QUERY + """
            }#END OPTIONAL
            # External Signatures
            OPTIONAL {
                """ + EXTERNAL_SIGNATURE_SUB_QUERY + """
            }#END OPTIONAL
            # Internal Signatures
            OPTIONAL { ?f ff:InternalSignature ?sig .
               """ + FORM_INTERNAL_SIG_SUB_QUERY + """
            }#END OPTIONAL
                             
            # Container Signatures
            OPTIONAL { ?f ff:ContainerSignature ?contSig .
               """ + FORM_CONTAINER_SIG_SUB_QUERY + """
            }#END OPTIONAL
                        
            # Development Actors
            OPTIONAL { ?f pr:fileFormat.DevelopedBy.Actor ?fDevActor .
            """ + ActorDAO.ACTOR_SUB_QUERY.replaceAll("\\?act", "?fDevActor") + """
            }#END OPTIONAL
                        
            # Support Actors
            OPTIONAL { ?f pr:fileFormat.SupportedBy.Actor ?fSupportActor .
            """ + ActorDAO.ACTOR_SUB_QUERY.replaceAll("\\?act", "?fSupportActor") + """
            }#END OPTIONAL
                        
            # Format Relationships
            OPTIONAL { ?f pr:fileFormat.In.FileFormatRelationship ?fRel .
               """ + FORMAT_RELATIONSHIPS_SUB_QUERY + """
            }#END OPTIONAL
                        
            # Format Families
            OPTIONAL { ?f pr:fileFormat.FormatFamily ?fFamily .
                ?fFamily rdfs:label ?fFamilyName .
            }#END OPTIONAL
                        
            # Format Aliases
            OPTIONAL { ?f pr:fileFormat.Alias ?fAlias .
                ?fAlias rdfs:label ?fAliasName ; pr:formatAlias.Version ?fAliasVersion .
            }#END OPTIONAL
            """;
    public static final String FILE_FORMAT_QUERY = PREFIXES + """
            CONSTRUCT {
            """ + trimOptionals(FILE_FORMAT_SUB_QUERY) + """ 
             } WHERE {
            """ + FILE_FORMAT_SUB_QUERY + """
             }
            """;

    public static String MULTIPLE_FILE_FORMAT_QUERY(Collection<Resource> uris) {
        String formats = uris.stream().map(ex -> "(<" + ex + ">)").collect(Collectors.joining(" "));
        return PREFIXES + """
                CONSTRUCT {
                """ + trimOptionals(FILE_FORMAT_SUB_QUERY) + """ 
                } WHERE {
                VALUES (?f) {
                 """ + formats + """
                 }
                """ + FILE_FORMAT_SUB_QUERY + """
                 }
                """;
    }

    public static final String SIG_GEN_QUERY = PREFIXES + """
            CONSTRUCT {
            # File format representation
            """ + trimOptionals(MINIMAL_FILE_FORMAT_SUB_QUERY)
            .replaceAll("\\?f ff:PuidTypeId/rdfs:label \\?puidTypeName \\.", "?fPuidType rdfs:label ?puidTypeName .") + """
            # Binary signatures
              """ + trimOptionals(INTERNAL_SIG_SUB_QUERY) + """
            # Link File Format
            ?f pr:fileFormat.InternalSignature ?sig .
            # Byte Sequences
            """ + trimOptionals(BYTE_SEQUENCE_SUB_QUERY) + """
            # Format Identifiers
            ?f pr:fileFormat.In.FileFormatRelationship ?fRel .
            """ + trimOptionals(FORMAT_RELATIONSHIPS_SUB_QUERY) + """
                        
            """ + trimOptionals(EXTERNAL_SIGNATURE_SUB_QUERY) + """
                        
            """ + trimOptionals(FORMAT_IDENTIFIER_SUB_QUERY) + """
            } WHERE {
            # File format representation
            """ + MINIMAL_FILE_FORMAT_SUB_QUERY + """
            OPTIONAL { ?f pr:fileFormat.InternalSignature ?sig .
              """ + INTERNAL_SIG_SUB_QUERY + """
            # Byte Sequences
            OPTIONAL {
                """ + BYTE_SEQUENCE_SUB_QUERY + """
                }#END OPTIONAL
            }#END OPTIONAL
            # Format identifiers
            OPTIONAL { ?f pr:fileFormat.In.FileFormatRelationship ?fRel .
            """ + FORMAT_RELATIONSHIPS_SUB_QUERY + """
            }#END OPTIONAL
            # External Signatures
            OPTIONAL {
            """ + EXTERNAL_SIGNATURE_SUB_QUERY + """
            }#END OPTIONAL
            # Format Identifiers
            OPTIONAL{
               """ + FORMAT_IDENTIFIER_SUB_QUERY + """
            }#END OPTIONAL
            }
            """.replaceAll("\\?fRelType", "<" + PRONOM.FormatRelationshipType.PriorityOver + ">");

    public static final String LABEL_TO_URI_QUERY = PREFIXES + "CONSTRUCT { ?s a ?type . } WHERE { ?s a ?type ; rdfs:label ?label . }";

    public static String MULTIPLE_LABEL_TO_URI_QUERY(List<String> ls) {
        String values = ls.stream().map(x -> "(\"" + x + "\")").collect(Collectors.joining(" "));
        return PREFIXES + "CONSTRUCT { ?s a ?type ; rdfs:label ?label . } WHERE { VALUES(?label) { " + values + " } ?s a ?type ; rdfs:label ?label . }";
    }

    public FileFormat getFileFormatByPuid(String puid) {
        return getFileFormatByPuid(puid, "fmt");
    }

    public Resource getURIFromLabel(String label, Resource type) {
        logger.trace("fetching URI of type '" + type + "' for label: " + label);
        Map<String, RDFNode> params = new HashMap<>();
        params.put("label", makeLiteral(label));
        if (type != null) {
            params.put("type", type);
        }
        Model m = TriplestoreUtil.constructQuery(LABEL_TO_URI_QUERY, params);
        ResIterator subject = m.listSubjectsWithProperty(makeProp(RDF.type), type);
        if (subject == null || !subject.hasNext()) return null;
        return subject.nextResource();
    }

    public Resource getURIFromLabel(String label) {
        return getURIFromLabel(label, null);
    }

    public Map<Resource, String> getURIsFromLabels(List<String> ls, Resource type) {
        Map<Resource, String> map = new HashMap<>();
        if (ls == null || ls.isEmpty()) return map;
        logger.trace("fetching URIs of type '" + type + "' for labels: " + Strings.join(ls, ','));
        Map<String, RDFNode> params = new HashMap<>();
        if (type != null) {
            params.put("type", type);
        }
        Model m = TriplestoreUtil.constructQuery(MULTIPLE_LABEL_TO_URI_QUERY(ls), params);
        ModelUtil mu = new ModelUtil(m);
        m.listSubjectsWithProperty(makeProp(RDF.type), type).toList().forEach(uri -> {
            String label = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(RDFS.label)));
            if (label != null) {
                map.put(uri, label);
            }
        });
        return map;
    }

    public FileFormat getFileFormatByPuid(String puid, String puidType) {
        logger.trace("fetching file format by PUID: " + puidType + "/" + puid);
        FileFormat.Deserializer deserializer = new FileFormat.Deserializer();
        Map<String, RDFNode> params = new HashMap<>();
        params.put("puid", makeLiteral(puid, XSDDatatype.XSDinteger));
        params.put("puidTypeName", makeLiteral(puidType));
        Model m = TriplestoreUtil.constructQuery(FILE_FORMAT_QUERY, params);
        ResIterator subject = m.listSubjectsWithProperty(makeProp(RDF.type), deserializer.getRDFType());
        if (subject == null || !subject.hasNext()) return null;
        return deserializer.fromModel(subject.nextResource(), m);
    }

    public FileFormat getFileFormatByURI(Resource uri) {
        logger.trace("fetching file format by URI: " + uri);
        FileFormat.Deserializer deserializer = new FileFormat.Deserializer();
        Map<String, RDFNode> params = new HashMap<>();
        params.put("f", uri);
        Model m = TriplestoreUtil.constructQuery(FILE_FORMAT_QUERY, params);
        ResIterator subject = m.listSubjectsWithProperty(makeProp(RDF.type), deserializer.getRDFType());
        if (subject == null || !subject.hasNext()) return null;
        return deserializer.fromModel(subject.nextResource(), m);
    }

    public List<FileFormat> getAll() {
        logger.trace("fetching all file formats");
        Model m = TriplestoreUtil.constructQuery(FILE_FORMAT_QUERY);
        ModelUtil mu = new ModelUtil(m);
        logger.trace("building file format objects");
        List<FileFormat> fs = mu.buildAllFromModel(new FileFormat.Deserializer());
        logger.trace("file formats built");
        return fs;
    }

    public List<FileFormat> getFileFormatsbyURI(Collection<Resource> uris) {
        logger.trace("fetching file formats: " + uris);
        Model m = TriplestoreUtil.constructQuery(MULTIPLE_FILE_FORMAT_QUERY(uris));
        ModelUtil mu = new ModelUtil(m);
        logger.trace("building file format objects");
        List<FileFormat> fs = mu.buildAllFromModel(new FileFormat.Deserializer());
        logger.trace("file formats built");
        return fs;
    }

    public List<FileFormat> getAllForSignature() {
        logger.trace("fetching all file formats for signature generation");
        Model m = TriplestoreUtil.constructQuery(SIG_GEN_QUERY);
        ModelUtil mu = new ModelUtil(m);
        logger.trace("building file format objects for signature generation");
        List<FileFormat> fs = mu.buildAllFromModel(new FileFormat.Deserializer());
        logger.trace("file formats built for signature generation");
        return fs;
    }

    public void saveFormat(FileFormat ff) {
        logger.trace("saving FileFormat: " + ff.getURI());
        TriplestoreUtil.load(ff.toRDF());
    }

    public void saveAllFormats(List<FileFormat> ffs) {
        if (ffs.isEmpty()) return;
        Model m = ModelFactory.createDefaultModel();
        ffs.forEach(ff -> m.add(ff.toRDF()));
        TriplestoreUtil.load(m);
    }

    public void publishRelease() {
        SubmissionDAO subDao = new SubmissionDAO();
        List<Submission> readySubmissions = subDao.getSubmissionsByStatus(makeResource(PRONOM.Submission.StatusReady));
        // Delete submissions and convert file formats
        List<FileFormat> ffs = readySubmissions.stream().map(s -> {
            subDao.deleteSubmission(s.getURI());
            return s.getFormat().convertToFileFormat();
        }).collect(Collectors.toList());
        // Save formats in triplestore
        saveAllFormats(ffs);
    }
}
