package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Resource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
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
    private List<FormLabeledURI> formatFamilies;
    private List<String> classifications;
    private List<FormInternalSignature> internalSignatures;
    private List<FormExternalSignature> externalSignatures;
    private List<FormContainerSignature> containerSignatures;
    private List<FormActor> developmentActors;
    private List<FormActor> supportActors;
    private List<FormFileFormatRelationship> hasPriorityOver;
    private List<FormFileFormatRelationship> hasRelationships;
    private FormSubmittedBy submittedBy;
    private List<FormDocumentation> references;

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
        references = List.of(new FormDocumentation());
        formatFamilies = List.of(new FormLabeledURI());
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

    public List<FormLabeledURI> getFormatFamilies() {
        return formatFamilies;
    }

    public void setFormatFamilies(List<FormLabeledURI> formatFamilies) {
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

    public List<FormDocumentation> getReferences() {
        return references;
    }

    public void setReferences(List<FormDocumentation> references) {
        this.references = references;
    }

    public List<FormContainerSignature> getContainerSignatures() {
        return containerSignatures;
    }

    public void setContainerSignatures(List<FormContainerSignature> containerSignatures) {
        this.containerSignatures = containerSignatures;
    }

    public FileFormat toObject(Integer puid, Resource puidType, Instant updated, List<LabeledURI> classifications) {
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
                List.of(makeResource(getByteOrder())),
                getReferences().stream().map(FormDocumentation::toObject).collect(Collectors.toList()),
                classifications,
                getInternalSignatures().stream().map(is -> is.toObject(ffUri, updated)).collect(Collectors.toList()),
                getExternalSignatures().stream().map(FormExternalSignature::toObject).collect(Collectors.toList()),
                getContainerSignatures().stream().map(cs -> cs.toObject(ffUri)).collect(Collectors.toList()),
                getIdentifiers().stream().map(FormFormatIdentifier::toObject).collect(Collectors.toList()),
                getDevelopmentActors().stream().map(FormActor::toObject).collect(Collectors.toList()),
                getSupportActors().stream().map(FormActor::toObject).collect(Collectors.toList()),
                getHasRelationships().stream().map(FormFileFormatRelationship::toObject).collect(Collectors.toList()),
                getFormatFamilies().stream().map(fff -> new LabeledURI(makeResource(fff.getUri()), fff.getLabel())).collect(Collectors.toList()),
                getAliases().stream().map(FormAlias::toObject).collect(Collectors.toList()));
    }

    public void randomizeURIs() {
        // Set own URI
        setUri(PRONOM.TentativeFileFormat.id + UUID.randomUUID());
        if (submittedBy != null) submittedBy.setUri(PRONOM.Contributor.id + UUID.randomUUID());
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
        if (identifiers != null) identifiers.forEach(ffi -> ffi.setUri(PRONOM.FormatIdentifier.id + UUID.randomUUID()));
        if (hasPriorityOver != null)
            hasPriorityOver.forEach(fhr -> fhr.setUri(PRONOM.FileFormatRelationship.id + UUID.randomUUID()));
        if (hasRelationships != null)
            hasRelationships.forEach(fhr -> fhr.setUri(PRONOM.FileFormatRelationship.id + UUID.randomUUID()));
        if (references != null) references.forEach(fr -> fr.setUri(PRONOM.Documentation.id + UUID.randomUUID()));
        if (formatFamilies != null)
            formatFamilies.forEach(fff -> fff.setUri(PRONOM.FileFormatFamily.id + UUID.randomUUID()));
        if (aliases != null) aliases.forEach(fa -> fa.setUri(PRONOM.FormatAlias.id + UUID.randomUUID()));
    }

    public void removeEmpties() {
        if (byteOrder != null)
            byteOrder = List.of(PRONOM.ByteOrder.littleEndian, PRONOM.ByteOrder.bigEndian).contains(byteOrder) ? byteOrder : null;
        if (classifications != null)
            classifications = classifications.stream().filter(c -> !c.isBlank()).collect(Collectors.toList());
        if (internalSignatures != null) internalSignatures = internalSignatures.stream().filter(is -> {
            is.removeEmpties();
            return is.isNotEmpty();
        }).collect(Collectors.toList());
        if (containerSignatures != null) containerSignatures = containerSignatures.stream().filter(cs -> {
            cs.removeEmpties();
            return cs.isNotEmpty();
        }).collect(Collectors.toList());
        if (externalSignatures != null)
            externalSignatures = externalSignatures.stream().filter(FormExternalSignature::isNotEmpty).collect(Collectors.toList());
        if (hasPriorityOver != null)
            hasPriorityOver = hasPriorityOver.stream().filter(FormFileFormatRelationship::isNotEmpty).collect(Collectors.toList());
        if (hasRelationships != null)
            hasRelationships = hasRelationships.stream().filter(FormFileFormatRelationship::isNotEmpty).collect(Collectors.toList());
        if (identifiers != null)
            identifiers = identifiers.stream().filter(FormFormatIdentifier::isNotEmpty).collect(Collectors.toList());
        if (aliases != null) aliases = aliases.stream().filter(FormAlias::isNotEmpty).collect(Collectors.toList());
        if (references != null)
            references = references.stream().filter(FormDocumentation::isNotEmpty).collect(Collectors.toList());
        if (supportActors != null)
            supportActors = supportActors.stream().filter(a -> a.getUri() != null && !a.getUri().isBlank()).collect(Collectors.toList());
        if (developmentActors != null)
            developmentActors = developmentActors.stream().filter(a -> a.getUri() != null && !a.getUri().isBlank()).collect(Collectors.toList());
        if (formatFamilies != null)
            formatFamilies = formatFamilies.stream()
                    .filter(a -> a.getUri() != null && !a.getUri().isBlank() && a.getUri().startsWith(PRONOM.FileFormatFamily.id))
                    .collect(Collectors.toList());
        if (aliases != null) aliases = aliases.stream().filter(FormAlias::isNotEmpty).collect(Collectors.toList());
    }

    public List<FormValidationException> validate(boolean isInternal) {
        ArrayList<FormValidationException> errors = new ArrayList<>();
        if (name == null || name.isBlank())
            errors.add(new FormValidationException("The file format name can not be empty"));
        if (description == null || description.isBlank())
            errors.add(new FormValidationException("The file format description can not be empty"));
        if (isInternal) {
            if (formatType == null || formatType.isBlank())
                errors.add(new FormValidationException("The file format type can not be empty"));
        }
        return errors;
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
                ", containerSignatures=" + containerSignatures +
                ", developmentActors=" + developmentActors +
                ", supportActors=" + supportActors +
                ", hasPriorityOver=" + hasPriorityOver +
                ", hasRelationships=" + hasRelationships +
                ", submittedBy=" + submittedBy +
                ", references=" + references +
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
        ff.setClassifications(f.getClassifications().stream().map(LabeledURI::getLabel).collect(Collectors.toList()));
        // Internal Signatures
        ff.setInternalSignatures(f.getInternalSignatures().stream().map(is -> {
            FormInternalSignature fis = FormInternalSignature.convert(is);
            fis.setFileFormat(f.getFormattedPuid());
            return fis;
        }).sorted(Comparator.comparing(FormInternalSignature::getUri)).collect(Collectors.toList()));
        // External Signatures
        ff.setExternalSignatures(f.getExternalSignatures().stream().map(es -> {
            FormExternalSignature fes = FormExternalSignature.convert(es);
            fes.setFileFormat(f.getFormattedPuid());
            return fes;
        }).sorted(Comparator.comparing(FormExternalSignature::getUri)).collect(Collectors.toList()));
        // Container Signatures
        ff.setContainerSignatures(f.getContainerSignatures().stream().map(cs -> {
            FormContainerSignature fcs = FormContainerSignature.convert(cs);
            fcs.setFileFormat(f.getFormattedPuid());
            return fcs;
        }).sorted(Comparator.comparing(FormContainerSignature::getUri)).collect(Collectors.toList()));
        ff.setHasRelationships(f.getHasRelationships().stream().map(FileFormatRelationship::convert).collect(Collectors.toList()));
        if (ff.getHasRelationships().isEmpty()) {
            ff.setHasRelationships(List.of(new FormFileFormatRelationship()));
        }
        ff.setHasPriorityOver(f.getHasPriorityOver().stream().map(FileFormatRelationship::convert).collect(Collectors.toList()));
        if (ff.getHasPriorityOver().isEmpty()) {
            ff.setHasPriorityOver(List.of(new FormFileFormatRelationship()));
        }
        ff.setReferences(f.getReferences().stream().map(Documentation::convert).collect(Collectors.toList()));
        if (ff.getReferences().isEmpty()) {
            ff.setReferences(List.of(new FormDocumentation()));
        }
        ff.setFormatFamilies(f.getFormatFamilies().stream().map(LabeledURI::convert).collect(Collectors.toList()));
        if (ff.getFormatFamilies().isEmpty()) {
            ff.setFormatFamilies(List.of(new FormLabeledURI()));
        }
        ff.setDevelopmentActors(f.getDevelopmentActors().stream().map(Actor::convert).collect(Collectors.toList()));
        if (ff.getDevelopmentActors().isEmpty()) {
            ff.setDevelopmentActors(List.of(new FormActor()));
        }
        ff.setSupportActors(f.getSupportActors().stream().map(Actor::convert).collect(Collectors.toList()));
        if (ff.getSupportActors().isEmpty()) {
            ff.setSupportActors(List.of(new FormActor()));
        }
        ff.setIdentifiers(f.getFormatIdentifiers().stream().map(FormatIdentifier::convert).collect(Collectors.toList()));
        if (ff.getIdentifiers().isEmpty()) {
            ff.setIdentifiers(List.of(new FormFormatIdentifier()));
        }
        ff.setAliases(f.getAliases().stream().map(FormatAlias::convert).collect(Collectors.toList()));
        if (ff.getAliases().isEmpty()) {
            ff.setAliases(List.of(new FormAlias()));
        }
        return ff;
    }
}
