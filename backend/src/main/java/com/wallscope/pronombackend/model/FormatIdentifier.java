package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class FormatIdentifier implements RDFWritable {
    private final Resource uri;
    private final String name;
    private final Resource type;
    private final String typeName;

    public FormatIdentifier(Resource uri, String name, Resource signatureType, String identifierTypeName) {
        this.uri = uri;
        this.name = name;
        this.type = signatureType;
        this.typeName = identifierTypeName;
    }

    public String getName() {
        return name;
    }

    public Resource getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public Resource getURI() {
        return uri;
    }

    @Override
    public Model toRDF() {
        return null;
    }

    @Override
    public String toString() {
        return "FormatIdentifier{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                ", identifierType=" + type +
                ", identifierTypeName='" + typeName + '\'' +
                '}';
    }

    public static class Deserializer implements RDFDeserializer<FormatIdentifier> {
        @Override
        public Resource getRDFType() {
            return makeResource(PRONOM.FormatIdentifier.type);
        }

        @Override
        public FormatIdentifier fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            String name = mu.getOneObjectOrNull(uri, makeProp(RDFS.label)).asLiteral().getString();
            Resource idType = mu.getOneObjectOrNull(uri, makeProp(PRONOM.FormatIdentifier.FormatIdentifierType)).asResource();
            String idTypeName = mu.getOneObjectOrNull(idType, makeProp(RDFS.label)).asLiteral().getString();
            return new FormatIdentifier(uri, name, idType, idTypeName);
        }
    }
}
