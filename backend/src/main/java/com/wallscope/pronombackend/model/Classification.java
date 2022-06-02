package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Resource;

public class Classification {
    private final Resource uri;
    private final String name;

    public Classification(Resource uri, String name) {
        this.uri = uri;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        String[] parts = uri.getURI().split("/");
        return parts[parts.length - 1];
    }

    public Resource getURI() {
        return uri;
    }

    @Override
    public String toString() {
        return "Classification{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                '}';
    }
}
