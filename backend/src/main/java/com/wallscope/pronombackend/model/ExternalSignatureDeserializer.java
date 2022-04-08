package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class ExternalSignatureDeserializer implements RDFDeserializer<ExternalSignature> {
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
