package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.PRONOM;
import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class ActorDeserializer implements RDFDeserializer<Actor> {
    @Override
    public Resource getRDFType() {
        return makeResource(PRONOM.Actor.type);
    }

    @Override
    public Actor fromModel(Resource uri, Model model) {
        return new Actor(uri);
    }
}
