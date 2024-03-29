package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class Documentation implements RDFWritable {
    private final Resource uri;
    private final String name;
    private final Actor author;
    private final String identifiers;
    private final Instant publicationDate;
    private final String type;
    private final String note;

    public Documentation(Resource uri, String name, Actor author, String identifiers, Instant publicationDate, String type, String note) {
        this.uri = uri;
        this.name = name;
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
        m.add(uri, makeProp(RDF.type), makeResource(PRONOM.Documentation.type));
        if (name != null) m.add(uri, makeProp(RDFS.label), makeLiteral(name));
        if (author != null) m.add(uri, makeProp(PRONOM.Documentation.Author), author.getURI());
        if (identifiers != null) m.add(uri, makeProp(PRONOM.Documentation.Identifiers), makeLiteral(identifiers));
        if (publicationDate != null) {
            m.add(uri, makeProp(PRONOM.Documentation.PublicationDate), makeXSDDateTime(publicationDate));
        }
        if (type != null) m.add(uri, makeProp(PRONOM.Documentation.DocumentType), makeLiteral(type));
        if (note != null) m.add(uri, makeProp(PRONOM.Documentation.Note), makeLiteral(note));
        return m;
    }

    public Actor getAuthor() {
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
                ", author=" + author +
                ", identifiers='" + identifiers + '\'' +
                ", publicationDate=" + publicationDate +
                ", type=" + type +
                ", note='" + note + '\'' +
                '}';
    }

    public FormDocumentation convert() {
        FormDocumentation fr = new FormDocumentation();
        fr.setUri(safelyGetUriOrNull(uri));
        fr.setName(name);
        if (author != null) {
            fr.setAuthor(safelyGetUriOrNull(author.getURI()));
            fr.setAuthorName(author.getDisplayName());
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.UK).withZone(ZoneId.systemDefault());
        if (publicationDate != null) fr.setPublicationDate(formatter.format(publicationDate));
        fr.setIdentifiers(identifiers);
        fr.setType(type);
        fr.setNote(note);
        return fr;
    }

    public static class Deserializer implements RDFDeserializer<Documentation> {

        @Override
        public Resource getRDFType() {
            return makeResource(PRONOM.Documentation.type);
        }

        @Override
        public Documentation fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            String name = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(RDFS.label)));
            Resource authorUri = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Documentation.Author)));
            Actor author = new Actor.Deserializer().fromModel(authorUri, model);

            String type = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Documentation.DocumentType)));
            Instant publicationDate = safelyParseDateOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Documentation.PublicationDate)));
            String identifiers = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Documentation.Identifiers)));
            String note = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Documentation.Note)));
            return new Documentation(uri, name, author, identifiers, publicationDate, type, note);
        }
    }
}
