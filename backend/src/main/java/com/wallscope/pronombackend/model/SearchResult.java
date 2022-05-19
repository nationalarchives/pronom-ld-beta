package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.*;
import static java.util.Map.entry;

public class SearchResult implements RDFWritable {
    private static final String MATCH_CHAR_START = "↦"; // Unicode \u21a6
    private static final String MATCH_CHAR_END = "↤"; // Unicode \u21a4
    private static final Map<String, String> humanProperties = Map.ofEntries(
            entry(SKOS.notation, "PUID"),
            entry(SKOS.hiddenLabel, "File Extension(s)"),
            entry(PRONOM.FileFormat.Version, "Version"),
            entry(PRONOM.FileFormat.ReleaseDate, "Release Date"),
            entry(PRONOM.InternalSignature.LastUpdatedDate, "Last Updated"), // change data to make this global
            entry(PRONOM.FileFormat.LastUpdatedDate, "Last Updated")
    );
    private final Resource uri;
    private final Resource type;
    private final Float score;
    private final String match;
    private final List<Resource> fields;
    private final String name;
    private final String description;
    private final Map<String, RDFNode> properties;

    public SearchResult(Resource uri, Resource type, Float score, String match, List<Resource> field, String label, String description, Map<String, RDFNode> properties) {
        this.uri = uri;
        this.type = type;
        this.score = score;
        this.match = match;
        this.fields = field;
        this.name = label;
        this.description = description;
        this.properties = properties;
    }

    public Resource getUri() {
        return uri;
    }

    public Resource getType() {
        return type;
    }

    public Float getScore() {
        return score;
    }

    public String getMatch() {
        return match;
    }

    public List<Resource> getFields() {
        return fields;
    }

    public String getName() {
        return name;
    }

    public String getHTMLName() {
        if (fields.stream().map(Resource::getURI).collect(Collectors.toList()).contains(RDFS.label)) {
            return replaceMatch(getName());
        }
        return getName();
    }

    public String getDescription() {
        return description;
    }

    public String getHTMLDescription() {
        if (fields.stream().map(Resource::getURI).collect(Collectors.toList()).contains(RDFS.comment)) {
            return replaceMatch(getDescription());
        }
        return getDescription();
    }

    public Map<String, RDFNode> getProperties() {
        return properties;
    }

    public String getPuid() {
        return properties.getOrDefault(SKOS.notation, makeLiteral("")).asLiteral().getString();
    }

    public String getHTMLPuid() {
        if (fields.stream().map(Resource::getURI).collect(Collectors.toList()).contains(SKOS.notation)) {
            return replaceMatch(getPuid());
        }
        return getPuid();
    }

    private String replaceMatch(String str) {
        // Disabling highlighting but leaving the possibility open for future
//        String[] matches = match.split("\\|");
//        String result = str;
//        for (String m : matches) {
//            String clean = m.replaceAll(MATCH_CHAR_START, "").replaceAll(MATCH_CHAR_END, "");
//            String html = "<em class=\"highlight\">" + clean + "</em>";
//            result = result.replaceAll(Pattern.quote(clean), html);
//        }
//        return result;
        return str;
    }

    @Override
    public Resource getURI() {
        return uri;
    }

    @Override
    public Model toRDF() {
        return ModelFactory.createDefaultModel();
    }

    public String getStringType() {
        if (this.type == null) return "";
        String[] parts = this.type.getURI().split("/");
        return parts[parts.length - 1];
    }

    // Returns the last 2 parts of the uri, which allows us to link to the generic /id endpoint which will then redirect accordingly.
    public String getIdUri() {
        if (this.uri == null || this.uri.getURI().isBlank()) return "";
        String[] parts = this.uri.getURI().split("/");
        String id = parts[parts.length - 1];
        return getStringType() + "/" + id;
    }

    public Map<String, String> getHTMLProperties() {
        HashMap<String, String> human = new HashMap<>();
        for (Map.Entry<String, String> entry : humanProperties.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (properties.containsKey(k)) {
                String propVal = safelyGetStringOrNull(properties.get(k));
                if (propVal == null) continue;
                if (fields.stream().map(Resource::getURI).collect(Collectors.toList()).contains(k)) {
                    propVal = replaceMatch(propVal);
                }
                human.put(v, propVal);
            }
        }
        return human;
    }

    public Integer getPuidSortNumber() {
        String[] parts = getPuid().split("/");
        Integer number = null;
        try {
            number = Integer.parseInt(parts[parts.length - 1]);
        } catch (Exception ignored) {
            number = 99999;
        }
        return number;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "uri=" + uri +
                ", type=" + type +
                ", score=" + score +
                ", match='" + match + '\'' +
                ", fields=" + fields +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", properties=" + properties +
                '}';
    }

    public static class Deserializer implements RDFDeserializer<SearchResult> {
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
}
