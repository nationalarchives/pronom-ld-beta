package com.wallscope.pronombackend.dao;

import static com.wallscope.pronombackend.utils.RDFUtil.PREFIXES;
import static com.wallscope.pronombackend.utils.RDFUtil.trimOptionals;

public class InternalSignatureDAO {
    public static final String SIGNATURE_SUB_QUERY = """
            ?sig a pr:InternalSignature ;
              rdfs:label ?sigName ;
              pr:internalSignature.LastUpdatedDate ?sigUpdated ;
              pr:internalSignature.GenericFlag ?sigGenericFlag ;
            .
              OPTIONAL { ?sig pr:internalSignature.Note ?sigNote . }#END OPTIONAL
              OPTIONAL { ?sig pr:internalSignature.Provenance ?sigProvenance . }#END OPTIONAL
              OPTIONAL { ?sig pr:internalSignature.FileFormat ?sigFf . }#END OPTIONAL
              OPTIONAL { ?sig pr:internalSignature.ByteSequence ?byteSeq .
                
                """ + ByteSequenceDAO.BYTE_SEQUENCE_SUB_QUERY + """
              }#END OPTIONAL
            """;
    public static final String SIGNATURE_QUERY = PREFIXES + """
            CONSTRUCT {
            """ + trimOptionals(SIGNATURE_SUB_QUERY) + """
            } WHERE {
            """ + SIGNATURE_SUB_QUERY + """
            }
            """;
}
