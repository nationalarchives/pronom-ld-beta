package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class FileFormatRelationshipDeserializer implements RDFDeserializer<FileFormatRelationship> {
    @Override
    public Resource getRDFType() {
        return null;
    }

    @Override
    public FileFormatRelationship fromModel(Resource uri, Model model) {
        ModelUtil mu = new ModelUtil(model);
        Resource relType = mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormatRelationship.FileFormatRelationshipType)).asResource();
        String relTypeName = mu.getOneObjectOrNull(relType, makeProp(PRONOM.FormatRelationshipType.TypeName)).asLiteral().getString();
        String inverseTypeName = mu.getOneObjectOrNull(relType, makeProp(PRONOM.FormatRelationshipType.InverseTypeName)).asLiteral().getString();
        Resource source = mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormatRelationship.Source)).asResource();
        String sourceName = safelyGetStringOrNull(mu.getOneObjectOrNull(source, makeProp(RDFS.label)));
        Resource target = mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormatRelationship.Target)).asResource();
        String targetName = safelyGetStringOrNull(mu.getOneObjectOrNull(target, makeProp(RDFS.label)));
        // Optional
        String note = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FileFormatRelationship.Note)));
        return new FileFormatRelationship(uri, relType, relTypeName, inverseTypeName, source, sourceName, target, targetName, note);
    }
}
