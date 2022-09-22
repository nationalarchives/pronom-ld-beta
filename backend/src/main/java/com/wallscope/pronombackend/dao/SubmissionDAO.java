package com.wallscope.pronombackend.dao;

import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.model.Submission;
import com.wallscope.pronombackend.model.TentativeFileFormat;
import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.TriplestoreUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.dao.ContainerSignatureDAO.CONTAINER_SIG_SUB_QUERY;
import static com.wallscope.pronombackend.dao.FileFormatDAO.*;
import static com.wallscope.pronombackend.dao.InternalSignatureDAO.INTERNAL_SIG_SUB_QUERY;
import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class SubmissionDAO {
    Logger logger = LoggerFactory.getLogger(SubmissionDAO.class);
    public static final String MINIMAL_SUBMISSION_SUB_QUERY = """
             ?sub a pr:Submission ;
               pr:submission.SubmissionStatus ?subStatus ;
               pr:submission.SubmissionType ?subType ;
               pr:submission.Created ?created ;
               pr:submission.Contributor ?subContributor ;
               pr:submission.FileFormat ?subFf ;
               .
              
             OPTIONAL{ ?sub pr:submission.Updated ?updated . }#END OPTIONAL
             
             ?subContributor a pr:Contributor ;
               pr:contributor.Internal ?subContributorInternal ;
               pr:contributor.Anonymous ?subContributorAnonymous ;
               rdfs:label ?subContributorName ;
               .
             
             ?subFf rdfs:label ?subFfName .
                      
            """;
    public static final String MINIMAL_SUBMISSION_QUERY = PREFIXES + """
            CONSTRUCT {
            """ + trimOptionals(MINIMAL_SUBMISSION_SUB_QUERY) + """
            } WHERE {
            """ + MINIMAL_SUBMISSION_SUB_QUERY + """
            }
            """;
    public static final String SUBMISSION_SUB_QUERY = """
            ?sub a pr:Submission ;
               pr:submission.SubmissionStatus ?subStatus ;
               pr:submission.SubmissionType ?subType ;
               pr:submission.Created ?created ;
               pr:submission.Contributor ?subContributor ;
               pr:submission.FileFormat ?f ;
               .
            OPTIONAL { ?sub pr:submission.Source ?subSource . }#END OPTIONAL
               """ + FILE_FORMAT_SUB_QUERY
            // A TentativeFileFormat has all of these fields as optionals
            .replaceAll("\\?f a pr:FileFormat", "?f a pr:TentativeFileFormat")
            .replaceAll("\\?f rdfs:label \\?label \\.", "OPTIONAL { ?f rdfs:label ?label . }#END OPTIONAL")
            .replaceAll("\\?f rdfs:comment \\?comment \\.", "OPTIONAL { ?f rdfs:comment ?comment . }#END OPTIONAL")
            .replaceAll("\\?f pr:fileFormat\\.Puid \\?puid \\.", "OPTIONAL { ?f pr:fileFormat.Puid ?puid . }#END OPTIONAL")
            .replaceAll("\\?f pr:fileFormat\\.PuidTypeId \\?puidType \\.", "OPTIONAL { ?f pr:fileFormat.PuidTypeId ?puidType . ?puidType rdfs:label ?puidTypeName . }#END OPTIONAL")
            .replaceAll("# Links\\n\\?puidType rdfs:label \\?puidTypeName \\.\\n", "")
            .replaceAll("\\?f pr:fileFormat\\.LastUpdatedDate \\?updated \\.", "OPTIONAL { ?f pr:fileFormat.LastUpdatedDate ?updated . }#END OPTIONAL")
            + """
                        
            """;
    public static final String SUBMISSION_QUERY = PREFIXES + """
            CONSTRUCT {
            """ + trimOptionals(SUBMISSION_SUB_QUERY) + """
            } WHERE {
            """ + SUBMISSION_SUB_QUERY + """
            }
            """;
    public static final String SUBMISSION_SIG_GEN_QUERY = PREFIXES + """
            CONSTRUCT {
            # Submission link
            ?sub a pr:Submission ;
               pr:submission.SubmissionStatus ?subStatus ;
               pr:submission.FileFormat ?f ;
               .
            # File format representation
            """ + trimOptionals(MINIMAL_FILE_FORMAT_SUB_QUERY)
            .replaceAll("\\?f a pr:FileFormat", "?f a pr:TentativeFileFormat")
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
            VALUES (?subStatus) {
              #STATUS#
            }
            # Submission link
            ?sub a pr:Submission ;
               pr:submission.SubmissionStatus ?subStatus ;
               pr:submission.FileFormat ?f ;
               .
            # File format representation
            """ + MINIMAL_FILE_FORMAT_SUB_QUERY
            .replaceAll("\\?f a pr:FileFormat", "?f a pr:TentativeFileFormat") + """
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

    public static final String SUBMISSION_CONTAINER_SIG_GEN_QUERY = PREFIXES + """
            CONSTRUCT {
            # Submission link
            ?sub a pr:Submission ;
               pr:submission.SubmissionStatus ?subStatus ;
               pr:submission.FileFormat ?f ;
               .
            # File format representation
            """ + trimOptionals(MINIMAL_FILE_FORMAT_SUB_QUERY)
            .replaceAll("\\?f a pr:FileFormat", "?f a pr:TentativeFileFormat")
            .replaceAll("\\?f ff:PuidTypeId/rdfs:label \\?puidTypeName \\.", "?fPuidType rdfs:label ?puidTypeName .") + """
            # Container signatures
            """ + trimOptionals(CONTAINER_SIG_SUB_QUERY) + """
            # Link File Format
            ?f pr:fileFormat.ContainerSignature ?contSig .
            # Byte Sequences
            """ + trimOptionals(BYTE_SEQUENCE_SUB_QUERY) + """
            } WHERE {
            VALUES (?subStatus) {
              #STATUS#
            }
            # Submission link
            ?sub a pr:Submission ;
               pr:submission.SubmissionStatus ?subStatus ;
               pr:submission.FileFormat ?f ;
               .
            # File format representation
            """ + MINIMAL_FILE_FORMAT_SUB_QUERY
            .replaceAll("\\?f a pr:FileFormat", "?f a pr:TentativeFileFormat") + """
            # Link File Format
            ?f pr:fileFormat.ContainerSignature ?contSig .
            # Container signatures
            """ + CONTAINER_SIG_SUB_QUERY + """
            # Byte Sequences
              OPTIONAL {
              """ + BYTE_SEQUENCE_SUB_QUERY + """
              }#END OPTIONAL
            }
            """;

    public static final String MOVE_SUBMISSION_QUERY = PREFIXES + WITH_STATEMENT + """
            DELETE { ?sub pr:submission.SubmissionStatus ?subStatus }
            INSERT { ?sub pr:submission.SubmissionStatus ?newSubStatus }
            WHERE  { ?sub pr:submission.SubmissionStatus ?subStatus }
            """;
    public static final String SUBMISSION_DELETE_SUB_QUERY = SUBMISSION_SUB_QUERY
            .replaceAll("\\?puidType rdfs:label \\?puidTypeName \\.", "")
            .replaceAll("\\?classification rdfs:label \\?classificationName \\.", "")
            .replaceAll("\\?fIdType rdfs:label \\?fIdTypeName \\.", "")
            .replaceAll("\\?intByteSeqPosition rdfs:label \\?intByteSeqPositionName \\.", "")
            .replaceAll("\\?contByteSeqPosition rdfs:label \\?contByteSeqPositionName \\.", "")
            .replaceAll("\\?fRelSource rdfs:label \\?fRelSourceName \\.", "")
            .replaceAll("\\?fRelTarget rdfs:label \\?fRelTargetName \\.", "")
            .replaceAll("\\?fRelType pr:formatRelationshipType\\.TypeName \\?fRelTypeName ;\\n\\s\\spr:formatRelationshipType\\.InverseTypeName \\?fRelInverseTypeName \\.", "")
            .replaceAll("\\?fFamily rdfs:label \\?fFamilyName \\.", "")
            .replaceAll("^(?:OPTIONAL\\{)?\\s?\\?f(?:Support|Dev)Actor.*","");

    public static final String DELETE_SUBMISSION_QUERY = PREFIXES + WITH_STATEMENT + """
            DELETE {
                """ + trimOptionals(SUBMISSION_DELETE_SUB_QUERY) + """
            } WHERE {
            """ + SUBMISSION_DELETE_SUB_QUERY + """
            }
            """;


    public List<Submission> getAllSubmissions() {
        logger.trace("fetching all submissions");
        Model m = TriplestoreUtil.constructQuery(MINIMAL_SUBMISSION_QUERY);
        ModelUtil mu = new ModelUtil(m);
        logger.trace("building submission objects");
        List<Submission> subs = mu.buildAllFromModel(new Submission.Deserializer());
        logger.trace("submissions built");
        return subs;
    }

    public List<FileFormat> getForBinarySignature(List<String> statuses) {
        logger.trace("fetching file formats for signature generation with statuses: " + statuses);
        String statusStr = statuses.stream().map(ex -> "(<" + ex + ">)").collect(Collectors.joining(" "));
        Model m = TriplestoreUtil.constructQuery(SUBMISSION_SIG_GEN_QUERY.replace("#STATUS#", statusStr));
        ModelUtil mu = new ModelUtil(m);
        logger.trace("building file format objects for signature generation");
        List<FileFormat> fs = mu.buildAllFromModel(new TentativeFileFormat.Deserializer());
        logger.trace("MODEL: \n" + mu.toString(Lang.TTL));
        logger.trace("file formats built for signature generation");
        return fs;
    }

    public List<FileFormat> getForContainerSignature(List<String> statuses) {
        logger.trace("fetching file formats for container signature generation with statuses: " + statuses);
        String statusStr = statuses.stream().map(ex -> "(<" + ex + ">)").collect(Collectors.joining(" "));
        Model m = TriplestoreUtil.constructQuery(SUBMISSION_CONTAINER_SIG_GEN_QUERY.replace("#STATUS#", statusStr));
        ModelUtil mu = new ModelUtil(m);
        logger.trace("building file format objects for signature generation");
        List<FileFormat> fs = mu.buildAllFromModel(new TentativeFileFormat.Deserializer());
        logger.trace("file formats built for signature generation");
        return fs;
    }

    public List<Submission> getSubmissionsByStatus(Resource status) {
        logger.trace("fetching submissions with status: " + status);
        Model m = TriplestoreUtil.constructQuery(SUBMISSION_QUERY, Map.of("subStatus", status));
        ModelUtil mu = new ModelUtil(m);
        logger.trace("building submission objects");
        List<Submission> subs = mu.buildAllFromModel(new Submission.Deserializer());
        logger.trace("submissions built");
        return subs;
    }

    public void saveSubmission(Submission sub) {
        logger.trace("saving Submission: " + sub.getURI());
        TriplestoreUtil.load(sub.toRDF());
    }

    public void moveSubmission(Resource uri, Resource to) {
        Map<String, RDFNode> params = new HashMap<>();
        params.put("sub", uri);
        params.put("newSubStatus", to);
        logger.trace("moving submission (" + uri + ") to: " + to);
        TriplestoreUtil.updateQuery(MOVE_SUBMISSION_QUERY, params);
    }

    public void deleteSubmission(Resource uri) {
        Map<String, RDFNode> params = new HashMap<>();
        params.put("sub", uri);
        logger.trace("deleting submission: " + uri);
        TriplestoreUtil.updateQuery(DELETE_SUBMISSION_QUERY, params);
    }

    public Submission getSubmissionByURI(Resource uri) {
        logger.trace("fetching Submission by uri: " + uri);
        Submission.Deserializer deserializer = new Submission.Deserializer();
        Map<String, RDFNode> params = new HashMap<>();
        params.put("sub", uri);
        Model m = TriplestoreUtil.constructQuery(SUBMISSION_QUERY, params);
        ResIterator subject = m.listSubjectsWithProperty(makeProp(RDF.type), deserializer.getRDFType());
        if (subject == null || !subject.hasNext()) return null;
        return deserializer.fromModel(subject.nextResource(), m);
    }

    public static final List<String> statuses = List.of(
            PRONOM.Submission.StatusWaiting,
            PRONOM.Submission.StatusNextRelease,
            PRONOM.Submission.StatusWIP,
            PRONOM.Submission.StatusTesting,
            PRONOM.Submission.StatusReady);

    public static List<String> statusList(String status) {
        if (!statuses.contains(status)) return Collections.emptyList();
        return statuses.subList(statuses.indexOf(status), statuses.size());
    }
}
