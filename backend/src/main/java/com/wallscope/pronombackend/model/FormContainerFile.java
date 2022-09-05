package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class FormContainerFile {
    private String uri;
    private String signature;
    private String path;
    private ArrayList<FormByteSequence> byteSequences;

    public FormContainerFile() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<FormByteSequence> getByteSequences() {
        return byteSequences;
    }

    public void setByteSequences(ArrayList<FormByteSequence> byteSequences) {
        this.byteSequences = byteSequences;
    }

    public static FormContainerFile convert(ContainerFile cf) {
        FormContainerFile fcf = new FormContainerFile();
        fcf.setUri(safelyGetUriOrNull(cf.getURI()));
        fcf.setPath(cf.getPath());
        // signature field is set at the parent
        fcf.setByteSequences(cf.getByteSequences().stream().map(bs -> {
            FormByteSequence fbs = FormByteSequence.convert(bs);
            fbs.setSignature(safelyGetUriOrNull(cf.getURI()));
            return fbs;
        }).collect(Collectors.toCollection(ArrayList::new)));
        return fcf;
    }

    public ContainerFile toObject(Resource signature) {
        Resource uri = makeResource(PRONOM.ContainerFile.id + getUri());
        return new ContainerFile(uri, signature, getPath(), byteSequences.stream().map(bs -> bs.toObject(uri)).collect(Collectors.toList()));
    }

    public void removeEmpties() {
        if (byteSequences != null)
            byteSequences = byteSequences.stream().filter(FormByteSequence::isNotEmpty).collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean isNotEmpty() {
        return uri != null && !uri.isBlank()
                && path != null && !path.isBlank()
                && byteSequences != null && byteSequences.stream().anyMatch(FormByteSequence::isNotEmpty);
    }
}
