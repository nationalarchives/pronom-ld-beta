package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import java.time.Instant;
import java.util.List;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class TentativeFileFormat extends FileFormat {
    private final String author;
    private final Submitter submitter;
    private final Resource source;

    public TentativeFileFormat(Resource uri, Integer puid, Resource puidType, String puidTypeName, String name, String description, Instant updated, String version, Boolean binaryFlag, Boolean withdrawnFlag, List<Classification> classifications, List<InternalSignature> internalSignatures, List<ExternalSignature> externalSignatures, List<ContainerSignature> containerSignatures, List<FormatIdentifier> formatIdentifiers, List<Actor> developmentActors, List<Actor> supportActors, List<FileFormatRelationship> hasRelationships, String author, Submitter submitter, Resource source) {
        super(uri, puid, puidType, puidTypeName, name, description, updated, version, binaryFlag, withdrawnFlag, classifications, internalSignatures, externalSignatures, containerSignatures, formatIdentifiers, developmentActors, supportActors, hasRelationships);
        this.author = author;
        this.submitter = submitter;
        this.source = source;
    }

    public TentativeFileFormat(Resource uri, FileFormat ff, String author, Submitter submitter, Resource source) {
        this(uri, ff.getPuid(), ff.getPuidType(), ff.getPuidTypeName(), ff.getName(), ff.getDescription(), ff.getUpdated(), ff.getVersion(), ff.isBinaryFlag(), ff.isWithdrawnFlag(), ff.getClassifications(), ff.getInternalSignatures(), ff.getExternalSignatures(), ff.getContainerSignatures(), ff.getFormatIdentifiers(), ff.getDevelopmentActors(), ff.getSupportActors(), ff.getHasRelationships(), author, submitter, source);
    }

    public String getAuthor() {
        return author;
    }

    public Resource getSource() {
        return source;
    }

    @Override
    public Model toRDF() {
        Model m = super.toRDF();
        m.removeAll(super.getURI(), makeProp(RDF.type), makeResource(PRONOM.FileFormat.type));
        m.add(super.getURI(), makeProp(RDF.type), makeResource(PRONOM.TentativeFileFormat.type));
        m.add(super.getURI(), makeProp(PRONOM.FileFormat.type), makeResource(PRONOM.TentativeFileFormat.type));
        return m;
    }

    public Submitter getSubmitter() {
        return submitter;
    }

    public static class Deserializer implements RDFDeserializer<TentativeFileFormat> {

        public Deserializer() {
        }

        @Override
        public Resource getRDFType() {
            return makeResource(PRONOM.TentativeFileFormat.type);
        }

        @Override
        public TentativeFileFormat fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            FileFormat ff = new FileFormat.Deserializer().fromModel(uri, model);
            String author = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.TentativeFileFormat.Author)));
            Resource source = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.TentativeFileFormat.Source)));
            // TODO: Implement submitter
            return new TentativeFileFormat(uri, ff, author, null, source);
        }
    }
}
