package com.wallscope.pronombackend.model;

import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class FormExternalSignature {
    private String uri;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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
                "id='" + uri + '\'' +
                ", name='" + name + '\'' +
                ", signatureType='" + signatureType + '\'' +
                '}';
    }

    public static FormExternalSignature convert(ExternalSignature es) {
        FormExternalSignature fes = new FormExternalSignature();
        fes.setName(es.getName());
        fes.setSignatureType(es.getSignatureType());
        fes.setUri(es.getURI().getURI());
        // fileFormat set at parent
        return fes;
    }

    public ExternalSignature toObject() {
        return new ExternalSignature(makeResource(getUri()), getName(), getSignatureType());

    }

    public boolean isNotEmpty() {
        return uri != null && !uri.isBlank()
                && name != null && !name.isBlank()
                && signatureType != null && !signatureType.isBlank();
    }
}
