package com.wallscope.pronombackend.dao;

import com.wallscope.pronombackend.model.SearchResult;
import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.TriplestoreUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.wallscope.pronombackend.utils.RDFUtil.PREFIXES;
import static com.wallscope.pronombackend.utils.RDFUtil.makeLiteral;

public class SearchDAO {
    Logger logger = LoggerFactory.getLogger(SearchDAO.class);
    public static final String ALLOWED_TYPES = " (pr:FileFormat) (pr:Actor) (pr:InternalSignature) (pr:ExternalSignature) (pr:Software) (pr:Hardware) (pr:Encoding) (pr:CompressionType) ";
    public static final String SUB_SELECT_WHERE = """
            WHERE {
                VALUES (?field) {
                    #SEARCH_FIELDS#
                }
                (?result ?sc ?lit) text:query (?field ?query "highlight:") .
                OPTIONAL { ?result pr:global.Puid ?puid }
                FILTER EXISTS {
                    VALUES (?allowedTypes){
                        #ALLOWED_TYPES#
                    }
                ?result a ?allowedTypes .
                }
            }
            """;
    public static final String LIMIT_OFFSET = " LIMIT ?limit OFFSET ?offset";
    public static final String SUB_SELECT_SEARCH = """
            SELECT ?result ?sc ?lit ?field
            """ + SUB_SELECT_WHERE + "#ORDER# " + LIMIT_OFFSET;
    public static final String SUB_SELECT_COUNT = """
            SELECT (COUNT(DISTINCT ?result) as ?count)
            """ + SUB_SELECT_WHERE;
    public static final String COUNT_QUERY = PREFIXES + "prefix text: <http://jena.apache.org/text#>\n" + SUB_SELECT_COUNT;
    public static final String SEARCH_QUERY = PREFIXES + """
            PREFIX text: <http://jena.apache.org/text#>
            CONSTRUCT {
              ?result a pr:SearchResult ;
                pr:search.Score ?sc ;
                pr:search.Match ?lit ;
                pr:search.Field ?field ;
            	.
              ?result a ?type ; rdfs:label ?label ; ?p ?o .
            }
            WHERE {
              {
              """ + SUB_SELECT_SEARCH + """
              }
              ?result a ?type ; rdfs:label ?label ; ?p ?o .
            }
            """;

    public List<SearchResult> search(String q, Integer limit, Integer offset, Filters filters, String sort) {
        logger.debug("fetching search results");
        String sanitised = TriplestoreUtil.sanitiseLiteral(q);
        Map<String, RDFNode> params = new HashMap<>();
        params.put("query", makeLiteral(sanitised));
        params.put("limit", makeLiteral(limit));
        params.put("offset", makeLiteral(offset));
        Model m = TriplestoreUtil.constructQuery(preprocessQuery(SEARCH_QUERY, filters, sort), params);
        ModelUtil mu = new ModelUtil(m);
        return mu.buildAllFromModel(new SearchResult.Deserializer());
    }

    public Integer count(String q, Integer limit, Integer offset, Filters filters) {
        logger.debug("counting search results");
        String sanitised = TriplestoreUtil.sanitiseLiteral(q);
        Map<String, RDFNode> params = new HashMap<>();
        params.put("query", makeLiteral(sanitised));
        params.put("limit", makeLiteral(limit));
        params.put("offset", makeLiteral(offset));
        AtomicReference<Integer> count = new AtomicReference<>();
        TriplestoreUtil.selectQuery(preprocessQuery(COUNT_QUERY, filters), params, solution -> count.set(solution.getLiteral("count").getInt()));
        return count.get();
    }

    private String preprocessQuery(String q, Filters f) {
        return preprocessQuery(q, f, null);
    }

    private String preprocessQuery(String q, Filters f, String sort) {
        StringBuilder fields = new StringBuilder();
        if (f.name) fields.append(" (rdfs:label)");
        if (f.description) fields.append(" (rdfs:comment)");
        if (f.extension) fields.append(" (skos:notation)");
        if (f.puid) fields.append(" (skos:hiddenLabel)");
        fields.append(" ");
        String processed = q;
        if (sort != null) {
            String order = switch (sort) {
                case "name" -> "ORDER BY ?label";
                case "type" -> "ORDER BY ?type";
                case "puid" -> "ORDER BY COALESCE(xsd:integer(?puid), 9999999)";
                default -> "ORDER BY DESC(?sc)";
            };
            processed = processed.replace("#ORDER#", order);
        }
        return processed.replace("#ALLOWED_TYPES#", ALLOWED_TYPES).replace("#SEARCH_FIELDS#", fields.toString());
    }

    public static class Filters {
        public Boolean name;
        public Boolean extension;
        public Boolean description;
        public Boolean puid;

        public Filters(Boolean name, Boolean extension, Boolean description, Boolean puid) {
            this.name = name;
            this.extension = extension;
            this.description = description;
            this.puid = puid;
        }

    }
}
