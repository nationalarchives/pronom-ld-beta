package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class FormatAlias implements RDFWritable {
    private final Resource uri;
    private final String name;
    private final String version;


    public FormatAlias(Resource uri, String name, String version) {
        this.uri = uri;
        this.name = name;
        this.version = version;
    }

    @Override
    public Resource getURI() {
        return uri;
    }

    @Override
    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, makeProp(RDF.type), makeResource(PRONOM.FormatAlias.type));
        if (name != null) m.add(uri, makeProp(RDFS.label), makeLiteral(name));
        if (version != null) m.add(uri, makeProp(PRONOM.FormatAlias.Version), makeLiteral(version));
        return m;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public FormAlias convert() {
        FormAlias fa = new FormAlias();
        fa.setUri(safelyGetUriOrNull(uri));
        fa.setName(name);
        fa.setVersion(version);
        return fa;
    }

    @Override
    public String toString() {
        return "FormatAlias{" +
                "uri=" + uri +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    public static class Deserializer implements RDFDeserializer<FormatAlias> {

        @Override
        public Resource getRDFType() {
            return makeResource(PRONOM.FormatAlias.type);
        }

        @Override
        public FormatAlias fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            String name = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(RDFS.label)));
            String version = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.FormatAlias.Version)));
            return new FormatAlias(uri, name, version);
        }
    }
}
