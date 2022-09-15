package com.wallscope.pronombackend.soap;

import uk.gov.nationalarchives.pronom.signaturefile.SignatureFileType;
import uk.gov.nationalarchives.pronom.signaturefile.SignatureFileType.FileFormatCollection;
import uk.gov.nationalarchives.pronom.signaturefile.SignatureFileType.InternalSignatureCollection;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {})
@XmlRootElement(name = "FFSignatureFile")
public class BinarySignatureFileWrapper {

    @XmlElement(name = "InternalSignatureCollection", required = true)
    protected InternalSignatureCollection internalSignatureCollection;
    @XmlElement(name = "FileFormatCollection", required = true)
    protected FileFormatCollection fileFormatCollection;
    @XmlAttribute(name = "Version", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger version;
    @XmlAttribute(name = "DateCreated", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateCreated;

    public InternalSignatureCollection getInternalSignatureCollection() {
        return internalSignatureCollection;
    }

    public void setInternalSignatureCollection(InternalSignatureCollection value) {
        this.internalSignatureCollection = value;
    }

    public FileFormatCollection getFileFormatCollection() {
        return fileFormatCollection;
    }

    public void setFileFormatCollection(FileFormatCollection value) {
        this.fileFormatCollection = value;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger value) {
        this.version = value;
    }

    public XMLGregorianCalendar getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(XMLGregorianCalendar value) {
        this.dateCreated = value;
    }

    // This method converts this manually created wrapper into the auto generated JAXB format.
    public SignatureFileType toJAXBObject(){
        SignatureFileType sig = new SignatureFileType();
        sig.setVersion(version);
        sig.setDateCreated(dateCreated);
        sig.setInternalSignatureCollection(internalSignatureCollection);
        sig.setFileFormatCollection(fileFormatCollection);
        return sig;
    }
}

