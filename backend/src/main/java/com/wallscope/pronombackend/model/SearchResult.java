package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
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
        String[] matches = match.split("\\|");
        String result = str;
        for (String m : matches) {
            String clean = m.replaceAll(MATCH_CHAR_START, "").replaceAll(MATCH_CHAR_END, "");
            String html = "<em class=\"highlight\">" + clean + "</em>";
            result = result.replaceAll(Pattern.quote(clean), html);
        }
        return result;
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
}
