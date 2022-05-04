package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.RDFUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.makeProp;
import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class ExternalSignature implements RDFWritable {
    private final Resource uri;
    private final String name;
    private final String signatureType;

    public ExternalSignature(Resource uri, String name, String signatureType) {
        this.uri = uri;
        this.name = name;
        this.signatureType = signatureType;
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
        return null;
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
            return makeResource(RDFUtil.PRONOM.ExternalSignature.type);
        }

        @Override
        public ExternalSignature fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            String name = mu.getOneObjectOrNull(uri, makeProp(RDFUtil.RDFS.label)).asLiteral().getString();
            String sigType = mu.getOneObjectOrNull(uri, makeProp(RDFUtil.PRONOM.ExternalSignature.SignatureType)).asLiteral().getString();
            return new ExternalSignature(uri, name, sigType);
        }
    }
}
