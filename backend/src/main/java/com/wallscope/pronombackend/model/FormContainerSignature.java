package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Resource;

import java.util.List;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class FormContainerSignature {
    private String uri;
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

    public static FormContainerSignature convert(ContainerSignature cs) {
        FormContainerSignature fcs = new FormContainerSignature();
        fcs.setName(cs.getName());

        // fileFormat field is set at the parent
        fcs.setFiles(cs.getFiles().stream().map(f -> {
            FormContainerFile fcf = FormContainerFile.convert(f);
            fcf.setSignature(cs.getURI().getURI());
            return fcf;
        }).collect(Collectors.toList()));
        return fcs;
    }

    public ContainerSignature toObject(Resource formatUri) {
        Resource res = makeResource(getUri());
        return new ContainerSignature(res,
                getName(),
                makeResource(getContainerType()),
                formatUri,
                files.stream().map(f -> f.toObject(res)).collect(Collectors.toList())
        );
    }
}
