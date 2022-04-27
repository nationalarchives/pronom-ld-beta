package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class SearchResultDeserializer implements RDFDeserializer<SearchResult> {
    private final List<String> excludeProps = List.of(RDF.type, RDFS.label, RDFS.comment);

    @Override
    public Resource getRDFType() {
        return makeResource(PRONOM.SearchResult.type);
    }

    @Override
    public SearchResult fromModel(Resource uri, Model model) {
        ModelUtil mu = new ModelUtil(model);
        // Required
        String name = mu.getOneObjectOrNull(uri, makeProp(RDFS.label)).asLiteral().getString();
        Float score = mu.getOneObjectOrNull(uri, makeProp(PRONOM.SearchResult.Score)).asLiteral().getFloat();
        List<Resource> field = mu.getAllObjects(uri, makeProp(PRONOM.SearchResult.Field)).stream().map(RDFNode::asResource).collect(Collectors.toList());
        Resource type = safelyGetResourceOrNull(mu.getAllObjects(uri, makeProp(RDF.type)).stream()
                .filter(t -> !t.asResource().getURI().equals(PRONOM.SearchResult.type)).findFirst().orElse(null));
        // Optional w/ default
        String description = safelyGetStringOrDefault(mu.getOneObjectOrNull(uri, makeProp(RDFS.comment)), "");
        String match = safelyGetStringOrDefault(mu.getAllObjects(uri, makeProp(PRONOM.SearchResult.Match)).stream()
                .min(Comparator.comparingInt(a -> safelyGetStringOrDefault(a, "").length())).orElse(null), "");
        // Other properties
        // Get all statements relating to this subject that are not rdfs:label or rdfs:comment
        Map<String, RDFNode> props = mu.list(uri, null, null).stream().filter(stmt -> {
            Property p = stmt.getPredicate();
            return !excludeProps.contains(p.getURI());
        }).collect(Collectors.toMap((s) -> s.getPredicate().getURI(), Statement::getObject, (a, b) -> a));
        return new SearchResult(uri, type, score, match, field, name, description, props);
    }
}
