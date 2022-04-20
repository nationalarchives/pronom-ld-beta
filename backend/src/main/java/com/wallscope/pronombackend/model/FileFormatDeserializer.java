package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class FileFormatDeserializer implements RDFDeserializer<FileFormat> {

    public FileFormatDeserializer() {
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
        List<InternalSignature> internalSignatures = mu.buildFromModel(new InternalSignatureDeserializer(), intSigSubjects);
        // ExternalSignature
        List<Resource> extSigSubjects = mu.getAllSubjects(makeProp(PRONOM.ExternalSignature.FileFormat), uri).stream().map(RDFNode::asResource).collect(Collectors.toList());
        List<ExternalSignature> externalSignatures = mu.buildFromModel(new ExternalSignatureDeserializer(), extSigSubjects);
        // FileFormatRelationship
        List<Resource> relationshipSubjects = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.InFileFormatRelationship)).stream().map(RDFNode::asResource).collect(Collectors.toList());
        List<FileFormatRelationship> hasRelationships = mu.buildFromModel(new FileFormatRelationshipDeserializer(), relationshipSubjects);
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
                developmentActors,
                supportActors,
                hasRelationships);
    }
}
