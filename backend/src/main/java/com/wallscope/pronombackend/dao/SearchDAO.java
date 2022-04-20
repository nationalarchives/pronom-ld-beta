package com.wallscope.pronombackend.dao;

import com.wallscope.pronombackend.model.SearchResult;
import com.wallscope.pronombackend.model.SearchResultDeserializer;
import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.TriplestoreUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wallscope.pronombackend.utils.RDFUtil.PREFIXES;
import static com.wallscope.pronombackend.utils.RDFUtil.makeLiteral;

public class SearchDAO {
    Logger logger = LoggerFactory.getLogger(SearchDAO.class);
    public static final String ALLOWED_TYPES = " (pr:FileFormat) (pr:Actor) (pr:InternalSignature) (pr:ExternalSignature) (pr:Software) (pr:Hardware) (pr:Encoding) (pr:CompressionType) ";
    public static final String SEARCH_FIELDS = " (rdfs:label) (rdfs:comment) (skos:notation) (skos:hiddenLabel) ";
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
                SELECT ?result ?sc ?lit ?field WHERE {
                  VALUES (?field) {
                  """ + SEARCH_FIELDS + """
            }
            (?result ?sc ?lit) text:query (?field ?query) .
            FILTER EXISTS {
              VALUES (?allowedTypes){
              """ + ALLOWED_TYPES + """
                    }
                    ?result a ?allowedTypes .
                  }
                } ORDER BY DESC(?sc) LIMIT ?limit OFFSET ?offset
              }
              ?result a ?type ; rdfs:label ?label ; ?p ?o .
            }
            """;

    public List<SearchResult> search(String query, Integer limit, Integer offset) {
        logger.debug("fetching search results");
        Map<String, RDFNode> params = new HashMap<>();
        params.put("query", makeLiteral(query));
        params.put("limit", makeLiteral(limit));
        params.put("offset", makeLiteral(offset));
        Model m = TriplestoreUtil.constructQuery(SEARCH_QUERY, params);
        ModelUtil mu = new ModelUtil(m);
        return mu.buildAllFromModel(new SearchResultDeserializer());
    }
}
