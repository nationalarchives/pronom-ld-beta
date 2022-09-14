package com.wallscope.pronombackend.soap;

import uk.gov.nationalarchives.pronom.signaturefile.ByteSequenceType;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"containerSignatureCollection", "fileFormatMappingCollection", "triggerPuids"})
@XmlRootElement(name = "ContainerSignatureMapping")
public class ContainerSignatureFileWrapper {

    @XmlElement(name = "ContainerSignatures", required = true)
    protected ContainerSignatureCollection containerSignatureCollection;
    @XmlElement(name = "FileFormatMappings", required = true)
    protected FileFormatMappingCollection fileFormatMappingCollection;
    @XmlElement(name = "TriggerPuids", required = true)
    protected TriggerPuidsCollection triggerPuids;
    @XmlAttribute(name = "signatureVersion", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger version;
    @XmlAttribute(name = "schemaVersion", required = true)
    protected String schemaVersion;
    @XmlAttribute(name = "DateCreated", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateCreated;

    public ContainerSignatureCollection getContainerSignatureCollection() {
        return containerSignatureCollection;
    }

    public void setContainerSignatureCollection(ContainerSignatureCollection value) {
        this.containerSignatureCollection = value;
    }

    public FileFormatMappingCollection getFileFormatMappingCollection() {
        return fileFormatMappingCollection;
    }

    public void setFileFormatMappingCollection(FileFormatMappingCollection value) {
        this.fileFormatMappingCollection = value;
    }

    public TriggerPuidsCollection getTriggerPuids() {
        return triggerPuids;
    }

    public void setTriggerPuids(TriggerPuidsCollection triggerPuids) {
        this.triggerPuids = triggerPuids;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger value) {
        this.version = value;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String value) {
        this.schemaVersion = value;
    }

    public XMLGregorianCalendar getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(XMLGregorianCalendar value) {
        this.dateCreated = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"containerSignature"})
    public static class ContainerSignatureCollection {

        @XmlElement(name = "ContainerSignature")
        protected List<ContainerSignatureType> containerSignature;

        public List<ContainerSignatureType> getContainerSignature() {
            if (containerSignature == null) {
                containerSignature = new ArrayList<>();
            }
            return this.containerSignature;
        }

        public void setContainerSignature(List<ContainerSignatureType> containerSignature) {
            this.containerSignature = containerSignature;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "ContainerSignatureType", propOrder = {"files"})
        public static class ContainerSignatureType {

            @XmlAttribute(name = "Id", required = true)
            protected String id;
            @XmlAttribute(name = "ContainerType", required = true)
            protected String containerType;
            @XmlElement(name = "Files")
            protected List<FileType> files;

            public List<FileType> getFiles() {
                if (files == null) {
                    files = new ArrayList<>();
                }
                return this.files;
            }

            public void setFiles(List<FileType> files) {
                this.files = files;
            }

            public String getID() {
                return id;
            }

            public void setID(String value) {
                this.id = value;
            }

            public String getContainerType() {
                return containerType;
            }

            public void setContainerType(String value) {
                this.containerType = value;
            }

            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "FileType", propOrder = {"path", "byteSequence"})
            public static class FileType {
                @XmlElement(name = "Path")
                protected String path;
                @XmlElement(name = "ByteSequence")
                protected List<ByteSequenceType> byteSequence;

                public List<ByteSequenceType> getByteSequence() {
                    if (byteSequence == null) {
                        byteSequence = new ArrayList<>();
                    }
                    return this.byteSequence;
                }

                public void setByteSequence(List<ByteSequenceType> byteSequence) {
                    this.byteSequence = byteSequence;
                }

                public String getPath() {
                    return path;
                }

                public void setPath(String path) {
                    this.path = path;
                }
            }

        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class FileFormatMappingCollection {
        @XmlElement(name = "FileFormatMapping")
        protected List<FileFormatMapping> ffMapping;

        public List<FileFormatMapping> getFfMapping() {
            if (ffMapping == null) {
                ffMapping = new ArrayList<>();
            }
            return this.ffMapping;
        }

        public void setFfMapping(List<FileFormatMapping> ffMapping) {
            this.ffMapping = ffMapping;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "FileFormatMappingType")
        public static class FileFormatMapping {
            @XmlAttribute(name = "signatureId", required = true)
            protected String signatureId;
            @XmlAttribute(name = "Puid", required = true)
            protected String puid;

            public String getSignatureId() {
                return signatureId;
            }

            public void setSignatureId(String signatureId) {
                this.signatureId = signatureId;
            }

            public String getPuid() {
                return puid;
            }

            public void setPuid(String puid) {
                this.puid = puid;
            }
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class TriggerPuidsCollection {
        @XmlElement(name = "TriggerPuidMapping")
        protected List<TriggerPuid> triggerPuids;

        public List<TriggerPuid> getTriggerPuids() {
            if (triggerPuids == null) {
                triggerPuids = new ArrayList<>();
            }
            return this.triggerPuids;
        }

        public void setTriggerPuids(List<TriggerPuid> triggerPuids) {
            this.triggerPuids = triggerPuids;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "TriggerPuidType")
        public static class TriggerPuid {
            @XmlAttribute(name = "ContainerType", required = true)
            protected String containerType;
            @XmlAttribute(name = "Puid", required = true)
            protected String puid;

            public String getContainerType() {
                return containerType;
            }

            public void setContainerType(String containerType) {
                this.containerType = containerType;
            }

            public String getPuid() {
                return puid;
            }

            public void setPuid(String puid) {
                this.puid = puid;
            }
        }
    }
}

