package com.wallscope.pronombackend.dao;

import com.wallscope.pronombackend.model.Submission;
import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.TriplestoreUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class SubmissionDAO {
    Logger logger = LoggerFactory.getLogger(SubmissionDAO.class);
    // Gets user submissions
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
    public static final String MOVE_SUBMISSION_QUERY = PREFIXES + WITH_STATEMENT + """
            DELETE { ?sub pr:submission.SubmissionStatus ?subStatus }
            INSERT { ?sub pr:submission.SubmissionStatus ?newSubStatus }
            WHERE  { ?sub pr:submission.SubmissionStatus ?subStatus }
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

    public void moveSubmission(Resource uri, Resource to) {
        Map<String, RDFNode> params = new HashMap<>();
        params.put("sub", uri);
        params.put("newSubStatus", to);
        logger.debug("moving submission ("+uri+") to: " + to);
        TriplestoreUtil.updateQuery(MOVE_SUBMISSION_QUERY, params);

    }
}
