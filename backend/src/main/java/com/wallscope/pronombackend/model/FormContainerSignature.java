package com.wallscope.pronombackend.model;

import org.apache.jena.rdf.model.Resource;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;
import static com.wallscope.pronombackend.utils.RDFUtil.safelyGetUriOrNull;

public class FormContainerSignature {
    private String uri;
    private String name;
    private String description;
    private String containerType;
    private String fileFormat;
    private ArrayList<FormContainerFile> files;

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

    public ArrayList<FormContainerFile> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<FormContainerFile> files) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static FormContainerSignature convert(ContainerSignature cs) {
        FormContainerSignature fcs = new FormContainerSignature();
        fcs.setName(cs.getName());
        fcs.setDescription(cs.getDescription());
        fcs.setUri(safelyGetUriOrNull(cs.getURI()));
        // fileFormat field is set at the parent
        fcs.setFiles(cs.getFiles().stream().map(f -> {
            FormContainerFile fcf = FormContainerFile.convert(f);
            fcf.setSignature(safelyGetUriOrNull(cs.getURI()));
            return fcf;
        }).collect(Collectors.toCollection(ArrayList::new)));
        return fcs;
    }

    public ContainerSignature toObject(Resource formatUri) {
        Resource res = makeResource(getUri());
        return new ContainerSignature(res,
                getName(),
                getDescription(),
                makeResource(getContainerType()),
                formatUri,
                getFiles().stream().map(f -> f.toObject(res)).collect(Collectors.toList())
        );
    }

    public void removeEmpties() {
        if (files != null) files = files.stream().filter(cf -> {
            cf.removeEmpties();
            return cf.isNotEmpty();
        }).collect(Collectors.toCollection(ArrayList::new));

    }

    public boolean isNotEmpty() {
        return uri != null && !uri.isBlank()
                && name != null && !name.isBlank()
                && files != null && files.stream().anyMatch(FormContainerFile::isNotEmpty);
    }
}
