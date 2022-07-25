package com.wallscope.pronombackend.dao;

public class ActorDAO {
    public static final String ACTOR_SUB_QUERY = """
            ?act a pr:Actor ; rdf:type ?actExtraTypes .
            OPTIONAL{ ?act rdfs:label ?actName . }#END OPTIONAL
            OPTIONAL{ ?act pr:actor.OrganisationName ?actOrganisation . }#END OPTIONAL
            OPTIONAL{ ?act pr:actor.Email ?actEmail . }#END OPTIONAL
            OPTIONAL{ ?act pr:actor.Country ?actCountry . }#END OPTIONAL
            OPTIONAL{ ?act pr:actor.Website ?actWebsite . }#END OPTIONAL
            """;

}
