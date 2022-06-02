package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Resource;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.PRONOM;
import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class FormContainerSignature {
    private String id;
    private String name;
    private String containerType;
    private String fileFormat;
    private List<FormContainerFile> files;

    public FormContainerSignature() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContainerType() {
        return containerType;
    }

    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }

    public List<FormContainerFile> getFiles() {
        return files;
    }

    public void setFiles(List<FormContainerFile> files) {
        this.files = files;
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

    public static FormContainerSignature convert(ContainerSignature cs) {
        FormContainerSignature fcs = new FormContainerSignature();
        fcs.setName(cs.getName());

        // fileFormat field is set at the parent
        fcs.setFiles(cs.getFiles().stream().map(f -> {
            FormContainerFile fcf = FormContainerFile.convert(f);
            fcf.setSignature(cs.getID());
            return fcf;
        }).collect(Collectors.toList()));
        return fcs;
    }

    public ContainerSignature toObject(Resource formatUri) {
        Resource uri = makeResource(PRONOM.ContainerSignature.id + getId());
        return new ContainerSignature(uri,
                getName(),
                makeResource(getContainerType()),
                formatUri,
                files.stream().map(f -> f.toObject(uri)).collect(Collectors.toList())
        );
    }
}
