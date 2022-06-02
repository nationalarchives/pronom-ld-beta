package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.RDFUtil.PRONOM;
import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class FormExternalSignature {
    private String id;
    private String name;
    private String fileFormat;
    private String signatureType;

    public FormExternalSignature() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignatureType() {
        return signatureType;
    }

    public void setSignatureType(String signatureType) {
        this.signatureType = signatureType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    @Override
    public String toString() {
        return "FormExternalSignature{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", signatureType='" + signatureType + '\'' +
                '}';
    }

    public static FormExternalSignature convert(ExternalSignature es) {
        FormExternalSignature fes = new FormExternalSignature();
        fes.setName(es.getName());
        fes.setSignatureType(es.getSignatureType());
        fes.setId(es.getID());
        // fileFormat set at parent
        return fes;
    }

    public ExternalSignature toObject() {
        Resource uri = makeResource(PRONOM.ExternalSignature.id + getId());
        return new ExternalSignature(uri, getName(), signatureType);

    }
}
