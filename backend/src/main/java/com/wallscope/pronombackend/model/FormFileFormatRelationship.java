package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class FormFileFormatRelationship {
    private String uri;
    private String relationshipType;
    private String source;
    private String target;
    private String note;

    public FormFileFormatRelationship() {
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    @Override
    public String toString() {
        return "FormFileFormatRelationship{" +
                "uri='" + uri + '\'' +
                ", relationshipType='" + relationshipType + '\'' +
                ", source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public FileFormatRelationship toObject() {
        Resource relUri = makeResource(uri);
        return new FileFormatRelationship(relUri,
                makeResource(getRelationshipType()),
                null,
                null,
                makeResource(getSource()),
                null,
                makeResource(getTarget()),
                null,
                getNote()
        );
    }

    public boolean isNotEmpty() {
        return uri != null && !uri.isBlank()
                && source != null && !source.isBlank()
                && target != null && !target.isBlank()
                && relationshipType != null && !relationshipType.isBlank();
    }
}
