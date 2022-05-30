package com.wallscope.pronombackend.dao;

import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.model.PUID;
import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.TriplestoreUtil;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wallscope.pronombackend.dao.ContainerSignatureDAO.CONTAINER_SIG_SUB_QUERY;
import static com.wallscope.pronombackend.dao.InternalSignatureDAO.INTERNAL_SIG_SUB_QUERY;
import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class FileFormatDAO {
    Logger logger = LoggerFactory.getLogger(FileFormatDAO.class);
    // This sub-query is used to gather file-format related data in non-file format
    public static final String MINIMAL_FILE_FORMAT_SUB_QUERY = """
                ?f a pr:FileFormat ; rdfs:label ?label ; rdfs:comment ?comment ; ff:Puid ?puid ; ff:PuidTypeId ?puidType ; ff:LastUpdatedDate ?updated .
                # Links
                ?puidType rdfs:label ?puidTypeName .
            """;
    public static final String BYTE_SEQUENCE_SUB_QUERY = ByteSequenceDAO.BYTE_SEQUENCE_SUB_QUERY
            .replaceAll("\\?byteSeq pr:byteSequence.ContainerFile \\?contSigFile \\.", "");
    public static final String EXTERNAL_SIGNATURE_SUB_QUERY = "?extSig a pr:ExternalSignature ; pr:externalSignature.FileFormat ?f ; rdfs:label ?extSigName ; pr:externalSignature.SignatureType ?extSigType .";
    public static final String FORMAT_IDENTIFIER_SUB_QUERY = """
            ?fId a pr:FormatIdentifier ; pr:formatIdentifier.FileFormat ?f .
            ?fId rdfs:label ?fIdName ; pr:formatIdentifier.FormatIdentifierType ?fIdType .
            ?fIdType rdfs:label ?fIdTypeName .
            """;
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
            ?f a pr:FileFormat ;
                 rdfs:label ?label ;
                 rdfs:comment ?comment ;
                 ff:Puid ?puid ;
                 ff:PuidTypeId ?puidType ;
                 ff:LastUpdatedDate ?updated ;
                 .
            # Links
            ?puidType rdfs:label ?puidTypeName .
                        
            # Non-required fields
            OPTIONAL { ?f ff:Classification ?classification .
               ?classification rdfs:label ?classificationName .
            }#END OPTIONAL
            OPTIONAL { ?f ff:Version ?version . }#END OPTIONAL
            OPTIONAL { ?f ff:BinaryFlag ?binaryFlag . }#END OPTIONAL
            OPTIONAL { ?f ff:WithdrawnFlag ?withdrawn . }#END OPTIONAL
            OPTIONAL { ?f ff:Development.Actor ?devActor . }#END OPTIONAL
            OPTIONAL { ?f ff:Support.Actor ?supportActor . }#END OPTIONAL
            # Format Identifiers
            OPTIONAL{
               """ + FORMAT_IDENTIFIER_SUB_QUERY + """
            }#END OPTIONAL
            # External Signatures
            OPTIONAL {
                """ + EXTERNAL_SIGNATURE_SUB_QUERY + """
            }#END OPTIONAL
            # Internal Signatures
            OPTIONAL { ?f ff:InternalSignature ?sig .
               """ + INTERNAL_SIG_SUB_QUERY + """
            }#END OPTIONAL
                             
            # Container Signatures
            OPTIONAL { ?f ff:ContainerSignature ?contSig .
               """ + CONTAINER_SIG_SUB_QUERY + """
            }#END OPTIONAL
                           	 
            # Byte Sequences
            OPTIONAL {
               """ + BYTE_SEQUENCE_SUB_QUERY + """
            }#END OPTIONAL
                        
            OPTIONAL { ?f pr:fileFormat.In.FileFormatRelationship ?fRel .
               """ + FORMAT_RELATIONSHIPS_SUB_QUERY + """
            }#END OPTIONAL
            """;
    public static final String FILE_FORMAT_QUERY = PREFIXES + """
            prefix ff: <http://www.nationalarchives.gov.uk/PRONOM/fileFormat.>
            CONSTRUCT {
            """ + trimOptionals(FILE_FORMAT_SUB_QUERY) + """ 
             } WHERE {
            """ + FILE_FORMAT_SUB_QUERY + """
             }
            """;

    public static final String SIG_GEN_QUERY = PREFIXES + """
            prefix ff: <http://www.nationalarchives.gov.uk/PRONOM/fileFormat.>
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
            }
            """;
    public static final String PUID_QUERY = PREFIXES + "CONSTRUCT {?f pr:fileFormat.Puid ?puid; pr:fileFormat.PuidTypeId ?type . ?type rdfs:label ?puidIdName} WHERE{ ?f pr:fileFormat.Puid ?puid; pr:fileFormat.PuidTypeId ?type . ?type rdfs:label ?puidIdName }";

    public FileFormat getFileFormatByPuid(String puid) {
        return getFileFormatByPuid(puid, "fmt");
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

    public PUID getPuidForURI(Resource uri) {
        Map<String, RDFNode> params = new HashMap<>();
        params.put("f", uri);
        Model m = TriplestoreUtil.constructQuery(PUID_QUERY, params);
        ModelUtil mu = new ModelUtil(m);
        RDFNode puidNode = mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.Puid));
        if (puidNode == null || !puidNode.isLiteral()) return null;
        Integer puid = puidNode.asLiteral().getInt();
        String puidType = mu.getOneObjectOrNull(null, makeProp(RDFS.label)).asLiteral().getString();
        return new PUID(puid, puidType.trim());
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
}
