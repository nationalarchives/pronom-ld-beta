package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Resource;

public class PUID {
    public final Resource uri;
    public final Integer puid;
    public final String type;

    public PUID(Resource uri, Integer puid, String type) {
        this.uri = uri;
        this.puid = puid;
        this.type = type;
    }
}
