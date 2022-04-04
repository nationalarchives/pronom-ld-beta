package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.RDFUtil;
import org.apache.jena.rdf.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class FileFormat implements RDFWritable {
    Logger logger = LoggerFactory.getLogger(FileFormat.class);
    private final Resource uri;
    private final Integer puid;
    private final Resource puidType;
    private final String puidTypeName;
    private final String name;
    private final String description;
    private final Instant updated;
    private final String version;
    private final Boolean binaryFlag;
    private final Boolean withdrawnFlag;

    public FileFormat(Resource uri, Integer puid, Resource puidType, String puidTypeName, String name, String description, Instant updated, String version, Boolean binaryFlag, Boolean withdrawnFlag) {
        this.uri = uri;
        this.puid = puid;
        this.puidType = puidType;
        this.puidTypeName = puidTypeName;
        this.name = name;
        this.description = description;
        this.updated = updated;
        this.version = version;
        this.binaryFlag = binaryFlag;
        this.withdrawnFlag = withdrawnFlag;
    }

    public Resource getURI() {
        return uri;
    }

    public Integer getPuid() {
        return puid;
    }

    public Resource getPuidType() {
        return puidType;
    }

    public String getPuidTypeName() {
        return puidTypeName;
    }

    public String getFormattedPuid() {
        return puidTypeName.trim() + "/" + puid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getUpdated() {
        return updated;
    }

    public String getVersion() {
        return version;
    }

    public boolean isBinaryFlag() {
        return binaryFlag;
    }

    public boolean isWithdrawnFlag() {
        return withdrawnFlag;
    }

    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, RDFUtil.makeProp(RDFUtil.RDF.type), RDFUtil.makeResource(RDFUtil.PRONOM.FileFormat.type));
        m.add(uri, RDFUtil.makeProp(RDFUtil.RDFS.label), RDFUtil.makeLiteral(name));
        m.add(uri, RDFUtil.makeProp(RDFUtil.RDFS.comment), RDFUtil.makeLiteral(description));
        m.add(uri, RDFUtil.makeProp(RDFUtil.RDFS.label), RDFUtil.makeLiteral(name));
        m.add(uri, RDFUtil.makeProp(RDFUtil.PRONOM.FileFormat.LastUpdatedDate), RDFUtil.makeXSDDateTime(updated));
        m.add(uri, RDFUtil.makeProp(RDFUtil.PRONOM.FileFormat.Version), RDFUtil.makeLiteral(version));
        if (binaryFlag != null) {
            m.add(uri, RDFUtil.makeProp(RDFUtil.PRONOM.FileFormat.BinaryFlag), RDFUtil.makeLiteral(binaryFlag));
        }
        if (withdrawnFlag != null) {
            m.add(uri, RDFUtil.makeProp(RDFUtil.PRONOM.FileFormat.WithdrawnFlag), RDFUtil.makeLiteral(withdrawnFlag));
        }
        return m;
    }

    public static class FileFormatDeserializer {
        public static Resource getRDFType() {
            return RDFUtil.makeResource(RDFUtil.PRONOM.FileFormat.type);
        }

        public static FileFormat fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            Integer puid = mu.getOneObjectOrNull(uri, RDFUtil.makeProp(RDFUtil.PRONOM.FileFormat.Puid)).asLiteral().getInt();
            Resource puidType = mu.getOneObjectOrNull(uri, RDFUtil.makeProp(RDFUtil.PRONOM.FileFormat.PuidTypeId)).asResource();
            String puidTypeName = mu.getOneObjectOrNull(puidType, RDFUtil.makeProp(RDFUtil.PRONOM.PuidType.PuidType)).asLiteral().getString();
            String name = mu.getOneObjectOrNull(uri, RDFUtil.makeProp(RDFUtil.RDFS.label)).asLiteral().getString();
            String description = mu.getOneObjectOrNull(uri, RDFUtil.makeProp(RDFUtil.RDFS.comment)).asLiteral().getString();
            Literal updatedLit = mu.getOneObjectOrNull(uri, RDFUtil.makeProp(RDFUtil.PRONOM.FileFormat.LastUpdatedDate)).asLiteral();
            Instant updated = RDFUtil.parseDate(updatedLit);
            String version = mu.getOneObjectOrNull(uri, RDFUtil.makeProp(RDFUtil.PRONOM.FileFormat.Version)).asLiteral().getString();
            Boolean binaryFlag = null;
            RDFNode binaryFlagNode = mu.getOneObjectOrNull(uri, RDFUtil.makeProp(RDFUtil.PRONOM.FileFormat.BinaryFlag));
            if (binaryFlagNode != null) {
                binaryFlag = binaryFlagNode.asLiteral().getBoolean();
            }
            Boolean withdrawnFlag = null;
            RDFNode withdrawnFlagNode = mu.getOneObjectOrNull(uri, RDFUtil.makeProp(RDFUtil.PRONOM.FileFormat.WithdrawnFlag));
            if (withdrawnFlagNode != null) {
                withdrawnFlag = withdrawnFlagNode.asLiteral().getBoolean();
            }
            return new FileFormat(uri, puid, puidType, puidTypeName, name, description, updated, version, binaryFlag, withdrawnFlag);
        }
    }

    // Boilerplate

    @Override
    public String toString() {
        return "FileFormat{" +
                "uri=" + uri +
                ", puid='" + puid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", updated=" + updated +
                ", version='" + version + '\'' +
                ", binaryFlag=" + binaryFlag +
                ", withdrawnFlag=" + withdrawnFlag +
                '}';
    }
}
