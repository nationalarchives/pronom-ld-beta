package com.wallscope.pronombackend.dao;

import com.wallscope.pronombackend.model.Classification;
import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.model.PUID;
import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.TriplestoreUtil;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
              pr:Documentation.Link ?fDocumentationLink .
            OPTIONAL{ ?fDocumentation pr:Documentation.Author ?fDocumentationAuthor . }#END OPTIONAL
            OPTIONAL{ ?fDocumentation pr:Documentation.Identifiers ?fDocumentationIdentifiers . }#END OPTIONAL
            OPTIONAL{ ?fDocumentation pr:Documentation.PublicationDate ?fDocumentationDate . }#END OPTIONAL
            OPTIONAL{ ?fDocumentation pr:Documentation.DocumentType ?fDocumentationType . }#END OPTIONAL
            OPTIONAL{ ?fDocumentation pr:Documentation.Note ?fDocumentationNote . }#END OPTIONAL
            """;
    public static final String FORMAT_IDENTIFIER_SUB_QUERY = """
            ?fId a pr:FormatIdentifier ; pr:formatIdentifier.FileFormat ?f .
            ?fId rdfs:label ?fIdName ; pr:formatIdentifier.FormatIdentifierType ?fIdType .
            ?fIdType rdfs:label ?fIdTypeName .
            """;
    // This sub-query is used to gather file-format related data in non-file format
    public static final String MINIMAL_FILE_FORMAT_SUB_QUERY = """
                ?f a pr:FileFormat ; rdfs:label ?label ; rdfs:comment ?comment ; ff:Puid ?puid ; ff:PuidTypeId ?puidType ; ff:LastUpdatedDate ?updated .
                # Links
                ?puidType rdfs:label ?puidTypeName .
            """;
    public static final String BYTE_SEQUENCE_SUB_QUERY = ByteSequenceDAO.BYTE_SEQUENCE_SUB_QUERY
            .replaceAll("\\?byteSeq pr:byteSequence.ContainerFile \\?contSigFile \\.", "");
    public static final String FORMAT_RELATIONSHIPS_SUB_QUERY = """
            ?fRel pr:fileFormatRelationship.FileFormatRelationshipType ?fRelType ;
              pr:fileFormatRelationship.Source ?fRelSource ;
              pr:fileFormatRelationship.Target ?fRelTarget ;
              .
              OPTIONAL { ?fRel pr:fileFormatRelationship.Note ?fRelNote . }#END OPTIONAL
              
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
            OPTIONAL { ?f pr:fileFormat.Version ?version . }#END OPTIONAL
            OPTIONAL { ?f pr:fileFormat.BinaryFlag ?binaryFlag . }#END OPTIONAL
            OPTIONAL { ?f pr:fileFormat.WithdrawnFlag ?withdrawn . }#END OPTIONAL
            OPTIONAL { ?f pr:fileFormat.Development.Actor ?devActor . }#END OPTIONAL
            OPTIONAL { ?f pr:fileFormat.Support.Actor ?supportActor . }#END OPTIONAL
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
                           	 
                        
            OPTIONAL { ?f pr:fileFormat.In.FileFormatRelationship ?fRel .
               """ + FORMAT_RELATIONSHIPS_SUB_QUERY + """
            }#END OPTIONAL
            """;
    public static final String FILE_FORMAT_QUERY = PREFIXES + """
            CONSTRUCT {
            """ + trimOptionals(FILE_FORMAT_SUB_QUERY) + """ 
             } WHERE {
            """ + FILE_FORMAT_SUB_QUERY + """
             }
            """;

    public static final String SIG_GEN_QUERY = PREFIXES + """
            CONSTRUCT {
              """ + trimOptionals(INTERNAL_SIG_SUB_QUERY) + """
            # Byte Sequences
            """ + trimOptionals(BYTE_SEQUENCE_SUB_QUERY) + """
            # Link File Format
            ?f pr:fileFormat.InternalSignature ?sig .
            # File format representation
            """ + trimOptionals(MINIMAL_FILE_FORMAT_SUB_QUERY) + """
                        
            """ + trimOptionals(FORMAT_RELATIONSHIPS_SUB_QUERY) + """
                        
            """ + trimOptionals(EXTERNAL_SIGNATURE_SUB_QUERY) + """
                        
            """ + trimOptionals(FORMAT_IDENTIFIER_SUB_QUERY) + """
            } WHERE {
              """ + INTERNAL_SIG_SUB_QUERY + """
            # Byte Sequences
            OPTIONAL {
            """ + BYTE_SEQUENCE_SUB_QUERY + """
            }#END OPTIONAL
            # Link File Format
            ?f pr:fileFormat.InternalSignature ?sig .
            # File format representation
            """ + MINIMAL_FILE_FORMAT_SUB_QUERY + """
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
            """;

    public static final String LABEL_TO_URI_QUERY = PREFIXES + "CONSTRUCT { ?s a ?type . } WHERE { ?s a ?type ; rdfs:label ?label . }";
    public static final String PUID_QUERY = PREFIXES + "CONSTRUCT {?f pr:fileFormat.Puid ?puid; pr:fileFormat.PuidTypeId ?type . ?type rdfs:label ?puidIdName} WHERE{ ?f pr:fileFormat.Puid ?puid; pr:fileFormat.PuidTypeId ?type . ?type rdfs:label ?puidIdName }";

    public static String MULTIPLE_LABEL_TO_URI_QUERY(List<String> ls) {
        String values = ls.stream().map(x -> "(\"" + x + "\")").collect(Collectors.joining(" "));
        return PREFIXES + "CONSTRUCT { ?s a ?type ; rdfs:label ?label . } WHERE { VALUES(?label) { " + values + " } ?s a ?type ; rdfs:label ?label . }";
    }

    public FileFormat getFileFormatByPuid(String puid) {
        return getFileFormatByPuid(puid, "fmt");
    }

    public Resource getURIFromLabel(String label, Resource type) {
        logger.debug("fetching URI of type '" + type + "' for label: " + label);
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
        logger.debug("fetching URIs of type '" + type + "' for labels: " + Strings.join(ls, ','));
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
        logger.debug("fetching file format by PUID: " + puidType + "/" + puid);
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
        logger.debug("fetching file format by URI: " + uri);
        FileFormat.Deserializer deserializer = new FileFormat.Deserializer();
        Map<String, RDFNode> params = new HashMap<>();
        params.put("f", uri);
        Model m = TriplestoreUtil.constructQuery(FILE_FORMAT_QUERY, params);
        ResIterator subject = m.listSubjectsWithProperty(makeProp(RDF.type), deserializer.getRDFType());
        if (subject == null || !subject.hasNext()) return null;
        return deserializer.fromModel(subject.nextResource(), m);
    }

    public PUID getPuidForURI(Resource uri) {
        Map<String, RDFNode> params = new HashMap<>();
        params.put("f", uri);
        Model m = TriplestoreUtil.constructQuery(PUID_QUERY, params);
        ModelUtil mu = new ModelUtil(m);
        Integer puid = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.Puid)));
        if (puid == null) return null;
        String puidType = safelyGetStringOrNull(mu.getOneObjectOrNull(null, makeProp(RDFS.label)));
        return new PUID(uri, puid, puidType);
    }

    public PUID getURIforPuid(String puidType, Integer puid) {
        Map<String, RDFNode> params = new HashMap<>();
        Literal puidLit = makeLiteral(puid.toString(), XSDDatatype.XSDinteger);
        params.put("puid", puidLit);
        params.put("puidIdName", makeLiteral(puidType));
        Model m = TriplestoreUtil.constructQuery(PUID_QUERY, params);
        ModelUtil mu = new ModelUtil(m);
        Resource uri = mu.getOneSubjectOrNull(makeProp(PRONOM.FileFormat.Puid), puidLit);
        return new PUID(uri, puid, puidType.trim());
    }

    public List<FileFormat> getAll() {
        logger.debug("fetching all file formats");
        Model m = TriplestoreUtil.constructQuery(FILE_FORMAT_QUERY);
        ModelUtil mu = new ModelUtil(m);
        logger.debug("building file format objects");
        List<FileFormat> fs = mu.buildAllFromModel(new FileFormat.Deserializer());
        logger.debug("file formats built");
        return fs;
    }

    public List<FileFormat> getAllForSignature() {
        logger.debug("fetching all file formats for signature generation");
        Model m = TriplestoreUtil.constructQuery(SIG_GEN_QUERY);
        ModelUtil mu = new ModelUtil(m);
        logger.debug("building file format objects for signature generation");
        List<FileFormat> fs = mu.buildAllFromModel(new FileFormat.Deserializer());
        logger.debug("file formats built for signature generation");
        return fs;
    }

    public List<Classification> getClassifications(List<String> ls) {
        Map<Resource, String> map = getURIsFromLabels(ls, makeResource(PRONOM.Classification.type));
        return map.entrySet().stream().map(entry -> new Classification(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }
}
