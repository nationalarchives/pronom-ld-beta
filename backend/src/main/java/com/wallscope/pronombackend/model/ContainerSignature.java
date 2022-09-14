package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class ContainerSignature implements RDFWritable, Comparable<ContainerSignature> {
    private final Resource uri;
    private final String name;
    private final Resource containerType;
    private final Resource fileFormat;
    private final List<ContainerFile> files;

    public ContainerSignature(Resource uri, String name, Resource containerType, Resource fileFormat, List<ContainerFile> files) {
        this.uri = uri;
        this.name = name;
        this.containerType = containerType;
        this.fileFormat = fileFormat;
        this.files = files;
    }

    public String getID() {
        String[] parts = uri.getURI().split("/");
        return parts[parts.length - 1];
    }

    public String getContainerTypeName() {
        String[] parts = containerType.getURI().split("/");
        return parts[parts.length - 1];
    }

    public String getName() {
        return name;
    }

    public List<ContainerFile> getFiles() {
        return files;
    }

    public Resource getFileFormat() {
        return fileFormat;
    }

    public Resource getContainerType() {
        return containerType;
    }

    @Override
    public Resource getURI() {
        return this.uri;
    }

    @Override
    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, makeProp(RDF.type), makeResource(PRONOM.ContainerSignature.type));
        m.add(uri, makeProp(RDFS.label), makeLiteral(name));
        m.add(uri, makeProp(PRONOM.ContainerSignature.FileFormat), fileFormat);
        for (ContainerFile cf : files) {
            m.add(uri, makeProp(PRONOM.ContainerSignature.ContainerFile), cf.getURI());
            m.add(cf.toRDF());
        }

        return m;
    }

    @Override
    public String toString() {
        return "ContainerSignature{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                ", containerType=" + containerType +
                ", fileFormat=" + fileFormat +
                ", files=" + files +
                '}';
    }

    @Override
    public int compareTo(ContainerSignature b) {
        boolean aNull = this.getURI() == null;
        boolean bNull = b.getURI() == null;
        if (aNull && !bNull) {
            return -1;
        } else if (bNull && !aNull) {
            return 1;
        } else if (aNull && bNull) {
            return 0;
        }

        int aInt = NumberUtils.toInt(this.getURI().getLocalName(), Integer.MAX_VALUE);
        int bInt = NumberUtils.toInt(b.getURI().getLocalName(), Integer.MAX_VALUE);
        return aInt - bInt;
    }

    public ContainerSignature replaceFileFormat(Resource newUri) {
        return new ContainerSignature(uri, name, containerType, newUri, files);
    }

    public static class Deserializer implements RDFDeserializer<ContainerSignature> {
        @Override
        public Resource getRDFType() {
            return makeResource(PRONOM.ContainerSignature.type);
        }

        @Override
        public ContainerSignature fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            // Required
            String name = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(RDFS.label)));
            Resource containerType = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ContainerSignature.ContainerType)));
            Resource fileFormat = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ContainerSignature.FileFormat)));
            // Optional
            List<Resource> fileSubjects = mu.getAllObjects(uri, makeProp(PRONOM.ContainerSignature.ContainerFile)).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<ContainerFile> files = mu.buildFromModel(new ContainerFile.Deserializer(), fileSubjects)
                    .stream().sorted(Comparator.comparing(ContainerFile::getID)).collect(Collectors.toList());

            return new ContainerSignature(uri, name, containerType, fileFormat, files);
        }
    }

    public static class ContainerType implements RDFWritable {
        private final Resource uri;
        private final String name;
        private final List<FileFormat> fileFormats;

        public ContainerType(Resource uri, String name, List<FileFormat> fileFormats) {
            this.uri = uri;
            this.name = name;
            this.fileFormats = fileFormats;
        }

        @Override
        public Resource getURI() {
            return uri;
        }

        @Override
        public Model toRDF() {
            Model m = ModelFactory.createDefaultModel();
            m.add(uri, makeProp(RDFS.label), makeLiteral(name));
            this.fileFormats.forEach(ff -> m.add(uri, makeProp(PRONOM.ContainerType.FileFormat), ff.getURI()));
            return m;
        }

        public String getName() {
            return name;
        }

        public List<FileFormat> getFileFormats() {
            return fileFormats;
        }

        public static class Deserializer implements RDFDeserializer<ContainerType> {

            @Override
            public Resource getRDFType() {
                return makeResource(PRONOM.ContainerType.type);
            }

            @Override
            public ContainerType fromModel(Resource uri, Model model) {
                ModelUtil mu = new ModelUtil(model);
                // Required
                String name = mu.getOneObjectOrNull(uri, makeProp(RDFS.label)).asLiteral().getString();
                List<Resource> ffUris = mu.getAllObjects(uri, makeProp(PRONOM.ContainerType.FileFormat)).stream()
                        .map(RDFNode::asResource).collect(Collectors.toList());
                // Build attached file formats
                List<FileFormat> ffs = mu.buildFromModel(new FileFormat.Deserializer(), ffUris);

                return new ContainerType(uri, name, ffs);
            }
        }
    }
}
