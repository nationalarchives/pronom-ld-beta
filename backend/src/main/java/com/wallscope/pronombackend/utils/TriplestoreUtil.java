package com.wallscope.pronombackend.utils;

import com.wallscope.pronombackend.config.ApplicationConfig;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.riot.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

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
        logger.trace("sending ask: " + query);
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
        logger.trace("sending construct: " + q);
        return conn.build().queryConstruct(q.asQuery());
    }

    public static Model constructQuery(String query) {
        return constructQuery(query, null);
    }

    // All we really need to sanitise for a Literal is quotes and forward slashes.
    // As long as we don't allow an attacker to close the quotes early, there's nothing really they can do inside quotes that would change the result of a SPARQL query
    // Forward slashes are allowed in RDF literals but they break the text index search
    public static String sanitiseLiteral(String input) {
        // I know this looks weird, this explains it: https://stackoverflow.com/a/51057519/2614483
        String out = input.replaceAll("\"", "\\\\\\\"")
                .replaceAll("/", "\\\\/");
        logger.trace("SANITISING, in=[" + input + "], out=[" + out + "]");

        return out;
    }

    public static void selectQuery(String query, Map<String, RDFNode> params, Consumer<QuerySolution> f) {
        ParameterizedSparqlString q = new ParameterizedSparqlString();
        q.setCommandText(query);
        if (params != null) {
            for (Map.Entry<String, RDFNode> entry : params.entrySet()) {
                q.setParam(entry.getKey(), entry.getValue());
            }
        }
        logger.trace("sending select: " + q);
        conn.build().querySelect(q.asQuery(), f);
    }

    public static void selectQuery(String query, Consumer<QuerySolution> f) {
        selectQuery(query, null, f);
    }

    public static void updateQuery(String query, Map<String, RDFNode> params) {
        ParameterizedSparqlString q = new ParameterizedSparqlString();
        q.setCommandText(query);
        if (params != null) {
            for (Map.Entry<String, RDFNode> entry : params.entrySet()) {
                q.setParam(entry.getKey(), entry.getValue());
            }
        }
        logger.trace("sending update: " + q);
        conn.build().update(q.asUpdate());
    }

    public static void updateQuery(String query) {
        updateQuery(query, null);
    }

    public static boolean askQuery(String query, Map<String, RDFNode> params) {
        ParameterizedSparqlString q = new ParameterizedSparqlString();
        q.setCommandText(query);
        if (params != null) {
            for (Map.Entry<String, RDFNode> entry : params.entrySet()) {
                q.setParam(entry.getKey(), entry.getValue());
            }
        }
        logger.trace("sending ask: " + q);
        return conn.build().queryAsk(q.asQuery());
    }

    public static boolean askQuery(String query) {
        return askQuery(query, null);
    }

    public static void load(Model m) {
        conn.build().load(GRAPH, m);
    }
}
