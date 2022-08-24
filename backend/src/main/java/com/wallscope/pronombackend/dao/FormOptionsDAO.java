package com.wallscope.pronombackend.dao;

import com.wallscope.pronombackend.model.LabeledURI;
import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.RDFUtil;
import com.wallscope.pronombackend.utils.TriplestoreUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.wallscope.pronombackend.utils.RDFUtil.PREFIXES;
import static com.wallscope.pronombackend.utils.RDFUtil.makeProp;

public class FormOptionsDAO {
    public static String queryForTypes(List<Resource> ts) {
        StringBuilder types = new StringBuilder();
        ts.stream().map(RDFUtil::safelyGetUriOrNull).filter(Objects::nonNull).forEach(uri -> types.append(" (<").append(uri).append(">)"));
        types.append(" ");
        return PREFIXES + """
                CONSTRUCT { ?value a ?type ; rdfs:label ?label . }
                WHERE {
                  VALUES (?type) {
                    #TYPES#
                  }
                  ?value a ?type ; rdfs:label ?label .
                }
                """.replace("#TYPES#", types);
    }

    public Map<String, List<LabeledURI>> getOptionsOfType(List<Resource> types) {
        HashMap<String, List<LabeledURI>> map = new HashMap<>();
        Model m = TriplestoreUtil.constructQuery(queryForTypes(types));
        ModelUtil mu = new ModelUtil(m);
        types.forEach(t -> {
            List<LabeledURI> fos = mu.buildFromModel(new LabeledURI.Deserializer(), mu.getAllSubjects(makeProp(RDFUtil.RDF.type), t));
            map.put(t.getURI(), fos);
        });
        return map;
    }
}
