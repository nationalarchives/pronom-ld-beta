package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class FileFormat implements RDFWritable {
    Logger logger = LoggerFactory.getLogger(FileFormat.class);
    private final Resource uri;
    private final Integer puid;
    private final Resource puidType;
    private final String puidTypeName;
    private final String name;
    private final String description;
    private final Instant updated;
    private final String version;
    private final Boolean binaryFlag;
    private final Boolean withdrawnFlag;
    private final List<Classification> classifications;
    private final List<InternalSignature> internalSignatures;
    private final List<ExternalSignature> externalSignatures;
    private final List<Actor> developmentActors;
    private final List<Actor> supportActors;
    private final List<FileFormatRelationship> hasRelationships;

    public FileFormat(
            Resource uri,
            Integer puid,
            Resource puidType,
            String puidTypeName,
            String name,
            String description,
            Instant updated,
            String version,
            Boolean binaryFlag,
            Boolean withdrawnFlag,
            List<Classification> classifications,
            List<InternalSignature> internalSignatures,
            List<ExternalSignature> externalSignatures,
            List<Actor> developmentActors,
            List<Actor> supportActors,
            List<FileFormatRelationship> hasRelationships) {
        this.uri = uri;
        this.puid = puid;
        this.puidType = puidType;
        this.puidTypeName = puidTypeName;
        this.name = name;
        this.description = description;
        this.updated = updated;
        this.version = version;
        this.binaryFlag = binaryFlag;
        this.withdrawnFlag = withdrawnFlag;
        this.classifications = classifications;
        this.internalSignatures = internalSignatures;
        this.externalSignatures = externalSignatures;
        this.developmentActors = developmentActors;
        this.supportActors = supportActors;
        this.hasRelationships = hasRelationships;
    }

    public Resource getURI() {
        return uri;
    }

    public String getID() {
        String[] parts = uri.getURI().split("/");
        return parts[parts.length - 1];
    }

    public Integer getPuid() {
        return puid;
    }

    public Resource getPuidType() {
        return puidType;
    }

    public String getPuidTypeName() {
        return puidTypeName;
    }

    public String getFormattedPuid() {
        return puidTypeName + "/" + puid;
    }

    public String getFormattedMimeType() {
        return "MIME/type";
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getUpdated() {
        return updated;
    }

    public String getVersion() {
        return version;
    }

    public boolean isBinaryFlag() {
        return binaryFlag;
    }

    public boolean isWithdrawnFlag() {
        return withdrawnFlag;
    }

    public List<Classification> getClassifications() {
        return classifications;
    }

    public String getClassificationsListString() {
        return classifications.stream().map(Classification::getName).collect(Collectors.joining(", "));
    }

    public List<InternalSignature> getInternalSignatures() {
        return internalSignatures;
    }

    public List<ExternalSignature> getExternalSignatures() {
        return externalSignatures;
    }

    public List<FileFormatRelationship> getHasRelationships() {
        return hasRelationships;
    }

    public boolean getHasSignature() {
        return !(internalSignatures.isEmpty() && externalSignatures.isEmpty());
    }

    public String getFirstExtension() {
        return !externalSignatures.isEmpty() ? externalSignatures.get(0).getName() : null;
    }

    public List<FileFormatRelationship> getHasPriorityOver() {
        return hasRelationships.stream()
                .filter(r -> r.getRelationshipType().getURI().equals(PRONOM.FormatRelationshipType.PriorityOver))
                .collect(Collectors.toList());
    }

    public List<Actor> getDevelopmentActors() {
        return developmentActors;
    }

    public List<Actor> getSupportActors() {
        return supportActors;
    }

    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, makeProp(RDF.type), makeResource(PRONOM.FileFormat.type));
        m.add(uri, makeProp(PRONOM.FileFormat.Puid), makeLiteral(puid));
        m.add(uri, makeProp(PRONOM.FileFormat.PuidTypeId), puidType);
        m.add(uri, makeProp(RDFS.label), makeLiteral(name));
        m.add(uri, makeProp(RDFS.comment), makeLiteral(description));
        m.add(uri, makeProp(PRONOM.FileFormat.LastUpdatedDate), makeXSDDateTime(updated));
        m.add(uri, makeProp(PRONOM.FileFormat.Version), makeLiteral(version));
        if (binaryFlag != null) {
            m.add(uri, makeProp(PRONOM.FileFormat.BinaryFlag), makeLiteral(binaryFlag));
        }
        if (withdrawnFlag != null) {
            m.add(uri, makeProp(PRONOM.FileFormat.WithdrawnFlag), makeLiteral(withdrawnFlag));
        }
        return m;
    }

    // Boilerplate


    @Override
    public String toString() {
        return "FileFormat{" +
                "uri=" + uri +
                ", puid=" + puid +
                ", puidType=" + puidType +
                ", puidTypeName='" + puidTypeName + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", updated=" + updated +
                ", version='" + version + '\'' +
                ", binaryFlag=" + binaryFlag +
                ", withdrawnFlag=" + withdrawnFlag +
                ", classifications=" + classifications +
                ", internalSignatures=" + internalSignatures +
                ", externalSignatures=" + externalSignatures +
                ", developmentActors=" + developmentActors +
                ", supportActors=" + supportActors +
                ", hasRelationships=" + hasRelationships +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof FileFormat)) return false;
        FileFormat cast = (FileFormat) other;
        return this.toRDF().isIsomorphicWith(cast.toRDF());
    }
}
