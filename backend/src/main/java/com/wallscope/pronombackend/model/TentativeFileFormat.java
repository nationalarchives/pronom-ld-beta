package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import java.time.Instant;
import java.util.List;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class TentativeFileFormat extends FileFormat {
    public TentativeFileFormat(Resource uri, Integer puid, Resource puidType, String puidTypeName, String name, String description, Instant updated, Instant releaseDate, Instant withdrawnDate, String version, Boolean binaryFlag, Boolean withdrawnFlag, List<Resource> byteOrder, List<Documentation> references, List<LabeledURI> classifications, List<InternalSignature> internalSignatures, List<ExternalSignature> externalSignatures, List<ContainerSignature> containerSignatures, List<FormatIdentifier> formatIdentifiers, List<Actor> developmentActors, List<Actor> supportActors, List<FileFormatRelationship> hasRelationships, List<LabeledURI> formatFamilies, List<CompressionType> compressionTypes, List<FormatAlias> aliases) {
        super(uri, puid, puidType, puidTypeName, name, description, updated, releaseDate, withdrawnDate, version, binaryFlag, withdrawnFlag, byteOrder, references, classifications, internalSignatures, externalSignatures, containerSignatures, formatIdentifiers, developmentActors, supportActors, hasRelationships, formatFamilies, compressionTypes, aliases);
    }

    public TentativeFileFormat(Resource uri, FileFormat ff) {
        this(uri, ff.getPuid(), ff.getPuidType(), ff.getPuidTypeName(), ff.getName(), ff.getDescription(), ff.getUpdated(), ff.getReleaseDate(), ff.getWithdrawnDate(), ff.getVersion(), ff.isBinaryFlag(), ff.isWithdrawnFlag(), ff.getByteOrder(), ff.getReferences(), ff.getClassifications(), ff.getInternalSignatures(), ff.getExternalSignatures(), ff.getContainerSignatures(), ff.getFormatIdentifiers(), ff.getDevelopmentActors(), ff.getSupportActors(), ff.getHasRelationships(), ff.getFormatFamilies(), ff.getCompressionTypes(), ff.getAliases());
    }

    @Override
    public Model toRDF() {
        Model m = super.toRDF();
        m.removeAll(super.getURI(), makeProp(RDF.type), makeResource(PRONOM.FileFormat.type));
        m.add(super.getURI(), makeProp(RDF.type), makeResource(PRONOM.TentativeFileFormat.type));
        return m;
    }
}
