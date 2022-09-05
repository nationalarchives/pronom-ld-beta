package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.RDFUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.makeProp;

public class LabeledURI implements RDFWritable {
    private final Resource uri;
    private final String label;


    public LabeledURI(Resource uri, String label) {
        this.uri = uri;
        this.label = label;
    }

    @Override
    public Resource getURI() {
        return uri;
    }

    public String getLabel() {
        return label;
    }

    public FormLabeledURI convert() {
        FormLabeledURI flu = new FormLabeledURI();
        flu.setURI(uri.getURI());
        flu.setLabel(label);
        return flu;
    }

    @Override
    public Model toRDF() {
        return null;
    }

    public static class Deserializer implements RDFDeserializer<LabeledURI> {
        @Override
        public Resource getRDFType() {
            return null;
        }

        @Override
        public LabeledURI fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            String label = mu.getOneObjectOrNull(uri, makeProp(RDFUtil.RDFS.label)).asLiteral().getString();
            return new LabeledURI(uri, label);
        }
    }
}
