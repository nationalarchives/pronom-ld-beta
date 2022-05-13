package com.wallscope.pronombackend;

import com.wallscope.pronombackend.model.ByteSequence;

import static com.wallscope.pronombackend.utils.RDFUtil.PRONOM;
import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

public class TestResources {
    public static String ExampleFileFormatRDF = """
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
    public static ByteSequence ExampleByteSequence = new ByteSequence(
            makeResource(PRONOM.uri + "id/ByteSequence/124"),
            makeResource(PRONOM.uri + "id/InternalSignature/26"),
            makeResource(PRONOM.uri + "id/ByteSequencePosition/1"),
            "Absolute from BOF",
            0,
            "7B5C7274(66|6631)5C(616E7369|6D6163|7063|706361)5C616E7369637067{3-*}5C737473686664626368{1-4}5C73747368666C6F6368{1-4}5C737473686668696368{1-4}5C73747368666269",
            null,
            null,
            null,
            null
    );

    public static ByteSequence OtherExampleByteSequence = new ByteSequence(
            makeResource(PRONOM.uri + "id/ByteSequence/FAKE"),
            makeResource(PRONOM.uri + "id/InternalSignature/FAKE"),
            makeResource(PRONOM.ByteSequence.AbsoluteFromBOF),
            "Absolute from BOF",
            0,
            "3C3F786D6C2076657273696F6E3D(22|27)312E30(22|27){0-320}786D6C6E733A78646F6D65613D(22|27)75726E3A786F65762D64653A78646F6D65613A736368656D613A322E332E30(22|27)",
            null,
            null,
            null,
            null
    );

    public static ByteSequence SimpleExampleByteSequence = new ByteSequence(makeResource(PRONOM.uri + "id/ByteSequence/124"),
            makeResource(PRONOM.uri + "id/InternalSignature/26"),
            makeResource(PRONOM.uri + "id/ByteSequencePosition/1"),
            "Absolute from BOF",
            10,
            "A1A2A3[A4:A5]??B1B2B3(B4|B5)*{5}01??C1C2C3{4-7}D1????F1(F2|F3)F4F5",
            null,
            null,
            null,
            null
    );

    public static ByteSequence OffsetTestByteSequence = new ByteSequence(makeResource(PRONOM.uri + "id/ByteSequence/738"),
            makeResource(PRONOM.uri + "id/InternalSignature/596"),
            makeResource(PRONOM.uri + "id/ByteSequencePosition/1"),
            "Absolute from BOF",
            0,
            "(73|2073)6F6C696420*6661636574206E6F726D616C20{0-200}6F75746572206C6F6F70{0-20}76657274657820",
            null,
            null,
            null,
            null
    );
}