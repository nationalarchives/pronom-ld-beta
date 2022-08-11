package com.wallscope.pronombackend.soap;

import uk.gov.nationalarchives.pronom.signaturefile.SignatureFileType;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"ffSignatureFile"})
@XmlRootElement(name = "FFSignatureFile")
public class SignatureFileWrapper {

    @XmlElement(name = "SignatureFile", required = true)
    protected SignatureFileType ffSignatureFile;

    public SignatureFileType getSignatureFile() {
        return ffSignatureFile;
    }

    public void setSignatureFile(SignatureFileType value) {
        this.ffSignatureFile = value;
    }

}

