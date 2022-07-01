package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.RDFUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class Contributor implements RDFWritable {
    private final Resource uri;
    private final String name;
    private final String organisation;
    private final String email;
    private final String country;
    private final String comment;
    private final Boolean isAnonymous;
    private final Boolean isInternal;

    public Contributor(Resource uri, String name, String organisation, String email, String country, String comment, Boolean isAnonymous, Boolean isInternal) {
        this.uri = uri;
        this.name = name;
        this.organisation = organisation;
        this.email = email;
        this.country = country;
        this.comment = comment;
        this.isAnonymous = isAnonymous;
        this.isInternal = isInternal;
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

    public Boolean getIsInternal() {
        return isInternal;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "Contributor{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                ", organisation='" + organisation + '\'' +
                ", email='" + email + '\'' +
                ", country='" + country + '\'' +
                ", comment='" + comment + '\'' +
                ", isAnonymous=" + isAnonymous +
                ", isInternal=" + isInternal +
                '}';
    }

    @Override
    public Resource getURI() {
        return uri;
    }

    @Override
    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, makeProp(RDF.type), makeResource(PRONOM.Contributor.type));
        if (name != null) m.add(uri, makeProp(RDFS.label), makeLiteral(name));
        if (isAnonymous != null) m.add(uri, makeProp(PRONOM.Contributor.Anonymous), makeLiteral(isAnonymous));
        if (isInternal != null) m.add(uri, makeProp(PRONOM.Contributor.Internal), makeLiteral(isInternal));
        if (email != null) m.add(uri, makeProp(PRONOM.Contributor.Email), makeLiteral(email));
        if (organisation != null) m.add(uri, makeProp(PRONOM.Contributor.Organisation), makeLiteral(organisation));
        if (country != null) m.add(uri, makeProp(PRONOM.Contributor.Country), makeLiteral(country));
        if (comment != null) m.add(uri, makeProp(PRONOM.Contributor.Comment), makeLiteral(comment));
        return m;
    }

    public static class Deserializer implements RDFDeserializer<Contributor> {

        public Deserializer() {
        }

        @Override
        public Resource getRDFType() {
            return makeResource(RDFUtil.PRONOM.TentativeFileFormat.type);
        }

        @Override
        public Contributor fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            String name = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(RDFS.label)));
            String email = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Contributor.Email)));
            String org = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Contributor.Organisation)));
            String country = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Contributor.Country)));
            String comment = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Contributor.Comment)));
            Boolean isAnonymous = safelyGetBooleanOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Contributor.Anonymous)));
            Boolean isInternal = safelyGetBooleanOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Contributor.Internal)));

            return new Contributor(uri, name, org, email, country, comment, isAnonymous, isInternal);
        }
    }
}
