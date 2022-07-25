package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.RDFUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class Actor implements RDFWritable {
    private final Resource uri;
    private final String name;
    private final String organisation;
    private final String email;
    private final String country;
    private final String website;
    private final Boolean isContributor;


    public Actor(Resource uri, String name, String organisation, String email, String country, String website, Boolean isContributor) {
        this.uri = uri;
        this.name = name;
        this.organisation = organisation;
        this.email = email;
        this.country = country;
        this.website = website;
        this.isContributor = isContributor;
    }

    @Override
    public Resource getURI() {
        return uri;
    }

    @Override
    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, makeProp(RDFUtil.RDF.type), makeResource(RDFUtil.PRONOM.Actor.type));
        if (isContributor) m.add(uri, makeProp(RDFUtil.RDF.type), makeResource(PRONOM.Actor.ActorContributorType));
        if (name != null) m.add(uri, makeProp(RDFUtil.RDFS.label), makeLiteral(name));
        if (organisation != null) m.add(uri, makeProp(PRONOM.Actor.OrganisationName), makeLiteral(organisation));
        if (email != null) m.add(uri, makeProp(PRONOM.Actor.Email), makeLiteral(email));
        if (country != null) m.add(uri, makeProp(PRONOM.Actor.Country), makeLiteral(country));
        if (website != null) m.add(uri, makeProp(PRONOM.Actor.Website), makeLiteral(website));
        return m;
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

    public String getWebsite() {
        return website;
    }

    public String getDisplayName() {
        if (name != null && !name.isBlank()) return name;
        return organisation;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                ", organisation='" + organisation + '\'' +
                ", email='" + email + '\'' +
                ", country='" + country + '\'' +
                ", website='" + website + '\'' +
                ", isContributor=" + isContributor +
                '}';
    }

    public FormActor convert() {
        FormActor fa = new FormActor();
        fa.setUri(uri.getURI());
        fa.setName(name);
        fa.setOrganisation(organisation);
        fa.setEmail(email);
        fa.setCountry(country);
        fa.setWebsite(website);
        fa.setContributor(isContributor);
        return fa;
    }

    public static class Deserializer implements RDFDeserializer<Actor> {
        Logger logger = LoggerFactory.getLogger(Actor.Deserializer.class);

        @Override
        public Resource getRDFType() {
            return makeResource(RDFUtil.PRONOM.Actor.type);
        }

        @Override
        public Actor fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            String name = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(RDFS.label)));
            String organisation = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Actor.OrganisationName)));
            String email = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Actor.Email)));
            String website = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Actor.Website)));
            String country = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Actor.Country)));
            Boolean isContributor = mu.getAllObjects(uri, makeProp(RDF.type)).stream()
                    .map(RDFUtil::safelyGetResourceOrNull)
                    .anyMatch(r -> r.getURI().equals(PRONOM.Actor.ActorContributorType));
            return new Actor(uri, name, organisation, email, country, website, isContributor);
        }
    }
}
