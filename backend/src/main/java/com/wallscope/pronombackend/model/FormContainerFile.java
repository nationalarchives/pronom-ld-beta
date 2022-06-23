package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Resource;

import java.util.List;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.PRONOM;
import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class FormContainerFile {
    private String uri;
    private String signature;
    private String path;
    private List<FormByteSequence> byteSequences;

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

    public void setByteSequences(List<FormByteSequence> byteSequences) {
        this.byteSequences = byteSequences;
    }

    public static FormContainerFile convert(ContainerFile cf) {
        FormContainerFile fcf = new FormContainerFile();
        fcf.setUri(cf.getID());
        fcf.setPath(cf.getPath());
        // signature field is set at the parent
        fcf.setByteSequences(cf.getByteSequences().stream().map(bs -> {
            FormByteSequence fbs = FormByteSequence.convert(bs);
            fbs.setSignature(cf.getID());
            return fbs;
        }).collect(Collectors.toList()));
        return fcf;
    }

    public ContainerFile toObject(Resource signature) {
        Resource uri = makeResource(PRONOM.ContainerFile.id + getUri());
        return new ContainerFile(uri, signature, getPath(), byteSequences.stream().map(bs -> bs.toObject(uri)).collect(Collectors.toList()));
    }
}
