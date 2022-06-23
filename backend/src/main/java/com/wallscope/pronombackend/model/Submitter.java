package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public class Submitter implements RDFWritable {
    private final Resource uri;
    private final String name;
    private final String organisation;
    private final String email;
    private final String country;
    private final String comment;
    private final Boolean isAnonymous;

    public Submitter(Resource uri, String name, String organisation, String email, String country, String comment, Boolean isAnonymous) {
        this.uri = uri;
        this.name = name;
        this.organisation = organisation;
        this.email = email;
        this.country = country;
        this.comment = comment;
        this.isAnonymous = isAnonymous;
    }


    public String getName() {
        return name;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getEmail() {
        return email;
    }

    public String getCountry() {
        return country;
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "Submitter{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                ", organisation='" + organisation + '\'' +
                ", email='" + email + '\'' +
                ", country='" + country + '\'' +
                ", comment='" + comment + '\'' +
                ", isAnonymous=" + isAnonymous +
                '}';
    }

    @Override
    public Resource getURI() {
        return uri;
    }

    @Override
    public Model toRDF() {
        return null;
    }
}
