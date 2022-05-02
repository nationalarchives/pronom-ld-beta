package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.RDFUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class Actor implements RDFWritable {
    private final Resource uri;

    public Actor(Resource uri) {
        this.uri = uri;
    }

    @Override
    public Resource getURI() {
        return uri;
    }

    @Override
    public Model toRDF() {
        return null;
    }

    public static class Deserializer implements RDFDeserializer<Actor> {
        @Override
        public Resource getRDFType() {
            return makeResource(RDFUtil.PRONOM.Actor.type);
        }

        @Override
        public Actor fromModel(Resource uri, Model model) {
            return new Actor(uri);
        }
    }
}
