package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.RDFUtil;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class FormDocumentation {
    private String uri;
    private String name;
    private String author;
    private String identifiers;
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

    boolean isNotEmpty() {
        return uri != null && name != null && !name.isBlank() && identifiers != null && !identifiers.isBlank();
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
        return new Documentation(makeResource(uri), name, makeResource(author), identifiers, parsedDate, type, note);
    }

    private Instant parseDate(String str){
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            TemporalAccessor t = formatter.parse(str);
            return Instant.from(t);
        }catch(DateTimeParseException e){
            return null;
        }
    }
}
