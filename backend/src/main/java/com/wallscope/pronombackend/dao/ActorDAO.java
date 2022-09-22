package com.wallscope.pronombackend.dao;

import com.wallscope.pronombackend.model.Actor;
import com.wallscope.pronombackend.utils.RDFUtil;
import com.wallscope.pronombackend.utils.TriplestoreUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class ActorDAO {
    Logger logger = LoggerFactory.getLogger(ActorDAO.class);
    public static final String ACTOR_SUB_QUERY = """
            ?act a pr:Actor ; rdf:type ?actExtraTypes .
            OPTIONAL{ ?act rdfs:label ?actName . }#END OPTIONAL
            OPTIONAL{ ?act pr:actor.OrganisationName ?actOrganisation . }#END OPTIONAL
            OPTIONAL{ ?act pr:actor.Email ?actEmail . }#END OPTIONAL
            OPTIONAL{ ?act pr:actor.Country ?actCountry . }#END OPTIONAL
            OPTIONAL{ ?act pr:actor.Website ?actWebsite . }#END OPTIONAL
            """;
    public static final String ACTOR_QUERY = PREFIXES + """
            CONSTRUCT {
              """ + trimOptionals(ACTOR_SUB_QUERY) + """
            } WHERE {
              """ + ACTOR_SUB_QUERY + """
            }
            """;
    public static final String DELETE_ACTOR_QUERY = PREFIXES + WITH_STATEMENT + """
            DELETE {
                ?act a pr:Actor ; ?p ?o .
            } WHERE {
                ?act a pr:Actor ; ?p ?o .
            }
            """;

    public Actor getActorByURI(Resource uri) {
        logger.trace("fetching actor by URI: " + uri);
        Actor.Deserializer deserializer = new Actor.Deserializer();
        Map<String, RDFNode> params = new HashMap<>();
        params.put("act", uri);
        Model m = TriplestoreUtil.constructQuery(ACTOR_QUERY, params);
        ResIterator subject = m.listSubjectsWithProperty(makeProp(RDFUtil.RDF.type), deserializer.getRDFType());
        if (subject == null || !subject.hasNext()) return null;
        return deserializer.fromModel(subject.nextResource(), m);
    }

    public void saveActor(Actor act) {
        logger.trace("saving Actor: " + act.getURI());
        TriplestoreUtil.load(act.toRDF());
    }

    public void deleteActor(Resource uri) {
        Map<String, RDFNode> params = new HashMap<>();
        params.put("act", uri);
        logger.trace("deleting actor: " + uri);
        TriplestoreUtil.updateQuery(DELETE_ACTOR_QUERY, params);
    }
}
