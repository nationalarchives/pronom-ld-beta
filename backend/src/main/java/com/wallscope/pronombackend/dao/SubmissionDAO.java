package com.wallscope.pronombackend.dao;

import com.wallscope.pronombackend.model.Submission;
import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.TriplestoreUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wallscope.pronombackend.dao.FileFormatDAO.FILE_FORMAT_SUB_QUERY;
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
               
               """ + FILE_FORMAT_SUB_QUERY
            // A TentativeFileFormat has all of these fields as optionals
            .replaceAll("\\?f a pr:FileFormat", "?f a pr:TentativeFileFormat")
            .replaceAll("\\?f rdfs:label \\?label \\.", "OPTIONAL { ?f rdfs:label ?label . }#END OPTIONAL")
            .replaceAll("\\?f rdfs:comment \\?comment \\.", "OPTIONAL { ?f rdfs:comment ?comment . }#END OPTIONAL")
            .replaceAll("\\?f pr:fileFormat.Puid \\?puid \\.", "OPTIONAL { ?f pr:fileFormat.Puid ?puid . }#END OPTIONAL")
            .replaceAll("\\?f pr:fileFormat.PuidTypeId \\?puidType \\.", "OPTIONAL { ?f pr:fileFormat.PuidTypeId ?puidType . ?puidType rdfs:label ?puidTypeName . }#END OPTIONAL")
            .replaceAll("# Links\\n\\?puidType rdfs:label \\?puidTypeName .\\n", "")
            .replaceAll("\\?f pr:fileFormat.LastUpdatedDate \\?updated \\.", "OPTIONAL { ?f pr:fileFormat.LastUpdatedDate ?updated . }#END OPTIONAL")
            + """
                        
            """;
    public static final String SUBMISSION_QUERY = PREFIXES + """
            CONSTRUCT {
            """ + trimOptionals(SUBMISSION_SUB_QUERY) + """
            } WHERE {
            """ + SUBMISSION_SUB_QUERY + """
            }
            """;
    public static final String MOVE_SUBMISSION_QUERY = PREFIXES + WITH_STATEMENT + """
            DELETE { ?sub pr:submission.SubmissionStatus ?subStatus }
            INSERT { ?sub pr:submission.SubmissionStatus ?newSubStatus }
            WHERE  { ?sub pr:submission.SubmissionStatus ?subStatus }
            """;
    public static final String SUBMISSION_DELETE_SUB_QUERY = SUBMISSION_SUB_QUERY
            .replaceAll("pr:submission.FileFormat \\?f ;", "pr:submission.FileFormat ?f ;  pr:submission.Source ?subSource ;")
            .replaceAll("\\?puidType rdfs:label \\?puidTypeName \\.", "")
            .replaceAll("\\?classification rdfs:label \\?classificationName \\.", "")
            .replaceAll("\\?fIdType rdfs:label \\?fIdTypeName \\.", "")
            .replaceAll("\\?intByteSeqPosition rdfs:label \\?intByteSeqPositionName \\.", "")
            .replaceAll("\\?contByteSeqPosition rdfs:label \\?contByteSeqPositionName \\.", "")
            .replaceAll("\\?fRelSource rdfs:label \\?fRelSourceName \\.", "")
            .replaceAll("\\?fRelTarget rdfs:label \\?fRelTargetName \\.", "")
            .replaceAll("\\?fRelType pr:formatRelationshipType\\.TypeName \\?fRelTypeName ;\\n\\s\\spr:formatRelationshipType\\.InverseTypeName \\?fRelInverseTypeName \\.", "");

    public static final String DELETE_SUBMISSION_QUERY = PREFIXES + WITH_STATEMENT + """
            DELETE {
                """ + trimOptionals(SUBMISSION_DELETE_SUB_QUERY) + """
            } WHERE {
            """ + SUBMISSION_DELETE_SUB_QUERY + """
            }
            """;


    public List<Submission> getAllSubmissions() {
        logger.debug("fetching all submissions");
        Model m = TriplestoreUtil.constructQuery(MINIMAL_SUBMISSION_QUERY);
        ModelUtil mu = new ModelUtil(m);
        logger.debug("building submission objects");
        List<Submission> subs = mu.buildAllFromModel(new Submission.Deserializer());
        logger.debug("submissions built");
        return subs;
    }

    public void saveSubmission(Submission sub) {
        logger.debug("saving Submission: " + sub.getURI());
        TriplestoreUtil.load(sub.toRDF());
    }

    public void moveSubmission(Resource uri, Resource to) {
        Map<String, RDFNode> params = new HashMap<>();
        params.put("sub", uri);
        params.put("newSubStatus", to);
        logger.debug("moving submission (" + uri + ") to: " + to);
        TriplestoreUtil.updateQuery(MOVE_SUBMISSION_QUERY, params);
    }

    public void deleteSubmission(Resource uri) {
        Map<String, RDFNode> params = new HashMap<>();
        params.put("sub", uri);
        logger.debug("deleting submission: " + uri);
        TriplestoreUtil.updateQuery(DELETE_SUBMISSION_QUERY, params);
    }

    public Submission getSubmissionByURI(Resource uri) {
        logger.debug("fetching Submission by uri: " + uri);
        Submission.Deserializer deserializer = new Submission.Deserializer();
        Map<String, RDFNode> params = new HashMap<>();
        params.put("sub", uri);
        Model m = TriplestoreUtil.constructQuery(SUBMISSION_QUERY, params);
        ResIterator subject = m.listSubjectsWithProperty(makeProp(RDF.type), deserializer.getRDFType());
        if (subject == null || !subject.hasNext()) return null;
        return deserializer.fromModel(subject.nextResource(), m);
    }
}
