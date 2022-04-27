package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileFormatRelationship implements RDFWritable {
    Logger logger = LoggerFactory.getLogger(FileFormatRelationship.class);

    private final Resource uri;
    private final Resource relationshipType;
    private final String relationshipTypeName;
    private final String relationshipInverseTypeName;
    private final Resource source;
    private final String sourceName;
    private final String targetName;
    private final Resource target;
    private final String note;

    public FileFormatRelationship(Resource uri, Resource relationshipType, String relationshipTypeName, String relationshipInverseTypeName, Resource source, String sourceName, Resource target, String targetName, String note) {
        this.uri = uri;
        this.relationshipType = relationshipType;
        this.relationshipTypeName = relationshipTypeName;
        this.relationshipInverseTypeName = relationshipInverseTypeName;
        this.source = source;
        this.sourceName = sourceName;
        this.target = target;
        this.targetName = targetName;
        this.note = note;
    }

    public Resource getRelationshipType() {
        return relationshipType;
    }

    public Resource getSource() {
        return source;
    }

    public Resource getTarget() {
        return target;
    }

    public String getRelationshipTypeName() {
        return relationshipTypeName;
    }

    public String getID() {
        String[] parts = uri.getURI().split("/");
        return parts[parts.length - 1];
    }

    @Override
    public Resource getURI() {
        return uri;
    }

    @Override
    public Model toRDF() {
        return null;
    }

    public String getNote() {
        return note;
    }

    public String getRelationshipInverseTypeName() {
        return relationshipInverseTypeName;
    }

    public String getSourceID() {
        if (source == null || source.getURI().isBlank()) return null;
        String[] parts = source.getURI().split("/");
        return parts[parts.length - 1];
    }

    public String getTargetID() {
        if (target == null || target.getURI().isBlank()) return null;
        String[] parts = target.getURI().split("/");
        return parts[parts.length - 1];
    }

    @Override
    public String toString() {
        return "FileFormatRelationship{" +
                "uri=" + uri +
                ", relationshipType=" + relationshipType +
                ", relationshipTypeName='" + relationshipTypeName + '\'' +
                ", relationshipInverseTypeName='" + relationshipInverseTypeName + '\'' +
                ", source=" + source +
                ", target=" + target +
                ", note='" + note + '\'' +
                '}';
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getTargetName() {
        return targetName;
    }
}
