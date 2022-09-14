package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class ContainerFile implements RDFWritable {
    private final Resource uri;
    private final Resource signature;
    private final String path;
    private final List<ByteSequence> byteSequences;

    public ContainerFile(Resource uri, Resource signature, String path, List<ByteSequence> byteSequences) {
        this.uri = uri;
        this.path = path;
        this.signature = signature;
        this.byteSequences = byteSequences;
    }

    public String getID() {
        String[] parts = uri.getURI().split("/");
        return parts[parts.length - 1];
    }

    public List<ByteSequence> getByteSequences() {
        return byteSequences;
    }

    public Resource getSignature() {
        return signature;
    }

    public String getPath() {
        return path;
    }

    @Override
    public Resource getURI() {
        return this.uri;
    }

    @Override
    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, makeProp(RDF.type), makeResource(PRONOM.ContainerFile.type));
        if (signature != null) m.add(uri, makeProp(PRONOM.ContainerFile.ContainerSignature), signature);
        if (path != null) m.add(uri, makeProp(PRONOM.ContainerFile.FilePath), makeLiteral(path));
        if (byteSequences != null) {
            byteSequences.forEach(bs -> {
                m.add(uri, makeProp(PRONOM.ContainerFile.ByteSequence), bs.getURI());
                Model bsM = bs.toRDF();
                bsM.removeAll(bs.getURI(), makeProp(PRONOM.ByteSequence.InternalSignature), null);
                bsM.add(bs.getURI(), makeProp(PRONOM.ByteSequence.ContainerFile), uri);
                m.add(bsM);
            });
        }
        return m;
    }

    @Override
    public String toString() {
        return "ContainerFile{" +
                "uri=" + uri +
                ", signature=" + signature +
                ", path='" + path + '\'' +
                ", byteSequences=" + byteSequences +
                '}';
    }

    public static class Deserializer implements RDFDeserializer<ContainerFile> {
        @Override
        public Resource getRDFType() {
            return makeResource(PRONOM.ContainerFile.type);
        }

        @Override
        public ContainerFile fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            // Required
            Resource signature = mu.getOneObjectOrNull(uri, makeProp(PRONOM.ContainerFile.ContainerSignature)).asResource();
            // Optional
            String path = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ContainerFile.FilePath)));
            List<Resource> byteSeqSubjects = mu.getAllObjects(uri, makeProp(PRONOM.ContainerFile.ByteSequence)).stream().map(RDFNode::asResource).collect(Collectors.toList());
            List<ByteSequence> byteSequences = mu.buildFromModel(new ByteSequence.Deserializer(), byteSeqSubjects)
                    .stream().sorted(Comparator.comparingInt(bs -> Integer.parseInt(bs.getContainerID()))).collect(Collectors.toList());

            return new ContainerFile(uri, signature, path, byteSequences);
        }
    }
}
