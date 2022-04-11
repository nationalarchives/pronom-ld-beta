package com.wallscope.pronombackend;

import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.utils.RDFUtil;

public class TestResources {
    static String ExampleFileFormatRDF = """
            @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
            @prefix ff: <http://www.nationalarchives.gov.uk/PRONOM/fileFormat.> .
            @prefix pr: <http://www.nationalarchives.gov.uk/PRONOM/> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

            <http://www.nationalarchives.gov.uk/PRONOM/id/FileFormat/666> a pr:FileFormat ;
              rdfs:comment "Portable Network Graphics (PNG) was designed for the lossless, portable, compressed storage of raster images.  PNG provides a patent-free replacement for GIF and can also replace many common uses of TIFF. Indexed-color, grayscale, and truecolor images are supported, plus an optional alpha channel. Sample depths range from 1 to 16 bits. PNG is designed to work in online viewing applications, so it is fully streamable.  It can store gamma and chromaticity.  PNG also detects file corruption." ;
              rdfs:label "Portable Network Graphics" ;
              ff:BinaryFlag true ;
              ff:LastUpdatedDate "2005-08-02T12:08:55.937"^^xsd:dateTime ;
              ff:Puid 13 ;
              ff:PuidTypeId <http://www.nationalarchives.gov.uk/PRONOM/id/PuidType/1> ;
              ff:Version "1.2" ;
              ff:WithdrawnFlag false ;
              # TODO: Implement in File Format
              #ff:SourceId 1 ;
              #ff:SourceDate "2005-03-11T00:00:00"^^xsd:dateTime ;
              #ff:ByteOrder <http://www.nationalarchives.gov.uk/PRONOM/id/ByteOrder/bigEndian> ;
              #ff:Classification <http://www.nationalarchives.gov.uk/PRONOM/id/Classification/5> ;
              #ff:FormatIdentifier <http://www.nationalarchives.gov.uk/PRONOM/id/FormatIdentifier/199>, <http://www.nationalarchives.gov.uk/PRONOM/id/FormatIdentifier/290> ;
              #ff:In.FileFormatRelationship <http://www.nationalarchives.gov.uk/PRONOM/id/FileFormatRelationship/735>, <http://www.nationalarchives.gov.uk/PRONOM/id/FileFormatRelationship/209>, <http://www.nationalarchives.gov.uk/PRONOM/id/FileFormatRelationship/331> ;
              #ff:InternalSignature <http://www.nationalarchives.gov.uk/PRONOM/id/InternalSignature/186> ;
              .

            # PUID type is provided by the same query that builds a FileFormat entity
            <http://www.nationalarchives.gov.uk/PRONOM/id/PuidType/1>
                    pr:puidType.PuidType  "fmt       " .
            """;
/*    static FileFormat ExampleFileFormat = new FileFormat(
            RDFUtil.makeResource("http://www.nationalarchives.gov.uk/PRONOM/id/FileFormat/666"),
            13,
            RDFUtil.makeResource("http://www.nationalarchives.gov.uk/PRONOM/id/PuidType/1>"),
            "fmt       ",
            "Portable Network Graphics",
            "Portable Network Graphics (PNG) was designed for the lossless, portable, compressed storage of raster images.  PNG provides a patent-free replacement for GIF and can also replace many common uses of TIFF. Indexed-color, grayscale, and truecolor images are supported, plus an optional alpha channel. Sample depths range from 1 to 16 bits. PNG is designed to work in online viewing applications, so it is fully streamable.  It can store gamma and chromaticity.  PNG also detects file corruption.",
            RDFUtil.parseDate(RDFUtil.makeLiteral("2005-08-02T12:08:55.937")),
            "1.2",
            true,
            false,
            classifications, internalSignatures, developmentActors, supportActors);*/
}