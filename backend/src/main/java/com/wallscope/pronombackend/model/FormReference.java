package com.wallscope.pronombackend.model;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class FormReference {
    private String uri;
    private String name;
    private String link;
    private String author;
    private String identifiers;
    private String publicationDate;
    private String type;
    private String note;

    public FormReference() {

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(String identifiers) {
        this.identifiers = identifiers;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    boolean isNotEmpty() {
        return uri != null && name != null && !name.isBlank() && link != null && !link.isBlank();
    }

    @Override
    public String toString() {
        return "FormReference{" +
                "uri='" + uri + '\'' +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", author='" + author + '\'' +
                ", identifiers='" + identifiers + '\'' +
                ", publicationDate='" + publicationDate + '\'' +
                ", type='" + type + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

    public Reference toObject() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        TemporalAccessor t = formatter.parse(publicationDate);
        Instant parsedDate = Instant.from(t);
        return new Reference(makeResource(uri), name, link, makeResource(author), identifiers, parsedDate, type, note);
    }
}
