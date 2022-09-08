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
    private Integer puid;
    private String puidType;
    private String name;
    private String description;
    private String version;
    private Boolean binaryFlag;
    private Boolean withdrawnFlag;
    private List<String> formatTypes;
    private String byteOrder;
    private ArrayList<FormAlias> aliases;
    private ArrayList<FormFormatIdentifier> identifiers;
    private ArrayList<String> formatFamilies;
    private ArrayList<String> compressionTypes;
    private ArrayList<FormInternalSignature> internalSignatures;
    private ArrayList<FormExternalSignature> externalSignatures;
    private ArrayList<FormContainerSignature> containerSignatures;
    private ArrayList<FormActor> developmentActors;
    private ArrayList<FormActor> supportActors;
    private ArrayList<FormFileFormatRelationship> hasPriorityOver;
    private ArrayList<FormFileFormatRelationship> hasRelationships;
    private FormSubmittedBy submittedBy;
    private List<FormDocumentation> references;

    public FormFileFormat() {
        // We must initialise the nested fields otherwise we get null pointer exceptions in the template rendering
        internalSignatures = new ArrayList<>();
        containerSignatures = new ArrayList<>();
        externalSignatures = new ArrayList<>(List.of(new FormExternalSignature()));
        aliases = new ArrayList<>(List.of(new FormAlias()));
        identifiers = new ArrayList<>(List.of(new FormFormatIdentifier()));
        developmentActors = new ArrayList<>(List.of(new FormActor()));
        supportActors = new ArrayList<>(List.of(new FormActor()));
        hasRelationships = new ArrayList<>(List.of(new FormFileFormatRelationship()));
        hasPriorityOver = new ArrayList<>(List.of(new FormFileFormatRelationship()));
        submittedBy = new FormSubmittedBy();
        references = new ArrayList<>(List.of(new FormDocumentation()));
    }

    public Integer getPuid() {
        return puid;
    }

    public void setPuid(Integer puid) {
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

    public List<FormInternalSignature> getInternalSignatures() {
        return internalSignatures;
    }

    public void setInternalSignatures(ArrayList<FormInternalSignature> internalSignatures) {
        this.internalSignatures = internalSignatures;
    }

    public List<FormExternalSignature> getExternalSignatures() {
        return externalSignatures;
    }

    public void setExternalSignatures(ArrayList<FormExternalSignature> externalSignatures) {
        this.externalSignatures = externalSignatures;
    }

    public List<FormActor> getDevelopmentActors() {
        return developmentActors;
    }

    public void setDevelopmentActors(ArrayList<FormActor> developmentActors) {
        this.developmentActors = developmentActors;
    }

    public List<FormActor> getSupportActors() {
        return supportActors;
    }

    public void setSupportActors(ArrayList<FormActor> supportActors) {
        this.supportActors = supportActors;
    }

    public List<FormFileFormatRelationship> getHasRelationships() {
        return hasRelationships;
    }

    public void setHasRelationships(ArrayList<FormFileFormatRelationship> hasRelationships) {
        this.hasRelationships = hasRelationships;
    }

    public FormSubmittedBy getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(FormSubmittedBy submittedBy) {
        this.submittedBy = submittedBy;
    }

    public List<String> getFormatTypes() {
        return formatTypes;
    }

    public void setFormatTypes(List<String> formatTypes) {
        this.formatTypes = formatTypes;
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

    public void setIdentifiers(ArrayList<FormFormatIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    public List<FormAlias> getAliases() {
        return aliases;
    }

    public void setAliases(ArrayList<FormAlias> aliases) {
        this.aliases = aliases;
    }

    public List<FormFileFormatRelationship> getHasPriorityOver() {
        return hasPriorityOver;
    }

    public void setHasPriorityOver(ArrayList<FormFileFormatRelationship> hasPriorityOver) {
        this.hasPriorityOver = hasPriorityOver;
    }

    public List<String> getFormatFamilies() {
        return formatFamilies;
    }

    public void setFormatFamilies(ArrayList<String> formatFamilies) {
        this.formatFamilies = formatFamilies;
    }

    public String getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(String byteOrder) {
        this.byteOrder = byteOrder;
    }

    public List<String> getCompressionTypes() {
        return compressionTypes;
    }

    public void setCompressionTypes(ArrayList<String> compressionTypes) {
        this.compressionTypes = compressionTypes;
    }

    public List<FormDocumentation> getReferences() {
        return references;
    }

    public void setReferences(ArrayList<FormDocumentation> references) {
        this.references = references;
    }

    public List<FormContainerSignature> getContainerSignatures() {
        return containerSignatures;
    }

    public void setContainerSignatures(ArrayList<FormContainerSignature> containerSignatures) {
        this.containerSignatures = containerSignatures;
    }

    public FileFormat toObject(Instant updated, Instant releaseDate, Instant withdrawnDate, List<CompressionType> compressionTypes) {
        Resource ffUri = makeResource(uri);
        return new FileFormat(ffUri,
                getPuid(),
                makeResource(getPuidType()),
                null,
                getName(),
                getDescription(),
                updated,
                releaseDate,
                withdrawnDate,
                getVersion(),
                getBinaryFlag(),
                getWithdrawnFlag(),
                List.of(makeResource(getByteOrder())),
                getReferences().stream().map(FormDocumentation::toObject).collect(Collectors.toList()),
                getFormatTypes().stream().map(fft -> new LabeledURI(makeResource(fft), null)).collect(Collectors.toList()),
                getInternalSignatures().stream().map(is -> is.toObject(ffUri, updated)).collect(Collectors.toList()),
                getExternalSignatures().stream().map(FormExternalSignature::toObject).collect(Collectors.toList()),
                getContainerSignatures().stream().map(cs -> cs.toObject(ffUri)).collect(Collectors.toList()),
                getIdentifiers().stream().map(FormFormatIdentifier::toObject).collect(Collectors.toList()),
                getDevelopmentActors().stream().map(FormActor::toObject).collect(Collectors.toList()),
                getSupportActors().stream().map(FormActor::toObject).collect(Collectors.toList()),
                getHasRelationships().stream().map(FormFileFormatRelationship::toObject).collect(Collectors.toList()),
                getFormatFamilies().stream().map(fff -> new LabeledURI(makeResource(fff), null)).collect(Collectors.toList()),
                compressionTypes,
                getAliases().stream().map(FormAlias::toObject).collect(Collectors.toList()));
    }

    public void fillURIs() {
        // Set own URI
        if (uri == null || uri.isBlank())
            setUri(PRONOM.TentativeFileFormat.id + UUID.randomUUID());

        if (submittedBy != null && (submittedBy.getUri() == null || submittedBy.getUri().isBlank()))
            submittedBy.setUri(PRONOM.Contributor.id + UUID.randomUUID());

        if (internalSignatures != null) internalSignatures.forEach(fis -> {
            if (fis.getUri() == null || fis.getUri().isBlank())
                fis.setUri(PRONOM.InternalSignature.id + UUID.randomUUID());

            if (fis.getByteSequences() != null)
                fis.getByteSequences().forEach(fbs -> {
                    if (fbs.getUri() == null || fbs.getUri().isBlank())
                        fbs.setUri(PRONOM.ByteSequence.id + UUID.randomUUID());
                });
        });
        if (containerSignatures != null) containerSignatures.forEach(fcs -> {
            if (fcs.getUri() == null || fcs.getUri().isBlank())
                fcs.setUri(PRONOM.ContainerSignature.id + UUID.randomUUID());

            if (fcs.getFiles() != null) fcs.getFiles().forEach(fcf -> {
                if (fcf.getUri() == null || fcf.getUri().isBlank())
                    fcf.setUri(PRONOM.ContainerFile.id + UUID.randomUUID());

                if (fcf.getByteSequences() != null)
                    fcf.getByteSequences().forEach(fbs -> {
                        if (fbs.getUri() == null || fbs.getUri().isBlank())
                            fbs.setUri(PRONOM.ByteSequence.id + UUID.randomUUID());
                    });
            });
        });
        if (externalSignatures != null)
            externalSignatures.forEach(fes -> {
                if (fes.getUri() == null || fes.getUri().isBlank())
                    fes.setUri(PRONOM.ExternalSignature.id + UUID.randomUUID());
            });

        if (identifiers != null) identifiers.forEach(ffi -> {
            if (ffi.getUri() == null || ffi.getUri().isBlank())
                ffi.setUri(PRONOM.FormatIdentifier.id + UUID.randomUUID());
        });
        if (hasPriorityOver != null)
            hasPriorityOver.forEach(fhr -> {
                if (fhr.getUri() == null || fhr.getUri().isBlank())
                    fhr.setUri(PRONOM.FileFormatRelationship.id + UUID.randomUUID());
            });

        if (hasRelationships != null)
            hasRelationships.forEach(fhr -> {
                if (fhr.getUri() == null || fhr.getUri().isBlank())
                    fhr.setUri(PRONOM.FileFormatRelationship.id + UUID.randomUUID());
            });
        if (references != null) references.forEach(fr -> {
            if (fr.getUri() == null || fr.getUri().isBlank()) fr.setUri(PRONOM.Documentation.id + UUID.randomUUID());
        });
        if (aliases != null) aliases.forEach(fa -> {
            if (fa.getUri() == null || fa.getUri().isBlank()) fa.setUri(PRONOM.FormatAlias.id + UUID.randomUUID());
        });
    }

    public void removeEmpties() {
        if (byteOrder != null)
            byteOrder = List.of(PRONOM.ByteOrder.littleEndian, PRONOM.ByteOrder.bigEndian).contains(byteOrder) ? byteOrder : null;
        if (compressionTypes != null)
            compressionTypes = compressionTypes.stream().filter(c -> !c.isBlank()).collect(Collectors.toCollection(ArrayList::new));
        if (internalSignatures != null) internalSignatures = internalSignatures.stream().filter(is -> {
            is.removeEmpties();
            return is.isNotEmpty();
        }).collect(Collectors.toCollection(ArrayList::new));
        if (containerSignatures != null) containerSignatures = containerSignatures.stream().filter(cs -> {
            cs.removeEmpties();
            return cs.isNotEmpty();
        }).collect(Collectors.toCollection(ArrayList::new));
        if (externalSignatures != null)
            externalSignatures = externalSignatures.stream().filter(FormExternalSignature::isNotEmpty).collect(Collectors.toCollection(ArrayList::new));
        if (hasPriorityOver != null)
            hasPriorityOver = hasPriorityOver.stream().filter(FormFileFormatRelationship::isNotEmpty).collect(Collectors.toCollection(ArrayList::new));
        if (hasRelationships != null)
            hasRelationships = hasRelationships.stream().filter(FormFileFormatRelationship::isNotEmpty).collect(Collectors.toCollection(ArrayList::new));
        if (identifiers != null)
            identifiers = identifiers.stream().filter(FormFormatIdentifier::isNotEmpty).collect(Collectors.toCollection(ArrayList::new));
        if (aliases != null)
            aliases = aliases.stream().filter(FormAlias::isNotEmpty).collect(Collectors.toCollection(ArrayList::new));
        if (references != null)
            references = references.stream().filter(FormDocumentation::isNotEmpty).collect(Collectors.toCollection(ArrayList::new));
        if (supportActors != null)
            supportActors = supportActors.stream().filter(a -> a.getUri() != null && !a.getUri().isBlank()).collect(Collectors.toCollection(ArrayList::new));
        if (developmentActors != null)
            developmentActors = developmentActors.stream().filter(a -> a.getUri() != null && !a.getUri().isBlank()).collect(Collectors.toCollection(ArrayList::new));
        if (aliases != null)
            aliases = aliases.stream().filter(FormAlias::isNotEmpty).collect(Collectors.toCollection(ArrayList::new));
        if (formatFamilies != null)
            formatFamilies = formatFamilies.stream().filter(a -> a.startsWith(PRONOM.FileFormatFamily.id)).collect(Collectors.toCollection(ArrayList::new));
        if (formatTypes != null)
            formatTypes = formatTypes.stream().filter(a -> a.startsWith(PRONOM.Classification.id)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<FormValidationException> validate(boolean isInternal) {
        ArrayList<FormValidationException> errors = new ArrayList<>();
        if (name == null || name.isBlank())
            errors.add(new FormValidationException("The file format name can not be empty"));
        if (description == null || description.isBlank())
            errors.add(new FormValidationException("The file format description can not be empty"));
        if (isInternal) {
            if (formatTypes == null || formatTypes.isEmpty())
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
                ", formatType='" + formatTypes + '\'' +
                ", byteOrder='" + byteOrder + '\'' +
                ", compressionTypes='" + compressionTypes + '\'' +
                ", aliases=" + aliases +
                ", identifiers=" + identifiers +
                ", formatFamilies=" + formatFamilies +
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
        ff.setPuid(f.getPuid());
        ff.setPuidType(safelyGetUriOrNull(f.getPuidType()));
        ff.setName(f.getName());
        ff.setDescription(f.getDescription());
        ff.setVersion(f.getVersion());
        ff.setBinaryFlag(f.isBinaryFlag());
        ff.setWithdrawnFlag(f.isWithdrawnFlag());
        ff.setFormatTypes(f.getClassifications().stream().map(ft -> ft.getURI().getURI()).collect(Collectors.toCollection(ArrayList::new)));
        ff.setCompressionTypes(f.getCompressionTypes().stream().map(CompressionType::getName).collect(Collectors.toCollection(ArrayList::new)));
        // Internal Signatures
        ff.setInternalSignatures(f.getInternalSignatures().stream().map(is -> {
            FormInternalSignature fis = FormInternalSignature.convert(is);
            fis.setFileFormat(f.getFormattedPuid());
            return fis;
        }).sorted(Comparator.comparing(FormInternalSignature::getUri)).collect(Collectors.toCollection(ArrayList::new)));
        // External Signatures
        ff.setExternalSignatures(f.getExternalSignatures().stream().map(es -> {
            FormExternalSignature fes = FormExternalSignature.convert(es);
            fes.setFileFormat(f.getFormattedPuid());
            return fes;
        }).sorted(Comparator.comparing(FormExternalSignature::getUri)).collect(Collectors.toCollection(ArrayList::new)));
        // Container Signatures
        ff.setContainerSignatures(f.getContainerSignatures().stream().map(cs -> {
            FormContainerSignature fcs = FormContainerSignature.convert(cs);
            fcs.setFileFormat(f.getFormattedPuid());
            return fcs;
        }).sorted(Comparator.comparing(FormContainerSignature::getUri)).collect(Collectors.toCollection(ArrayList::new)));
        ff.setHasRelationships(f.getHasRelationships().stream().map(FileFormatRelationship::convert).collect(Collectors.toCollection(ArrayList::new)));
        if (ff.getHasRelationships().isEmpty()) {
            ff.setHasRelationships(new ArrayList<>(List.of(new FormFileFormatRelationship())));
        }
        ff.setHasPriorityOver(f.getHasPriorityOver().stream().map(FileFormatRelationship::convert).collect(Collectors.toCollection(ArrayList::new)));
        if (ff.getHasPriorityOver().isEmpty()) {
            ff.setHasPriorityOver(new ArrayList<>(List.of(new FormFileFormatRelationship())));
        }
        ff.setReferences(f.getReferences().stream().map(Documentation::convert).collect(Collectors.toCollection(ArrayList::new)));
        if (ff.getReferences().isEmpty()) {
            ff.setReferences(new ArrayList<>(List.of(new FormDocumentation())));
        }
        ff.setFormatFamilies(f.getFormatFamilies().stream().map(fam -> fam.getURI().getURI()).collect(Collectors.toCollection(ArrayList::new)));
        ff.setDevelopmentActors(f.getDevelopmentActors().stream().map(Actor::convert).collect(Collectors.toCollection(ArrayList::new)));
        if (ff.getDevelopmentActors().isEmpty()) {
            ff.setDevelopmentActors(new ArrayList<>(List.of(new FormActor())));
        }
        ff.setSupportActors(f.getSupportActors().stream().map(Actor::convert).collect(Collectors.toCollection(ArrayList::new)));
        if (ff.getSupportActors().isEmpty()) {
            ff.setSupportActors(new ArrayList<>(List.of(new FormActor())));
        }
        ff.setIdentifiers(f.getFormatIdentifiers().stream().map(FormatIdentifier::convert).collect(Collectors.toCollection(ArrayList::new)));
        if (ff.getIdentifiers().isEmpty()) {
            ff.setIdentifiers(new ArrayList<>(List.of(new FormFormatIdentifier())));
        }
        ff.setAliases(f.getAliases().stream().map(FormatAlias::convert).collect(Collectors.toCollection(ArrayList::new)));
        if (ff.getAliases().isEmpty()) {
            ff.setAliases(new ArrayList<>(List.of(new FormAlias())));
        }
        return ff;
    }

    public String getPuidType() {
        return puidType;
    }

    public void setPuidType(String puidType) {
        this.puidType = puidType;
    }
}
