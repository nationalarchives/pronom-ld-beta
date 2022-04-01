package com.wallscope.pronombackend.utils;

import com.wallscope.pronombackend.config.ApplicationConfig;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.riot.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TriplestoreUtil {
    // Internals
    private static final Logger logger = LoggerFactory.getLogger(TriplestoreUtil.class);
    private static final String endpoint = ApplicationConfig.TRIPLESTORE;
    private static final RDFConnectionRemoteBuilder conn = RDFConnectionRemote.create()
            .destination(endpoint)
            .queryEndpoint("sparql")
            .updateEndpoint("update")
            .gspEndpoint("data")
            .triplesFormat(RDFFormat.TURTLE);
    // Exports
    public static final String GRAPH = RDFUtil.PRONOM.uri;

    public static boolean checkIfExists(Resource s, Property p, RDFNode o) {
        if (s == null) return false;
        ParameterizedSparqlString query = new ParameterizedSparqlString();
        query.setCommandText("ASK { ?s ?p ?o }");
        query.setParam("s", s);
        if (p != null) {
            query.setParam("p", p);
        }
        if (o != null) {
            query.setParam("o", o);
        }
        logger.debug("sending ask: " + query);
        return conn.build().queryAsk(query.asQuery());
    }

    public static Model constructQuery(String query, Map<String, RDFNode> params) {
        ParameterizedSparqlString q = new ParameterizedSparqlString();
        q.setCommandText(query);
        if (params != null) {
            for (Map.Entry<String, RDFNode> entry : params.entrySet()) {
                q.setParam(entry.getKey(), entry.getValue());
            }
        }
        logger.debug("sending query: " + q);
        return conn.build().queryConstruct(q.asQuery());
    }
}
