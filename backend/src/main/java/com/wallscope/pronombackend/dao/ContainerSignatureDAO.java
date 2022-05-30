package com.wallscope.pronombackend.dao;

import com.wallscope.pronombackend.model.ContainerSignature;
import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.TriplestoreUtil;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.wallscope.pronombackend.dao.FileFormatDAO.MINIMAL_FILE_FORMAT_SUB_QUERY;
import static com.wallscope.pronombackend.utils.RDFUtil.PREFIXES;
import static com.wallscope.pronombackend.utils.RDFUtil.trimOptionals;

public class ContainerSignatureDAO {
    Logger logger = LoggerFactory.getLogger(ContainerSignatureDAO.class);
    public static final String BYTE_SEQUENCE_SUB_QUERY = ByteSequenceDAO.BYTE_SEQUENCE_SUB_QUERY
            .replaceAll("\\?byteSeq pr:byteSequence.InternalSignature \\?sig \\.", "");
    public static final String CONTAINER_SIG_SUB_QUERY = """
            ?contSig a pr:ContainerSignature ;
              rdfs:label ?contSigName ;
              pr:containerSignature.ContainerType ?contSigType ;
              pr:containerSignature.ContainerFile ?contSigFile ;
              pr:containerSignature.FileFormat ?f ;
            .
            # ContainerFile link
            ?contSigFile a pr:ContainerFile .
            OPTIONAL { ?contSigFile pr:containerFile.ContainerSignature ?contSig . }#END OPTIONAL
            OPTIONAL { ?contSigFile pr:containerFile.FilePath ?contSigFileFilePath . }#END OPTIONAL
            OPTIONAL { ?contSigFile pr:containerFile.ByteSequence ?byteSeq . }#END OPTIONAL
            """;
    public static final String CONTAINER_SIG_QUERY = PREFIXES + """
            prefix ff: <http://www.nationalarchives.gov.uk/PRONOM/fileFormat.>
            CONSTRUCT {
            """ + trimOptionals(CONTAINER_SIG_SUB_QUERY) + """
                        
            # Byte Sequences
            """ + trimOptionals(BYTE_SEQUENCE_SUB_QUERY) + """
                        
            # File Format Properties
            """ + trimOptionals(MINIMAL_FILE_FORMAT_SUB_QUERY) + """
            # Link File Format
            ?f pr:fileFormat.ContainerSignature ?contSig .
                        
            } WHERE {
            """ + CONTAINER_SIG_SUB_QUERY + """
            # Byte Sequences
              OPTIONAL {
              """ + BYTE_SEQUENCE_SUB_QUERY + """
            }#END OPTIONAL
                          
                          
            """ + MINIMAL_FILE_FORMAT_SUB_QUERY + """
            # Link File Format
            ?f pr:fileFormat.ContainerSignature ?contSig .
            }
            """;

    public static final String TRIGGER_FF_QUERY = PREFIXES + """
            prefix ff: <http://www.nationalarchives.gov.uk/PRONOM/fileFormat.>
            CONSTRUCT {
              ?contSigType a pr:ContainerType ; rdfs:label ?contSigTypeLabel ; pr:containerType.FileFormat ?f .
              """ + trimOptionals(MINIMAL_FILE_FORMAT_SUB_QUERY) + """
            }WHERE{
              ?contSigType a pr:ContainerType ; rdfs:label ?contSigTypeLabel ; pr:containerType.FileFormat ?f .
              """ + MINIMAL_FILE_FORMAT_SUB_QUERY + """
            }
            """;

    public List<FileFormat> getAllFileFormats() {
        logger.debug("fetching all container signatures");
        Model m = TriplestoreUtil.constructQuery(CONTAINER_SIG_QUERY);
        ModelUtil mu = new ModelUtil(m);
        logger.debug("building container signature objects");
        List<FileFormat> fs = mu.buildAllFromModel(new FileFormat.Deserializer());
        logger.debug("container signatures built");
        return fs;
    }

    public List<ContainerSignature.ContainerType> getTriggerPuids() {
        logger.debug("fetching all container type definitions");
        Model m = TriplestoreUtil.constructQuery(TRIGGER_FF_QUERY);
        ModelUtil mu = new ModelUtil(m);
        logger.debug("building container type definitions");
        List<ContainerSignature.ContainerType> cts = mu.buildAllFromModel(new ContainerSignature.ContainerType.Deserializer());
        logger.debug("container type definitions built");
        return cts;
    }
}
