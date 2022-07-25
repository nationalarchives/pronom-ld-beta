package com.wallscope.pronombackend.model;

import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class FormActor {
    private String uri;
    private String name;
    private String organisation;
    private String email;
    private String country;
    private String website;
    private Boolean isContributor;

    public FormActor() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Actor toObject() {
        return new Actor(makeResource(uri), name, organisation, email, country, website, isContributor);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setContributor(Boolean contributor) {
        isContributor = contributor;
    }

    public Boolean getIsContributor() {
        return isContributor;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWebsite() {
        return website;
    }
}
