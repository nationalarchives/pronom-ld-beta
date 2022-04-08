package com.wallscope.pronombackend;

import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.model.FileFormatDeserializer;
import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.RDFUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


class RDFConversionTest {
//    @Test
//    void fileFormatConvertsFromRDF() {
//        ModelUtil mu = new ModelUtil(TestResources.ExampleFileFormatRDF);
//        List<Resource> s = mu.getSubjects(RDFUtil.makeProp(RDFUtil.PRONOM.FileFormat.type));
//        Model extract = mu.extractModel(s.get(0), null, null);
//        FileFormatDeserializer d = new FileFormatDeserializer();
//        FileFormat f = d.fromModel(s.get(0), mu.getModel());
//        Model fRDF = f.toRDF();
//        assertTrue(extract.isIsomorphicWith(fRDF), () -> "Models are not isomorphic\nExpected:\n" + new ModelUtil(extract) + "\nActual:\n" + new ModelUtil(fRDF));
//    }
}
