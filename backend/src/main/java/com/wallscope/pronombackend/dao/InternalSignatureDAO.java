package com.wallscope.pronombackend.dao;

public class InternalSignatureDAO {
    public static final String INTERNAL_SIG_SUB_QUERY = """
            ?sig a pr:InternalSignature ;
              rdfs:label ?sigName ;
              pr:internalSignature.LastUpdatedDate ?sigUpdated ;
              pr:internalSignature.GenericFlag ?sigGenericFlag ;
            .
              OPTIONAL { ?sig pr:internalSignature.Note ?sigNote . }#END OPTIONAL
              OPTIONAL { ?sig pr:internalSignature.Provenance ?sigProvenance . }#END OPTIONAL
              OPTIONAL { ?sig pr:internalSignature.FileFormat ?f . }#END OPTIONAL
              OPTIONAL { ?sig pr:internalSignature.ByteSequence ?byteSeq . }#END OPTIONAL
            """;
    public static final String BYTE_SEQUENCE_SUB_QUERY = ByteSequenceDAO.BYTE_SEQUENCE_SUB_QUERY
            .replaceAll("\\?byteSeq pr:byteSequence.ContainerFile \\?contSigFile \\.", "");
}
