package com.wallscope.pronombackend.dao;

import static com.wallscope.pronombackend.utils.RDFUtil.PREFIXES;
import static com.wallscope.pronombackend.utils.RDFUtil.trimOptionals;

public class ByteSequenceDAO {
    public static final String BYTE_SEQUENCE_SUB_QUERY = """
            ?byteSeq a pr:ByteSequence ;
              pr:byteSequence.ByteSequence ?byteSeqSequence ;
              pr:byteSequence.ByteSequencePosition ?byteSeqPosition ;
            .
              ?byteSeq pr:byteSequence.InternalSignature ?sig .
              ?byteSeq pr:byteSequence.ContainerFile ?contSigFile .

              # Byte Sequence Links
              ?byteSeqPosition rdfs:label ?byteSeqPositionName .

              # Byte Sequence non-required fields
              OPTIONAL {?byteSeq pr:byteSequence.Offset ?byteSeqOffset . }#END OPTIONAL
              OPTIONAL {?byteSeq pr:byteSequence.ByteOrder ?byteSeqByteOrder . }#END OPTIONAL
              OPTIONAL {?byteSeq pr:byteSequence.MaxOffset ?byteSeqMaxOffset . }#END OPTIONAL
              OPTIONAL {?byteSeq pr:byteSequence.IndirectOffsetLocation ?byteSeqIndirectOffsetLocation . }#END OPTIONAL
              OPTIONAL {?byteSeq pr:byteSequence.IndirectOffsetLength ?byteSeqIndirectOffsetLength . }#END OPTIONAL
            """;
    public static final String BYTE_SEQUENCE_QUERY = PREFIXES + """
            CONSTRUCT {
            """ + trimOptionals(BYTE_SEQUENCE_SUB_QUERY) + """
            } WHERE {
            """ + BYTE_SEQUENCE_SUB_QUERY + """
            }
            """;
}
