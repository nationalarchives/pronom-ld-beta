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
    private final List<Resource> byteOrder;
    private final List<Documentation> references;
    private final List<Classification> classifications;
    private final List<InternalSignature> internalSignatures;
    private final List<ExternalSignature> externalSignatures;
    private final List<ContainerSignature> containerSignatures;
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
            List<Resource> byteOrder,
            List<Documentation> references,
            List<Classification> classifications,
            List<InternalSignature> internalSignatures,
            List<ExternalSignature> externalSignatures,
            List<ContainerSignature> containerSignatures,
            List<FormatIdentifier> formatIdentifiers,
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

    public List<Resource> getByteOrder() {
        return byteOrder;
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

    public List<ContainerSignature> getContainerSignatures() {
        return containerSignatures;
    }

    public List<FileFormatRelationship> getHasRelationships() {
        return hasRelationships;
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

    public List<Actor> getSupportActors() {
        return supportActors;
    }

    public List<Documentation> getReferences() {
        return references;
    }


    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, makeProp(RDF.type), makeResource(PRONOM.FileFormat.type));
        if (puid != null) m.add(uri, makeProp(PRONOM.FileFormat.Puid), makeLiteral(puid));
        if (puidType != null) m.add(uri, makeProp(PRONOM.FileFormat.PuidTypeId), puidType);
        if (name != null) m.add(uri, makeProp(RDFS.label), makeLiteral(name));
        if (description != null) m.add(uri, makeProp(RDFS.comment), makeLiteral(description));
        if (updated != null) m.add(uri, makeProp(PRONOM.FileFormat.LastUpdatedDate), makeXSDDateTime(updated));
        if (version != null) m.add(uri, makeProp(PRONOM.FileFormat.Version), makeLiteral(version));
        if (binaryFlag != null) m.add(uri, makeProp(PRONOM.FileFormat.BinaryFlag), makeLiteral(binaryFlag));
        if (withdrawnFlag != null) m.add(uri, makeProp(PRONOM.FileFormat.WithdrawnFlag), makeLiteral(withdrawnFlag));

        if (byteOrder != null) byteOrder.forEach(bo -> m.add(uri, makeProp(PRONOM.FileFormat.ByteOrder), bo));
        if (classifications != null) {
            classifications.forEach(c -> m.add(uri, makeProp(PRONOM.FileFormat.Classification), c.getURI()));
        }

        if (references != null) {
            references.forEach(r -> {
                m.add(uri, makeProp(PRONOM.FileFormat.Reference), r.getURI());
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
            externalSignatures.forEach(es -> {
                m.add(uri, makeProp(PRONOM.FileFormat.ExternalSignature), es.getURI());
                m.add(es.toRDF());
            });
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
//        if (developmentActors != null) {
//            developmentActors.forEach(x -> m.add(uri, makeProp(), x))
//        }
//        if (supportActors != null) {
//            supportActors.forEach(x -> m.add(uri, makeProp(), x))
//        }
        if (hasRelationships != null) {
            hasRelationships.forEach(rel -> {
                m.add(uri, makeProp(PRONOM.FileFormat.InFileFormatRelationship), rel.getURI());
                m.add(rel.toRDF());
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
                ", version='" + version + '\'' +
                ", binaryFlag=" + binaryFlag +
                ", withdrawnFlag=" + withdrawnFlag +
                ", classifications=" + classifications +
                ", internalSignatures=" + internalSignatures +
                ", externalSignatures=" + externalSignatures +
                ", containerSignatures=" + containerSignatures +
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

            Integer puid = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.Puid)));
            Resource puidType = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.PuidTypeId)));
            String puidTypeName = safelyGetStringOrNull(mu.getOneObjectOrNull(puidType, makeProp(RDFS.label)));
            String name = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(RDFS.label)));
            String description = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(RDFS.comment)));
            Instant updated = safelyParseDateOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.LastUpdatedDate)));
            String version = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.Version)));
            Boolean binaryFlag = safelyGetBooleanOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.BinaryFlag)));
            Boolean withdrawnFlag = safelyGetBooleanOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.WithdrawnFlag)));

            List<Classification> classifications = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.Classification)).stream().map(node -> {
                Resource res = node.asResource();
                String label = mu.getOneObjectOrNull(res, makeProp(RDFS.label)).asLiteral().getString();
                return new Classification(res, label);
            }).collect(Collectors.toList());

            // ByteOrder
            List<Resource> byteOrders = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.ByteOrder)).stream().map(RDFNode::asResource).collect(Collectors.toList());
            // Documents
            List<Resource> refSubjects = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.Reference)).stream().map(RDFNode::asResource).collect(Collectors.toList());
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
                    byteOrders,
                    references,
                    classifications,
                    internalSignatures,
                    externalSignatures,
                    containerSignatures,
                    formatIdentifiers,
                    developmentActors,
                    supportActors,
                    hasRelationships);
        }
    }
}
