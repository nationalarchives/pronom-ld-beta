package com.wallscope.pronombackend.model;

import javax.validation.constraints.NotNull;

public class FormSubmittedBy {
    private String name;
    private String organisation;
    @NotNull
    private String email;
    private String country;
    private String comment;
    private Boolean isAnonymous;

    public FormSubmittedBy() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "FormSubmittedBy{" +
                "name='" + name + '\'' +
                ", organisation='" + organisation + '\'' +
                ", email='" + email + '\'' +
                ", country='" + country + '\'' +
                ", comment='" + comment + '\'' +
                ", isAnonymous=" + isAnonymous +
                '}';
    }
}
