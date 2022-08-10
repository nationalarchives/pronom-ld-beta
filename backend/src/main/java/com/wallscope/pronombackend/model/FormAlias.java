package com.wallscope.pronombackend.model;

import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class FormAlias {
    private String uri;
    private String name;
    private String version;

    public FormAlias() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isNotEmpty() {
        return uri != null && !uri.isBlank()
                && name != null && !name.isBlank();
    }

    public FormatAlias toObject() {
        return new FormatAlias(makeResource(uri), name, version);
    }
}
