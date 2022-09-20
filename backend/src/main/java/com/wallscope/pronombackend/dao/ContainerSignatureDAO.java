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
    public static final String CONTAINER_BYTE_SEQUENCE_SUB_QUERY = ByteSequenceDAO.BYTE_SEQUENCE_SUB_QUERY
            .replaceAll("\\?byteSeq pr:byteSequence\\.InternalSignature \\?sig \\.", "")
            .replaceAll("\\?byteSeq", "?contByteSeq");
    public static final String CONTAINER_SIG_SUB_QUERY = """
            ?contSig a pr:ContainerSignature ;
              rdfs:label ?contSigName ;
              pr:containerSignature.ContainerFile ?contSigFile ;
              pr:containerSignature.FileFormat ?f ;
            .
            OPTIONAL{ ?contSig rdfs:comment ?comment . }#END OPTIONAL
            OPTIONAL{ ?contSig pr:containerSignature.ContainerType ?contSigType . }#END OPTIONAL
            # ContainerFile link
            ?contSigFile a pr:ContainerFile .
            OPTIONAL { ?contSigFile pr:containerFile.ContainerSignature ?contSig . }#END OPTIONAL
            OPTIONAL { ?contSigFile pr:containerFile.FilePath ?contSigFileFilePath . }#END OPTIONAL
            OPTIONAL { ?contSigFile pr:containerFile.ByteSequence ?contByteSeq . }#END OPTIONAL
            """;
    public static final String FORM_CONTAINER_SIG_SUB_QUERY = CONTAINER_SIG_SUB_QUERY.replaceAll("\\?byteSeq", "?contByteSeq") + "\n\n" + CONTAINER_BYTE_SEQUENCE_SUB_QUERY;
    public static final String CONTAINER_SIG_QUERY = PREFIXES + """
            CONSTRUCT {
            # File format representation
            """ + trimOptionals(MINIMAL_FILE_FORMAT_SUB_QUERY)
            .replaceAll("\\?f ff:PuidTypeId/rdfs:label \\?puidTypeName \\.", "?fPuidType rdfs:label ?puidTypeName .") + """
            # Container signatures
            """ + trimOptionals(CONTAINER_SIG_SUB_QUERY) + """
            # Link File Format
            ?f pr:fileFormat.ContainerSignature ?contSig .
            # Byte Sequences
            """ + trimOptionals(CONTAINER_BYTE_SEQUENCE_SUB_QUERY) + """
            } WHERE {
            # File format representation
            """ + MINIMAL_FILE_FORMAT_SUB_QUERY + """
            # Link File Format
            ?f pr:fileFormat.ContainerSignature ?contSig .
            # Container signatures
            """ + CONTAINER_SIG_SUB_QUERY + """
            # Byte Sequences
              OPTIONAL {
              """ + CONTAINER_BYTE_SEQUENCE_SUB_QUERY + """
              }#END OPTIONAL
            }
            """;

    public static final String TRIGGER_FF_QUERY = PREFIXES + """
            prefix ff: <http://www.nationalarchives.gov.uk/PRONOM/fileFormat.>
            CONSTRUCT {
              ?contSigType a pr:ContainerType ; rdfs:label ?contSigTypeLabel ; pr:containerType.FileFormat ?f .
              """ + trimOptionals(MINIMAL_FILE_FORMAT_SUB_QUERY)
            .replaceAll("\\?f ff:PuidTypeId/rdfs:label \\?puidTypeName \\.", "?fPuidType rdfs:label ?puidTypeName .") + """
            }WHERE{
              ?contSigType a pr:ContainerType ; rdfs:label ?contSigTypeLabel ; pr:containerType.FileFormat ?f .
              """ + MINIMAL_FILE_FORMAT_SUB_QUERY + """
            }
            """;

    public List<FileFormat> getAllForContainerSignature() {
        logger.trace("fetching all container signatures");
        Model m = TriplestoreUtil.constructQuery(CONTAINER_SIG_QUERY);
        ModelUtil mu = new ModelUtil(m);
        logger.trace("building container signature objects");
        List<FileFormat> fs = mu.buildAllFromModel(new FileFormat.Deserializer());
        logger.trace("container signatures built");
        return fs;
    }

    public List<ContainerSignature.ContainerType> getTriggerPuids() {
        logger.trace("fetching all container type definitions");
        Model m = TriplestoreUtil.constructQuery(TRIGGER_FF_QUERY);
        ModelUtil mu = new ModelUtil(m);
        logger.trace("building container type definitions");
        List<ContainerSignature.ContainerType> cts = mu.buildAllFromModel(new ContainerSignature.ContainerType.Deserializer());
        logger.trace("container type definitions built");
        return cts;
    }
}
