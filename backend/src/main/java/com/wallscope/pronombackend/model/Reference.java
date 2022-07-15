package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import java.time.Instant;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class Reference implements RDFWritable {
    private final Resource uri;
    private final String name;
    private final String link;
    private final Resource author;
    private final String identifiers;
    private final Instant publicationDate;
    private final String type;
    private final String note;

    public Reference(Resource uri, String name, String link, Resource author, String identifiers, Instant publicationDate, String type, String note) {
        this.uri = uri;
        this.name = name;
        this.link = link;
        this.author = author;
        this.identifiers = identifiers;
        this.publicationDate = publicationDate;
        this.type = type;
        this.note = note;
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
    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, makeProp(RDF.type), makeResource(PRONOM.Reference.type));
        if (name != null) m.add(uri, makeProp(RDFS.label), makeLiteral(name));
        if (link != null) m.add(uri, makeProp(PRONOM.Reference.Link), makeLiteral(link));
        if (author != null) m.add(uri, makeProp(PRONOM.Reference.Author), author);
        if (identifiers != null) m.add(uri, makeProp(PRONOM.Reference.Identifiers), makeLiteral(identifiers));
        if (publicationDate != null) {
            m.add(uri, makeProp(PRONOM.Reference.PublicationDate), makeXSDDateTime(publicationDate));
        }
        if (type != null) m.add(uri, makeProp(PRONOM.Reference.ReferenceType), makeLiteral(type));
        if (name != null) m.add(uri, makeProp(PRONOM.Reference.Note), makeLiteral(name));
        return m;
    }

    public String getLink() {
        return link;
    }

    public Resource getAuthor() {
        return author;
    }

    public String getIdentifiers() {
        return identifiers;
    }

    public Instant getPublicationDate() {
        return publicationDate;
    }

    public String getType() {
        return type;
    }

    public String getNote() {
        return note;
    }

    @Override
    public String toString() {
        return "Reference{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", author=" + author +
                ", identifiers='" + identifiers + '\'' +
                ", publicationDate=" + publicationDate +
                ", type=" + type +
                ", note='" + note + '\'' +
                '}';
    }

    public static class Deserializer implements RDFDeserializer<Reference> {

        @Override
        public Resource getRDFType() {
            return makeResource(PRONOM.Reference.type);
        }

        @Override
        public Reference fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            String name = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(RDFS.label)));
            String link = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Reference.Link)));
            Resource author = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Reference.Author)));
            String type = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Reference.ReferenceType)));
            Instant publicationDate = safelyParseDateOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Reference.PublicationDate)));
            String identifiers = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Reference.Identifiers)));
            String note = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(RDFS.comment)));
            return new Reference(uri, name, link, author, identifiers, publicationDate, type, note);
        }
    }
}
