package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.RDFUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.makeProp;

public class FormOption implements RDFWritable {
    private final String value;
    private final String label;

    public FormOption(String value, String label) {
        this.value = value;
        this.label = label;
    }

    @Override
    public Resource getURI() {
        return null;
    }

    @Override
    public Model toRDF() {
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public static class Deserializer implements RDFDeserializer<FormOption> {
        @Override
        public Resource getRDFType() {
            return null;
        }

        @Override
        public FormOption fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            String label = mu.getOneObjectOrNull(uri, makeProp(RDFUtil.RDFS.label)).asLiteral().getString();
            return new FormOption(uri.getURI(), label);
        }
    }
}
