package com.wallscope.pronombackend.dao;

public class InternalSignatureDAO {
    public static final String INTERNAL_SIG_SUB_QUERY = """
            ?sig a pr:InternalSignature ;
              rdfs:label ?sigName ;
              pr:internalSignature.LastUpdatedDate ?sigUpdated ;
              pr:internalSignature.FileFormat ?f ;
            .
              OPTIONAL { ?sig pr:internalSignature.GenericFlag ?sigGenericFlag . }#END OPTIONAL
              OPTIONAL { ?sig pr:internalSignature.Note ?sigNote . }#END OPTIONAL
              OPTIONAL { ?sig pr:internalSignature.Provenance ?sigProvenance . }#END OPTIONAL
              ?sig pr:internalSignature.ByteSequence ?byteSeq .
            """;
    public static final String BYTE_SEQUENCE_SUB_QUERY = ByteSequenceDAO.BYTE_SEQUENCE_SUB_QUERY
            .replaceAll("\\?byteSeq pr:byteSequence.ContainerFile \\?contSigFile \\.", "")
            .replaceAll("\\?byteSeq", "?intByteSeq");
    public static final String FORM_INTERNAL_SIG_SUB_QUERY = INTERNAL_SIG_SUB_QUERY.replaceAll("\\?byteSeq","?intByteSeq") + "\n\n" + BYTE_SEQUENCE_SUB_QUERY;
}
