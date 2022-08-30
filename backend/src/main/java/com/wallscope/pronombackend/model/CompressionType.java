package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class CompressionType implements RDFWritable {
    Logger logger = LoggerFactory.getLogger(CompressionType.class);
    private final Resource uri;
    private final Resource lossiness;
    private final String name;
    private final String description;
    private final Instant releaseDate;

    public CompressionType(
            Resource uri,
            Resource lossiness,
            String name,
            String description,
            Instant releaseDate) {
        this.uri = uri;
        this.lossiness = lossiness;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
    }

    public Resource getURI() {
        return uri;
    }

    public String getID() {
        String[] parts = uri.getURI().split("/");
        return parts[parts.length - 1];
    }

    public Resource getLossiness() {
        return lossiness;
    }

     public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getReleaseDate() {
        return releaseDate;
    }

    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, makeProp(RDF.type), makeResource(PRONOM.CompressionType.type));

        if (lossiness != null) {
            m.add(uri, makeProp(PRONOM.CompressionType.Lossiness), lossiness);
        }
        if (name != null) m.add(uri, makeProp(RDFS.label), makeLiteral(name));
        if (description != null) m.add(uri, makeProp(RDFS.comment), makeLiteral(description));
        if (releaseDate != null) m.add(uri, makeProp(PRONOM.CompressionType.ReleaseDate), makeXSDDateTime(releaseDate));

        return m;
    }

    // Boilerplate


    @Override
    public String toString() {
        return "CompressionType{" +
                "uri=" + uri +
                ", lossiness=" + lossiness +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof CompressionType)) return false;
        CompressionType cast = (CompressionType) other;
        return this.toRDF().isIsomorphicWith(cast.toRDF());
    }

    public static class Deserializer implements RDFDeserializer<CompressionType> {
        Logger logger = LoggerFactory.getLogger(CompressionType.Deserializer.class);

        public Deserializer() {
        }

        public Resource getRDFType() {
            return makeResource(PRONOM.CompressionType.type);
        }

        public CompressionType fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);

            Resource lossiness = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.CompressionType.Lossiness)));
            String name = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(RDFS.label)));
            String description = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(RDFS.comment)));

            Instant releaseDate = safelyParseDateOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.CompressionType.ReleaseDate)));

            return new CompressionType(uri,
                    lossiness,
                    name,
                    description,
                    releaseDate);
        }
    }
}
