package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class FormatIdentifier implements RDFWritable {
    private final Resource uri;
    private final String name;
    private final Resource type;
    private final String typeName;

    public FormatIdentifier(Resource uri, String name, Resource identifierType, String identifierTypeName) {
        this.uri = uri;
        this.name = name;
        this.type = identifierType;
        this.typeName = identifierTypeName;
    }

    public String getID() {
        String[] parts = uri.getURI().split("/");
        return parts[parts.length - 1];
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
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, makeProp(RDF.type), makeResource(PRONOM.FormatIdentifier.type));
        m.add(uri, makeProp(RDFS.label), makeLiteral(name));
        m.add(uri, makeProp(PRONOM.FormatIdentifier.FormatIdentifierType), type);
        return m;
    }

    @Override
    public String toString() {
        return "FormatIdentifier{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", typeName='" + typeName + '\'' +
                '}';
    }

    public FormFormatIdentifier convert() {
        FormFormatIdentifier fid = new FormFormatIdentifier();
        fid.setUri(safelyGetUriOrNull(uri));
        fid.setType(safelyGetUriOrNull(type));
        fid.setName(name);
        return fid;
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
