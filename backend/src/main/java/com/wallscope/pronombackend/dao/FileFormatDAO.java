package com.wallscope.pronombackend.dao;

import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.utils.RDFUtil;
import com.wallscope.pronombackend.utils.TriplestoreUtil;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class FileFormatDAO {
    Logger logger = LoggerFactory.getLogger(FileFormatDAO.class);
    public static final String FILE_FORMAT_QUERY = RDFUtil.PREFIXES + """
            prefix ff: <http://www.nationalarchives.gov.uk/PRONOM/fileFormat.>
            CONSTRUCT {
              ?f a pr:FileFormat ;
                ff:Puid ?puid ;
                rdfs:label ?label ;
                rdfs:comment ?comment ;
                ff:LastUpdatedDate ?updated ;
                ff:Version ?version ;
                ff:BinaryFlag ?binaryFlag ;
                ff:WithdrawnFlag ?withdrawn ;
                .
            } WHERE {
              ?f a pr:FileFormat ;
                ff:Puid ?puid ;
                rdfs:label ?label ;
                rdfs:comment ?comment ;
                ff:LastUpdatedDate ?updated ;
                ff:Version ?version ;
                .
                OPTIONAL { ?f ff:BinaryFlag ?binaryFlag }
                OPTIONAL { ?f ff:WithdrawnFlag ?withdrawn }
            }
            """;

    public FileFormat getFileFormatByPuid(String puid) {
        Map<String, RDFNode> params = new HashMap<>();
        params.put("puid", RDFUtil.makeLiteral(puid, XSDDatatype.XSDinteger));
        Model m = TriplestoreUtil.constructQuery(FILE_FORMAT_QUERY, params);
        Resource subject = m.listSubjectsWithProperty(RDFUtil.makeProp(RDFUtil.RDF.type), FileFormat.FileFormatDeserializer.getRDFType()).nextResource();
        if (subject == null) return null;
        return FileFormat.FileFormatDeserializer.fromModel(subject, m);
    }
}
