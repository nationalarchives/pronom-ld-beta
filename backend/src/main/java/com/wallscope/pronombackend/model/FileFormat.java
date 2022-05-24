package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Collections;
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
    private final List<FormatIdentifier> formatIdentifiers;
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
            List<FormatIdentifier> formatIdentifiers, List<Actor> developmentActors,
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
        this.formatIdentifiers = formatIdentifiers;
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

    public Boolean isBinaryFlag() {
        return binaryFlag;
    }

    public Boolean isWithdrawnFlag() {
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

    public List<FormatIdentifier> getFormatIdentifiers() {
        return formatIdentifiers;
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

    public String getMIMETypeList() {
        return formatIdentifiers.stream()
                .filter(id -> id.getType().getURI().equals(PRONOM.FormatIdentifierType.MIME))
                .map(FormatIdentifier::getName)
                .collect(Collectors.joining(", "));
    }

    public List<FormatIdentifier> getOtherIdentifiers() {
        return formatIdentifiers.stream()
                .filter(id -> !id.getType().getURI().equals(PRONOM.FormatIdentifierType.MIME))
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
                ", formatIdentifiers=" + formatIdentifiers +
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

    public static class Deserializer implements RDFDeserializer<FileFormat> {

        public Deserializer() {
        }

        public Resource getRDFType() {
            return makeResource(PRONOM.FileFormat.type);
        }

        public FileFormat fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            // Required
            Integer puid = mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.Puid)).asLiteral().getInt();
            Resource puidType = mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.PuidTypeId)).asResource();
            String puidTypeName = mu.getOneObjectOrNull(puidType, makeProp(RDFS.label)).asLiteral().getString();
            String name = mu.getOneObjectOrNull(uri, makeProp(RDFS.label)).asLiteral().getString();
            String description = mu.getOneObjectOrNull(uri, makeProp(RDFS.comment)).asLiteral().getString();
            Instant updated = parseDate(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.LastUpdatedDate)).asLiteral());
            // Optional
            String version = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.Version)));
            Boolean binaryFlag = safelyGetBooleanOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.BinaryFlag)));
            Boolean withdrawnFlag = safelyGetBooleanOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.WithdrawnFlag)));

            List<Classification> classifications = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.Classification)).stream().map(node -> {
                Resource res = node.asResource();
                String[] parts = res.getURI().split("/");
                String id = parts[parts.length - 1];
                String label = mu.getOneObjectOrNull(res, makeProp(RDFS.label)).asLiteral().getString();
                return new Classification(id, label);
            }).collect(Collectors.toList());

            // InternalSignature
            List<Resource> intSigSubjects = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.InternalSignature)).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<InternalSignature> internalSignatures = mu.buildFromModel(new InternalSignature.Deserializer(), intSigSubjects);
            // ExternalSignature
            List<Resource> extSigSubjects = mu.getAllSubjects(makeProp(PRONOM.ExternalSignature.FileFormat), uri).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<ExternalSignature> externalSignatures = mu.buildFromModel(new ExternalSignature.Deserializer(), extSigSubjects);
            // FormatIdentifier
            List<Resource> fIdSubjects = mu.getAllSubjects(makeProp(PRONOM.FormatIdentifier.FileFormat), uri).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<FormatIdentifier> formatIdentifiers = mu.buildFromModel(new FormatIdentifier.Deserializer(), fIdSubjects);
            // FileFormatRelationship
            List<Resource> relationshipSubjects = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.InFileFormatRelationship)).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<FileFormatRelationship> hasRelationships = mu.buildFromModel(new FileFormatRelationship.Deserializer(), relationshipSubjects);
            // TODO: Create actors connection
            List<Actor> developmentActors = Collections.emptyList();
            List<Actor> supportActors = Collections.emptyList();
            return new FileFormat(uri,
                    puid,
                    puidType,
                    puidTypeName,
                    name,
                    description,
                    updated,
                    version,
                    binaryFlag,
                    withdrawnFlag,
                    classifications,
                    internalSignatures,
                    externalSignatures,
                    formatIdentifiers,
                    developmentActors,
                    supportActors,
                    hasRelationships);
        }
    }
}
