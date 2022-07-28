package com.wallscope.pronombackend.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class FormDocumentation {
    private String uri;
    private String name;
    private String author;
    private String authorName;
    private String identifiers;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private String publicationDate;
    private String type;
    private String note;

    public FormDocumentation() {

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

    public boolean getIsDocumentation() {
        return (author != null && !author.isBlank())
                || (publicationDate != null && !publicationDate.isBlank())
                || (type != null && !type.isBlank())
                || (note != null && !note.isBlank());
    }

    boolean isNotEmpty() {
        return uri != null && name != null && !name.isBlank() && identifiers != null && !identifiers.isBlank();
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public String toString() {
        return "FormReference{" +
                "uri='" + uri + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", identifiers='" + identifiers + '\'' +
                ", publicationDate='" + publicationDate + '\'' +
                ", type='" + type + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

    public Documentation toObject() {
        Instant parsedDate = parseDate(publicationDate);
        return new Documentation(makeResource(uri), name, new Actor(makeResource(author), null, null, null, null, null, null), identifiers, parsedDate, type, note);
    }

    private Instant parseDate(String str) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.UK).withZone(ZoneId.systemDefault());
            TemporalAccessor t = formatter.parse(str);
            return Instant.from(t);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
