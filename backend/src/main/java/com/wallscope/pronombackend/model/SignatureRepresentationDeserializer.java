package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class SignatureRepresentationDeserializer implements RDFDeserializer<FileFormat> {

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
        // Optional
        String version = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormat.Version)));
        // InternalSignature
        List<Resource> intSigSubjects = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.InternalSignature)).stream().map(RDFNode::asResource).collect(Collectors.toList());
        List<InternalSignature> internalSignatures = mu.buildFromModel(new InternalSignatureDeserializer(), intSigSubjects);
        // ExternalSignature
        List<Resource> extSigSubjects = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.ExternalSignature)).stream().map(RDFNode::asResource).collect(Collectors.toList());
        List<ExternalSignature> externalSignatures = mu.buildFromModel(new ExternalSignatureDeserializer(), extSigSubjects);
        // FileFormatRelationship
        List<Resource> relationshipSubjects = mu.getAllObjects(uri, makeProp(PRONOM.FileFormat.InFileFormatRelationship)).stream().map(RDFNode::asResource).collect(Collectors.toList());
        List<FileFormatRelationship> hasRelationships = mu.buildFromModel(new FileFormatRelationshipDeserializer(), relationshipSubjects);

        return new FileFormat(uri,
                puid,
                puidType,
                puidTypeName,
                name,
                null,
                null,
                version,
                null,
                null,
                null,
                internalSignatures,
                externalSignatures,
                Collections.emptyList(),
                Collections.emptyList(),
                hasRelationships);
    }
}
