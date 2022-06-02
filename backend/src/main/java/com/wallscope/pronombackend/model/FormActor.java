package com.wallscope.pronombackend.model;

import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class FormActor {
    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Actor toObject() {
        return new Actor(makeResource(uri));
    }
}
