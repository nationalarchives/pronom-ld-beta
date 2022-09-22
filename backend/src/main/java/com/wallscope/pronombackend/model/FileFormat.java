package com.wallscope.pronombackend.model;

import com.opencsv.CSVWriter;
import com.wallscope.pronombackend.dao.FileFormatDAO;
import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.TemplateUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class FileFormat implements RDFWritable, Comparable<FileFormat> {
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
    private final List<CompressionType> compressionTypes;
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
            List<LabeledURI> formatFamilies,
            List<CompressionType> compressionTypes,
            List<FormatAlias> aliases) {
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
        this.compressionTypes = compressionTypes;
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

    public List<CompressionType> getCompressionTypes() {
        return compressionTypes;
    }

    public List<FormatAlias> getAliases() {
        return aliases;
    }

    public String toCSV() throws IOException {
        return toCSV(true);
    }

    public String toCSV(boolean includeHeaders) throws IOException {
        TemplateUtils t = new TemplateUtils();
        FileFormatDAO dao = new FileFormatDAO();

        // create FileWriter object with file as parameter
        StringWriter sw = new StringWriter();

        // create CSVWriter with ',' as separator
        CSVWriter writer = new CSVWriter(sw, ',',
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        List<String[]> data = new ArrayList<String[]>();
        if (includeHeaders) {
            data.add(new String[]{
                    "File Format ID",
                    "Format Name",
                    "Format Version",
                    "Other names",
                    "Identifiers: Type, Identifier",
                    "Format Families",
                    "Classification",
                    "Description",
                    "Binary",
                    "Byte Orders",
                    "Related File Formats: Relationship Type, Related Format ID, Related Format Name, Related Format Version",
                    "Release Date",
                    "Withdrawn Date",
                    "Developers: Developer ID, Developer Name",
                    "Support: Support ID, Support Name",
                    //"Provenance Source ID", "Provenance Source ID", "Provenance Source ID", "Provenance Source ID",
                    "Last Updated Date",
                    //"Format Note",
                    "Documents: Document ID, Display Text, Document Type, Availability, Availability Note, 'Authors: Author ID, Author Name', Publication Date, Title, 'Publishers: Publisher ID, Publisher Name', 'Document Identifiers: Type, Identifier', Document Rights, Document Note",
                    "External Signatures: Signature ID, Signature Type, Signature",
                    "Internal Signatures: Signature ID, Signature Name, Signature Description, 'Byte Sequence: Position Type, Offset, Byte ordering, Value'",
                    "Compression Types: Compression ID, Compression Name, Compression Version, Compression Other Names, 'Compression Identifiers: Type, Identifier', Family, Description, Lossiness, Release Date, Withdrawn Date, 'Developers: Developer ID, Developer Name', 'Support: Support ID, Support Name', Compression Documentation, Compression Rights, Compression Note"
                    //"Character Encodings: Encoding ID, Encoding Name, Encoding Version, Encoding Aliases, 'Encoding Identifiers: Type, Identifier', Family, Description, Code Page, Code Unit Width, Encoding Form Width, Release Date, Withdrawn Date, 'Developers: Developer ID, Developer Name', 'Support: Support ID, Support Name', Encoding Documentation, Encoding IPR, Encoding Note",
                    //"Rights: IPR ID, IPR Type, IPR Date, 'Owners: Owner ID, Owner Name', IPR Jurisdiction, Licence Details, 'IPR Identifiers: Type, Identifier', IPR Note",
                    //"Properties: Name, Description, Type, Value, Minimum Value, Maximum Value, Risk, High Risk"
            });
        }
        data.add(new String[]{
                t.getString(getID()),
                t.getString(getName()),
                t.getString(getVersion()),
                getAliases().stream().map(alias -> t.getString(alias.getName()) + " (" + t.getString(alias.getVersion()) + ")").collect(Collectors.joining(", ")),
                getFormatIdentifiers().stream().map(fi -> t.getString(fi.getTypeName()) + "," + t.getString(fi.getName()) + ";").collect(Collectors.joining()) + "PUID," + t.getString(getFormattedPuid()) + ";",
                t.getString(getFormatFamilies().stream().map(LabeledURI::getLabel).collect(Collectors.joining(", "))),
                t.getString(getClassifications().stream().map(LabeledURI::getLabel).collect(Collectors.joining(", "))),
                t.getString(getDescription()),
                t.getString(isBinaryFlag()),
                t.getString(getByteOrder().stream().map(t::getLabel).collect(Collectors.joining(", "))),
                getHasRelationships().stream().map(rf -> t.getString(rf.getRelationshipTypeName()) + "," + t.getString(rf.getTargetID()) + ", " + t.getString(rf.getTargetName()) + ", " + t.getString(dao.getFileFormatByURI(rf.getTarget()).getVersion() + ";")).collect(Collectors.joining()),
                t.getString(t.parseDate(getReleaseDate())),
                t.getString(t.parseDate(getWithdrawnDate())),
                getDevelopmentActors().stream().map(da -> t.getString(da.getID()) + "," + t.getString(da.getDisplayName()) + ";").collect(Collectors.joining()),
                getSupportActors().stream().map(sa -> t.getString(sa.getID()) + "," + t.getString(sa.getDisplayName()) + ";").collect(Collectors.joining()),
                t.getString(t.parseDate(getUpdated())),
                getReferences().stream().map(doc -> t.getString(doc.getId()) + "," + t.getString(doc.getName()) + "," + t.getString(doc.getType()) + ",'" + t.getString(doc.getAuthor().getID()) + "," + t.getString(doc.getAuthor().getDisplayName()) + "'," + t.getString(t.parseDate(doc.getPublicationDate())) + "," + t.getString(doc.getName()) + ",'" + t.getString(doc.getAuthor().getID()) + "," + t.getString(doc.getAuthor().getDisplayName()) + "'," + t.getString(doc.getNote()) + ";").collect(Collectors.joining()),
                getExternalSignatures().stream().map(es -> t.getString(es.getID()) + "," + t.getString(es.getSignatureType()) + "," + t.getString(es.getName()) + ";").collect(Collectors.joining()),
                getInternalSignatures().stream().map(is -> t.getString(is.getID()) + "," + t.getString(is.getName()) + "," + t.getString(is.getNote()) + ",'" + is.getByteSequences().stream().map(bs -> t.getString(bs.getPositionName()) + "," + t.getString(bs.getOffset()) + "," + t.getString(bs.getByteOrderName()) + "," + t.getString(bs.getSequence()) + ";").collect(Collectors.joining()) + "';").collect(Collectors.joining()),
                getCompressionTypes().stream().map(ct -> t.getString(ct.getID()) + "," + t.getString(ct.getName()) + "," + t.getString(ct.getDescription()) + "," + t.getString(ct.getLossiness().getLocalName()) + "," + t.getString(t.parseDate(ct.getReleaseDate())) + ";").collect(Collectors.joining())
        });

        writer.writeAll(data);

        writer.close();
        return sw.toString();
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
        if (withdrawnDate != null)
            m.add(uri, makeProp(PRONOM.FileFormat.WithdrawnDate), makeXSDDateTime(withdrawnDate));
        if (version != null) m.add(uri, makeProp(PRONOM.FileFormat.Version), makeLiteral(version));
        if (binaryFlag != null) m.add(uri, makeProp(PRONOM.FileFormat.BinaryFlag), makeLiteral(binaryFlag));
        if (withdrawnFlag != null) m.add(uri, makeProp(PRONOM.FileFormat.WithdrawnFlag), makeLiteral(withdrawnFlag));

        if (byteOrder != null) byteOrder.forEach(bo -> m.add(uri, makeProp(PRONOM.FileFormat.ByteOrder), bo));
        if (classifications != null) {
            classifications.forEach(c -> m.add(uri, makeProp(PRONOM.FileFormat.Classification), c.getURI()));
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
            formatFamilies.forEach(fam -> m.add(uri, makeProp(PRONOM.FileFormat.FormatFamily), fam.getURI()));
        }
        if (compressionTypes != null) {
            compressionTypes.forEach(x -> m.add(uri, makeProp(PRONOM.FileFormat.CompressionType), x.getURI()));
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
    public int compareTo(FileFormat b) {
        boolean aNull = this.getURI() == null;
        boolean bNull = b.getURI() == null;
        if (aNull && !bNull) {
            return -1;
        } else if (bNull && !aNull) {
            return 1;
        } else if (aNull && bNull) {
            return 0;
        }

        int aInt = NumberUtils.toInt(this.getURI().getLocalName(), Integer.MAX_VALUE);
        int bInt = NumberUtils.toInt(b.getURI().getLocalName(), Integer.MAX_VALUE);
        return aInt - bInt;
    }

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
                ", compressionTypes=" + compressionTypes +
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
            // Compression types
            List<Resource> compTypesSubs = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.CompressionType)).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<CompressionType> compressionTypes = mu.buildFromModel(new CompressionType.Deserializer(), compTypesSubs);
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
                    compressionTypes,
                    aliases);
        }


    }
}
