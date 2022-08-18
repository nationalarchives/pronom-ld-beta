package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
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
    private final Instant releaseDate;
    private final Instant withdrawnDate;
    private final String version;
    private final Boolean binaryFlag;
    private final Boolean withdrawnFlag;
    private final List<Resource> byteOrder;
    private final List<Documentation> references;
    private final List<LabeledURI> classifications;
    private final List<InternalSignature> internalSignatures;
    private final List<ExternalSignature> externalSignatures;
    private final List<ContainerSignature> containerSignatures;
    private final List<FormatIdentifier> formatIdentifiers;
    private final List<Actor> developmentActors;
    private final List<Actor> supportActors;
    private final List<FileFormatRelationship> hasRelationships;
    private final List<LabeledURI> formatFamilies;
    private final List<FormatAlias> aliases;

    public FileFormat(
            Resource uri,
            Integer puid,
            Resource puidType,
            String puidTypeName,
            String name,
            String description,
            Instant updated,
            Instant releaseDate,
            Instant withdrawnDate,
            String version,
            Boolean binaryFlag,
            Boolean withdrawnFlag,
            List<Resource> byteOrder,
            List<Documentation> references,
            List<LabeledURI> classifications,
            List<InternalSignature> internalSignatures,
            List<ExternalSignature> externalSignatures,
            List<ContainerSignature> containerSignatures,
            List<FormatIdentifier> formatIdentifiers,
            List<Actor> developmentActors,
            List<Actor> supportActors,
            List<FileFormatRelationship> hasRelationships,
            List<LabeledURI> formatFamilies, List<FormatAlias> aliases) {
        this.uri = uri;
        this.puid = puid;
        this.puidType = puidType;
        this.puidTypeName = puidTypeName;
        this.name = name;
        this.description = description;
        this.updated = updated;
        this.releaseDate = releaseDate;
        this.withdrawnDate = withdrawnDate;
        this.version = version;
        this.binaryFlag = binaryFlag;
        this.withdrawnFlag = withdrawnFlag;
        this.byteOrder = byteOrder;
        this.references = references;
        this.classifications = classifications;
        this.internalSignatures = internalSignatures;
        this.externalSignatures = externalSignatures;
        this.containerSignatures = containerSignatures;
        this.formatIdentifiers = formatIdentifiers;
        this.developmentActors = developmentActors;
        this.supportActors = supportActors;
        this.hasRelationships = hasRelationships;
        this.formatFamilies = formatFamilies;
        this.aliases = aliases;
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
        if (puid == null || puidTypeName == null) return null;
        return puidTypeName + "/" + puid;
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
    public Instant getReleaseDate() {
        return releaseDate;
    }
    public Instant getWithdrawnDate() {
        return withdrawnDate;
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

    public List<Resource> getByteOrder() {
        return byteOrder;
    }

    public List<LabeledURI> getClassifications() {
        return classifications;
    }

    public String getClassificationsListString() {
        return classifications.stream().map(LabeledURI::getLabel).collect(Collectors.joining(", "));
    }

    public List<InternalSignature> getInternalSignatures() {
        return internalSignatures;
    }

    public List<ExternalSignature> getExternalSignatures() {
        return externalSignatures;
    }

    public List<ContainerSignature> getContainerSignatures() {
        return containerSignatures;
    }

    public List<FileFormatRelationship> getHasRelationships() {
        return hasRelationships.stream()
                .filter(r -> !r.getRelationshipType().getURI().equals(PRONOM.FormatRelationshipType.PriorityOver))
                .collect(Collectors.toList());
    }

    public List<FormatIdentifier> getFormatIdentifiers() {
        return formatIdentifiers;
    }

    public boolean getHasSignature() {
        return !(internalSignatures.isEmpty() && externalSignatures.isEmpty() && containerSignatures.isEmpty());
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

    public String getDevelopmentActorsList() {
        return developmentActors.stream().map(Actor::getDisplayName).collect(Collectors.joining(", "));
    }

    public List<Actor> getSupportActors() {
        return supportActors;
    }

    public String getSupportActorsList() {
        return supportActors.stream().map(Actor::getDisplayName).collect(Collectors.joining(", "));
    }

    public String getFormatFamiliesList() {
        return formatFamilies.stream().map(LabeledURI::getLabel).collect(Collectors.joining(", "));
    }

    public List<Documentation> getReferences() {
        return references;
    }

    public List<LabeledURI> getFormatFamilies() {
        return formatFamilies;
    }

    public List<FormatAlias> getAliases() {
        return aliases;
    }

    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, makeProp(RDF.type), makeResource(PRONOM.FileFormat.type));
        if (puid != null) {
            m.add(uri, makeProp(PRONOM.FileFormat.Puid), makeLiteral(puid));
            m.add(uri, makeProp(PRONOM.Global.Puid), makeLiteral(puid));
        }
        if (puidType != null) {
            m.add(uri, makeProp(PRONOM.FileFormat.PuidTypeId), puidType);
            m.add(uri, makeProp(PRONOM.Global.PuidTypeId), puidType);
        }
        // enable search by full puid
        if (getFormattedPuid() != null) {
            m.add(uri, makeProp(SKOS.notation), getFormattedPuid());
        }
        if (name != null) m.add(uri, makeProp(RDFS.label), makeLiteral(name));
        if (description != null) m.add(uri, makeProp(RDFS.comment), makeLiteral(description));
        if (updated != null) m.add(uri, makeProp(PRONOM.FileFormat.LastUpdatedDate), makeXSDDateTime(updated));
        if (releaseDate != null) m.add(uri, makeProp(PRONOM.FileFormat.ReleaseDate), makeXSDDateTime(releaseDate));
        if (withdrawnDate != null) m.add(uri, makeProp(PRONOM.FileFormat.WithdrawnDate), makeXSDDateTime(withdrawnDate));
        if (version != null) m.add(uri, makeProp(PRONOM.FileFormat.Version), makeLiteral(version));
        if (binaryFlag != null) m.add(uri, makeProp(PRONOM.FileFormat.BinaryFlag), makeLiteral(binaryFlag));
        if (withdrawnFlag != null) m.add(uri, makeProp(PRONOM.FileFormat.WithdrawnFlag), makeLiteral(withdrawnFlag));

        if (byteOrder != null) byteOrder.forEach(bo -> m.add(uri, makeProp(PRONOM.FileFormat.ByteOrder), bo));
        if (classifications != null) {
            classifications.forEach(c -> m.add(uri, makeProp(PRONOM.FileFormat.Classification), c.getUri()));
        }

        if (references != null) {
            references.forEach(r -> {
                m.add(uri, makeProp(PRONOM.FileFormat.Documentation), r.getURI());
                m.add(r.toRDF());
            });
        }

        if (internalSignatures != null) {
            internalSignatures.forEach(is -> {
                m.add(uri, makeProp(PRONOM.FileFormat.InternalSignature), is.getURI());
                m.add(is.toRDF());
            });
        }
        if (externalSignatures != null) {
            List<String> extensions = new ArrayList<>();
            externalSignatures.forEach(es -> {
                m.add(uri, makeProp(PRONOM.FileFormat.ExternalSignature), es.getURI());
                m.add(es.toRDF());
                if (es.getSignatureType().equals("File extension")) {
                    extensions.add(es.getName());
                }
            });
            if (!extensions.isEmpty()) {
                m.add(uri, makeProp(SKOS.hiddenLabel), String.join(" ", extensions));
            }
        }
        if (containerSignatures != null) {
            containerSignatures.forEach(cs -> {
                m.add(uri, makeProp(PRONOM.FileFormat.ContainerSignature), cs.getURI());
                m.add(cs.toRDF());
            });
        }
        if (formatIdentifiers != null) {
            formatIdentifiers.forEach(fi -> {
                m.add(fi.getURI(), makeProp(PRONOM.FormatIdentifier.FileFormat), uri);
                m.add(fi.toRDF());
            });
        }
        if (developmentActors != null) {
            developmentActors.forEach(x -> m.add(uri, makeProp(PRONOM.FileFormat.DevelopedBy), x.getURI()));
        }
        if (supportActors != null) {
            supportActors.forEach(x -> m.add(uri, makeProp(PRONOM.FileFormat.SupportedBy), x.getURI()));
        }
        if (hasRelationships != null) {
            hasRelationships.forEach(rel -> {
                m.add(uri, makeProp(PRONOM.FileFormat.InFileFormatRelationship), rel.getURI());
                m.add(rel.toRDF());
            });
        }
        if (formatFamilies != null) {
            formatFamilies.forEach(fam -> m.add(uri, makeProp(PRONOM.FileFormat.FormatFamily), fam.getUri()));
        }
        if (aliases != null) {
            aliases.forEach(a -> {
                m.add(uri, makeProp(PRONOM.FileFormat.Alias), a.getURI());
                m.add(a.toRDF());
            });
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
                ", releaseDate=" + releaseDate +
                ", withdrawnDate=" + withdrawnDate +
                ", version='" + version + '\'' +
                ", binaryFlag=" + binaryFlag +
                ", withdrawnFlag=" + withdrawnFlag +
                ", byteOrder=" + byteOrder +
                ", references=" + references +
                ", classifications=" + classifications +
                ", internalSignatures=" + internalSignatures +
                ", externalSignatures=" + externalSignatures +
                ", containerSignatures=" + containerSignatures +
                ", formatIdentifiers=" + formatIdentifiers +
                ", developmentActors=" + developmentActors +
                ", supportActors=" + supportActors +
                ", hasRelationships=" + hasRelationships +
                ", formatFamilies=" + formatFamilies +
                ", aliases=" + aliases +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof FileFormat)) return false;
        FileFormat cast = (FileFormat) other;
        return this.toRDF().isIsomorphicWith(cast.toRDF());
    }

    public static class Deserializer implements RDFDeserializer<FileFormat> {
        Logger logger = LoggerFactory.getLogger(FileFormat.Deserializer.class);

        public Deserializer() {
        }

        public Resource getRDFType() {
            return makeResource(PRONOM.FileFormat.type);
        }

        public FileFormat fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);

            Integer puid = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.Puid)));
            Resource puidType = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.PuidTypeId)));
            String puidTypeName = safelyGetStringOrNull(mu.getOneObjectOrNull(puidType, makeProp(RDFS.label)));
            String name = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(RDFS.label)));
            String description = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(RDFS.comment)));
            Instant updated = safelyParseDateOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.LastUpdatedDate)));
            Instant releaseDate = safelyParseDateOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.ReleaseDate)));
            Instant withdrawnDate = safelyParseDateOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.WithdrawnDate)));
            String version = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.Version)));
            Boolean binaryFlag = safelyGetBooleanOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.BinaryFlag)));
            Boolean withdrawnFlag = safelyGetBooleanOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.WithdrawnFlag)));

            List<LabeledURI> classifications = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.Classification)).stream()
                    .map(node -> new LabeledURI(node.asResource(), safelyGetStringOrNull(mu.getOneObjectOrNull(node.asResource(), makeProp(RDFS.label)))))
                    .collect(Collectors.toList());

            // ByteOrder
            List<Resource> byteOrders = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.ByteOrder)).stream().map(RDFNode::asResource).collect(Collectors.toList());
            // Documents
            List<Resource> refSubjects = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.Documentation)).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<Documentation> references = mu.buildFromModel(new Documentation.Deserializer(), refSubjects);
            // InternalSignature
            List<Resource> intSigSubjects = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.InternalSignature)).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<InternalSignature> internalSignatures = mu.buildFromModel(new InternalSignature.Deserializer(), intSigSubjects);
            // ExternalSignature
            List<Resource> extSigSubjects = mu.getAllSubjects(makeProp(PRONOM.ExternalSignature.FileFormat), uri).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<ExternalSignature> externalSignatures = mu.buildFromModel(new ExternalSignature.Deserializer(), extSigSubjects);
            // ContainerSignature
            List<Resource> contSigSubjects = mu.getAllSubjects(makeProp(PRONOM.ContainerSignature.FileFormat), uri).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<ContainerSignature> containerSignatures = mu.buildFromModel(new ContainerSignature.Deserializer(), contSigSubjects);
            // FormatIdentifier
            List<Resource> fIdSubjects = mu.getAllSubjects(makeProp(PRONOM.FormatIdentifier.FileFormat), uri).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<FormatIdentifier> formatIdentifiers = mu.buildFromModel(new FormatIdentifier.Deserializer(), fIdSubjects);
            // FileFormatRelationship
            List<Resource> relationshipSubjects = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.InFileFormatRelationship)).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<FileFormatRelationship> hasRelationships = mu.buildFromModel(new FileFormatRelationship.Deserializer(), relationshipSubjects);
            // Development Actors
            List<Resource> devActSubs = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.DevelopedBy)).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<Actor> developmentActors = mu.buildFromModel(new Actor.Deserializer(), devActSubs);
            // Support Actors
            List<Resource> supActSubs = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.SupportedBy)).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<Actor> supportActors = mu.buildFromModel(new Actor.Deserializer(), supActSubs);
            // Format families
            List<LabeledURI> formatFamilies = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.FormatFamily)).stream()
                    .map(n -> new LabeledURI(n.asResource(), safelyGetStringOrNull(mu.getOneObjectOrNull(n.asResource(), makeProp(RDFS.label)))))
                    .collect(Collectors.toList());
            // Format Aliases
            List<Resource> aliasSubjects = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.Alias)).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<FormatAlias> aliases = mu.buildFromModel(new FormatAlias.Deserializer(), aliasSubjects);
            return new FileFormat(uri,
                    puid,
                    puidType,
                    puidTypeName,
                    name,
                    description,
                    updated,
                    releaseDate,
                    withdrawnDate,
                    version,
                    binaryFlag,
                    withdrawnFlag,
                    byteOrders,
                    references,
                    classifications,
                    internalSignatures,
                    externalSignatures,
                    containerSignatures,
                    formatIdentifiers,
                    developmentActors,
                    supportActors,
                    hasRelationships,
                    formatFamilies,
                    aliases);
        }
    }
}
