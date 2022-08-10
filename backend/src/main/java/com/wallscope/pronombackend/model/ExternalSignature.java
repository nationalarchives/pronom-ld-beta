package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class ExternalSignature implements RDFWritable {
    private final Resource uri;
    private final String name;
    private final String signatureType;

    public ExternalSignature(Resource uri, String name, String signatureType) {
        this.uri = uri;
        this.name = name;
        this.signatureType = signatureType;
    }

    public String getID() {
        String[] parts = uri.getURI().split("/");
        return parts[parts.length - 1];
    }

    public String getName() {
        return name;
    }

    public String getSignatureType() {
        return signatureType;
    }

    @Override
    public Resource getURI() {
        return uri;
    }

    @Override
    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        if (name != null) m.add(uri, makeProp(RDFS.label), makeLiteral(name));
        if (signatureType != null)
            m.add(uri, makeProp(PRONOM.ExternalSignature.SignatureType), makeLiteral(signatureType));
        return m;
    }

    @Override
    public String toString() {
        return "ExternalSignature{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                ", signatureType='" + signatureType + '\'' +
                '}';
    }

    public static class Deserializer implements RDFDeserializer<ExternalSignature> {
        @Override
        public Resource getRDFType() {
            return makeResource(PRONOM.ExternalSignature.type);
        }

        @Override
        public ExternalSignature fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            String name = mu.getOneObjectOrNull(uri, makeProp(RDFS.label)).asLiteral().getString();
            String sigType = mu.getOneObjectOrNull(uri, makeProp(PRONOM.ExternalSignature.SignatureType)).asLiteral().getString();
            return new ExternalSignature(uri, name, sigType);
        }
    }
}
