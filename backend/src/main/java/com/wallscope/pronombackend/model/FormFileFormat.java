package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Resource;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class FormFileFormat {
    private String uri;
    private String puid;
    private String name;
    private String description;
    private String version;
    private Boolean binaryFlag;
    private Boolean withdrawnFlag;
    private String formatType;
    private String byteOrder;
    private String compressionType;
    private List<FormAlias> aliases;
    private List<FormFormatIdentifier> identifiers;
    private List<String> formatFamilies;
    private List<String> classifications;
    private List<FormInternalSignature> internalSignatures;
    private List<FormExternalSignature> externalSignatures;
    private List<FormContainerSignature> containerSignatures;
    private List<FormActor> developmentActors;
    private List<FormActor> supportActors;
    private List<FormFileFormatRelationship> hasPriorityOver;
    private List<FormFileFormatRelationship> hasRelationships;
    private FormSubmittedBy submittedBy;

    public FormFileFormat() {
        // We must initialise the nested fields otherwise we get null pointer exceptions in the template rendering
        internalSignatures = List.of(new FormInternalSignature());
        externalSignatures = List.of(new FormExternalSignature());
        containerSignatures = List.of();
        aliases = List.of(new FormAlias());
        identifiers = List.of(new FormFormatIdentifier());
        developmentActors = List.of(new FormActor());
        supportActors = List.of(new FormActor());
        hasRelationships = List.of(new FormFileFormatRelationship());
        hasPriorityOver = List.of(new FormFileFormatRelationship());
        submittedBy = new FormSubmittedBy();
    }

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getBinaryFlag() {
        return binaryFlag;
    }

    public void setBinaryFlag(Boolean binaryFlag) {
        this.binaryFlag = binaryFlag;
    }

    public Boolean getWithdrawnFlag() {
        return withdrawnFlag;
    }

    public void setWithdrawnFlag(Boolean withdrawnFlag) {
        this.withdrawnFlag = withdrawnFlag;
    }

    public List<String> getClassifications() {
        return classifications;
    }

    public void setClassifications(List<String> classifications) {
        this.classifications = classifications;
    }

    public List<FormInternalSignature> getInternalSignatures() {
        return internalSignatures;
    }

    public void setInternalSignatures(List<FormInternalSignature> internalSignatures) {
        this.internalSignatures = internalSignatures;
    }

    public List<FormExternalSignature> getExternalSignatures() {
        return externalSignatures;
    }

    public void setExternalSignatures(List<FormExternalSignature> externalSignatures) {
        this.externalSignatures = externalSignatures;
    }

    public List<FormActor> getDevelopmentActors() {
        return developmentActors;
    }

    public void setDevelopmentActors(List<FormActor> developmentActors) {
        this.developmentActors = developmentActors;
    }

    public List<FormActor> getSupportActors() {
        return supportActors;
    }

    public void setSupportActors(List<FormActor> supportActors) {
        this.supportActors = supportActors;
    }

    public List<FormFileFormatRelationship> getHasRelationships() {
        return hasRelationships;
    }

    public void setHasRelationships(List<FormFileFormatRelationship> hasRelationships) {
        this.hasRelationships = hasRelationships;
    }

    public FormSubmittedBy getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(FormSubmittedBy submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<FormFormatIdentifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<FormFormatIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    public List<FormAlias> getAliases() {
        return aliases;
    }

    public void setAliases(List<FormAlias> aliases) {
        this.aliases = aliases;
    }

    public List<FormFileFormatRelationship> getHasPriorityOver() {
        return hasPriorityOver;
    }

    public void setHasPriorityOver(List<FormFileFormatRelationship> hasPriorityOver) {
        this.hasPriorityOver = hasPriorityOver;
    }

    public List<String> getFormatFamilies() {
        return formatFamilies;
    }

    public void setFormatFamilies(List<String> formatFamilies) {
        this.formatFamilies = formatFamilies;
    }

    public String getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(String byteOrder) {
        this.byteOrder = byteOrder;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }

    public FileFormat toObject(Integer puid, Resource puidType, Instant updated, List<Classification> classifications) {
        Resource ffUri = makeResource(uri);
        return new FileFormat(ffUri,
                puid,
                puidType,
                null,
                getName(),
                getDescription(),
                updated,
                getVersion(),
                getBinaryFlag(),
                getWithdrawnFlag(),
                classifications,
                getInternalSignatures().stream().map(is -> is.toObject(ffUri, updated)).collect(Collectors.toList()),
                getExternalSignatures().stream().map(FormExternalSignature::toObject).collect(Collectors.toList()),
                getContainerSignatures().stream().map(cs -> cs.toObject(ffUri)).collect(Collectors.toList()),
                getIdentifiers().stream().map(FormFormatIdentifier::toObject).collect(Collectors.toList()),
                getDevelopmentActors().stream().map(FormActor::toObject).collect(Collectors.toList()),
                getSupportActors().stream().map(FormActor::toObject).collect(Collectors.toList()),
                getHasRelationships().stream().map(FormFileFormatRelationship::toObject).collect(Collectors.toList())
        );
    }

    public List<FormContainerSignature> getContainerSignatures() {
        return containerSignatures;
    }

    public void setContainerSignatures(List<FormContainerSignature> containerSignatures) {
        this.containerSignatures = containerSignatures;
    }

    public void randomizeURIs() {
        // Set own URI
        setUri(PRONOM.FileFormat.id + UUID.randomUUID());
        if (submittedBy != null) submittedBy.setUri(PRONOM.Submitter.id + UUID.randomUUID());
        if (internalSignatures != null) internalSignatures.forEach(fis -> {
            fis.setUri(PRONOM.InternalSignature.id + UUID.randomUUID());
            if (fis.getByteSequences() != null)
                fis.getByteSequences().forEach(fbs -> fbs.setUri(PRONOM.ByteSequence.id + UUID.randomUUID()));
        });
        if (containerSignatures != null) containerSignatures.forEach(fcs -> {
            fcs.setUri(PRONOM.ContainerSignature.id + UUID.randomUUID());
            if (fcs.getFiles() != null) fcs.getFiles().forEach(fcf -> {
                fcf.setUri(PRONOM.ContainerFile.id + UUID.randomUUID());
                if (fcf.getByteSequences() != null)
                    fcf.getByteSequences().forEach(fbs -> fbs.setUri(PRONOM.ByteSequence.id + UUID.randomUUID()));
            });
        });
        if (externalSignatures != null)
            externalSignatures.forEach(fes -> fes.setUri(PRONOM.ExternalSignature.id + UUID.randomUUID()));
    }

    @Override
    public String toString() {
        return "FormFileFormat{" +
                "uri='" + uri + '\'' +
                ", puid='" + puid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", version='" + version + '\'' +
                ", binaryFlag=" + binaryFlag +
                ", withdrawnFlag=" + withdrawnFlag +
                ", formatType='" + formatType + '\'' +
                ", byteOrder='" + byteOrder + '\'' +
                ", compressionType='" + compressionType + '\'' +
                ", aliases=" + aliases +
                ", identifiers=" + identifiers +
                ", formatFamilies=" + formatFamilies +
                ", classifications=" + classifications +
                ", internalSignatures=" + internalSignatures +
                ", externalSignatures=" + externalSignatures +
                ", developmentActors=" + developmentActors +
                ", supportActors=" + supportActors +
                ", hasPriorityOver=" + hasPriorityOver +
                ", hasRelationships=" + hasRelationships +
                ", submittedBy=" + submittedBy +
                '}';
    }

    public static FormFileFormat convert(FileFormat f) {
        FormFileFormat ff = new FormFileFormat();
        ff.setUri(safelyGetUriOrNull(f.getURI()));
        ff.setPuid(f.getFormattedPuid());
        ff.setName(f.getName());
        ff.setDescription(f.getDescription());
        ff.setVersion(f.getVersion());
        ff.setBinaryFlag(f.isBinaryFlag());
        ff.setWithdrawnFlag(f.isWithdrawnFlag());
        ff.setClassifications(f.getClassifications().stream().map(Classification::getId).collect(Collectors.toList()));
        // Internal Signatures
        ff.setInternalSignatures(f.getInternalSignatures().stream().map(is -> {
            FormInternalSignature fis = FormInternalSignature.convert(is);
            fis.setFileFormat(f.getFormattedPuid());
            return fis;
        }).collect(Collectors.toList()));
        // External Signatures
        ff.setExternalSignatures(f.getExternalSignatures().stream().map(es -> {
            FormExternalSignature fes = FormExternalSignature.convert(es);
            fes.setFileFormat(f.getFormattedPuid());
            return fes;
        }).collect(Collectors.toList()));
        // Container Signatures
        ff.setContainerSignatures(f.getContainerSignatures().stream().map(cs -> {
            FormContainerSignature fcs = FormContainerSignature.convert(cs);
            fcs.setFileFormat(f.getFormattedPuid());
            return fcs;
        }).collect(Collectors.toList()));
        ff.setHasRelationships(f.getHasRelationships().stream().map(FileFormatRelationship::convert).collect(Collectors.toList()));
        return ff;
    }
}
