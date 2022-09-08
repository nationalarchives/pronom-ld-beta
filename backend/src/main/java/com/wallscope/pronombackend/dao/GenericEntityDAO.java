package com.wallscope.pronombackend.dao;


import com.wallscope.pronombackend.model.PUID;
import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.TriplestoreUtil;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class GenericEntityDAO {
    Logger logger = LoggerFactory.getLogger(GenericEntityDAO.class);
    public static final String MINIMAL_PUID_SUB_QUERY = """
                ?ent rdfs:label ?label ;
                    pr:global.Puid ?puid ;
                    pr:global.PuidTypeId ?puidType ;
                    .
                ?ent pr:global.PuidTypeId/rdfs:label ?puidTypeName .
            """;
    public static final String MINIMAL_PUID_QUERY = PREFIXES + """
            CONSTRUCT {
            """ + trimOptionals(MINIMAL_PUID_SUB_QUERY)
            .replaceAll("\\?ent pr:global\\.PuidTypeId/rdfs:label \\?puidTypeName \\.", "?puidType rdfs:label ?puidTypeName .") + """ 
             } WHERE {
            """ + MINIMAL_PUID_SUB_QUERY + """
             }
            """;
    public static final String BASIC_SPO_QUERY = PREFIXES + "CONSTRUCT { ?s ?p ?o . ?ro ?rp ?s . } WHERE { { ?s ?p ?o . } UNION { ?ro ?rp ?s . } } ";

    public static final String PUID_QUERY_WHERE = "WHERE { ?pt rdfs:label ?puidTypeName . ?ff pr:global.PuidTypeId ?pt ; pr:global.Puid ?puid . }";

    public static final String MAX_PUID_QUERY = PREFIXES + "SELECT (MAX(?puid) as ?max) " + PUID_QUERY_WHERE;

    public static final String PUID_EXISTS_QUERY = PREFIXES + "ASK " + PUID_QUERY_WHERE;

    public Model fetchForURI(Resource uri) {
        return TriplestoreUtil.constructQuery(BASIC_SPO_QUERY, Map.of("s", uri));
    }

    public Resource getURIForPuid(String puidType, String puid) {
        Map<String, RDFNode> params = new HashMap<>();
        params.put("puid", makeLiteral(puid, XSDDatatype.XSDinteger));
        params.put("puidTypeName", makeLiteral(puidType));
        Model m = TriplestoreUtil.constructQuery(MINIMAL_PUID_QUERY, params);
        ModelUtil mu = new ModelUtil(m);
        return mu.getOneSubjectOrNull(makeProp(PRONOM.Global.Puid), makeLiteral(puid, XSDDatatype.XSDinteger));
    }

    public PUID getPuidForURI(Resource uri) {
        Map<String, RDFNode> params = new HashMap<>();
        params.put("ent", uri);
        Model m = TriplestoreUtil.constructQuery(MINIMAL_PUID_QUERY, params);
        ModelUtil mu = new ModelUtil(m);
        Integer puid = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Global.Puid)));
        if (puid == null) return null;
        Resource puidTypeUri = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Global.PuidTypeId)));
        if (puidTypeUri == null) return null;
        String puidType = safelyGetStringOrNull(mu.getOneObjectOrNull(puidTypeUri, makeProp(RDFS.label)));
        return new PUID(uri, puid, puidType);
    }

    public Integer nextAvailablePuid(String puidType) {
        AtomicReference<Integer> max = new AtomicReference<>(null);
        TriplestoreUtil.selectQuery(MAX_PUID_QUERY, Map.of("puidTypeName", makeLiteral(puidType)), solution -> {
            Literal lit = solution.getLiteral("max");
            if (lit != null) {
                max.set(lit.getInt());
            }
        });
        Integer maxInt = max.get();
        if (maxInt == null) return null;
        return maxInt + 1;
    }

    public boolean puidExists(String puidType, Integer puid) {
        return TriplestoreUtil.askQuery(PUID_EXISTS_QUERY, Map.of("puidTypeName", makeLiteral(puidType), "puid", makeLiteral(puid)));
    }
}
