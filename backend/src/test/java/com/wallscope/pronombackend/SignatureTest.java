package com.wallscope.pronombackend;

import com.google.common.io.Resources;
import com.wallscope.pronombackend.model.ByteSequence;
import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;

import javax.xml.transform.Source;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SignatureTest {
    @Test
    void generatesSubsequences() {
        System.out.println("---------------------SIMPLE EXAMPLE----------------------------");
        List<ByteSequence.SubSequence> seqs = TestResources.SimpleExampleByteSequence.getSubSequences();
        System.out.println(seqs);
        System.out.println("--------------------COMPLEX EXAMPLE----------------------------");
        seqs = TestResources.ExampleByteSequence.getSubSequences();
        System.out.println(seqs);
        System.out.println("---------------------------------------------------------------");
        System.out.println("---------------------OTHER EXAMPLE-----------------------------");
        seqs = TestResources.OtherExampleByteSequence.getSubSequences();
        System.out.println(seqs);
        System.out.println("---------------------------------------------------------------");
        System.out.println("----------------------OFFSET TEST------------------------------");
        seqs = TestResources.OffsetTestByteSequence.getSubSequences();
        System.out.println(seqs);
        System.out.println("---------------------------------------------------------------");
        System.out.println("------------------FURTHER OFFSET TEST--------------------------");
        seqs = TestResources.FurtherOffsetTestBS.getSubSequences();
        System.out.println(seqs);
        System.out.println("---------------------------------------------------------------");
    }
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate testTemplate;


    @Test
    void compareToSigFile104() throws IOException {
//        String resultXml = testTemplate.withBasicAuth("pronom","").getForObject("http://localhost:" + port + "/signature.xml", String.class);
        String resultXml = Resources.toString(Resources.getResource("signature.xml"), StandardCharsets.UTF_8);
        Source xml = Input.fromString(resultXml).build();
        System.out.println("RESULT LENGTH: " + resultXml.length());
        Source sigFile = Input.fromString(Resources.toString(Resources.getResource("DROID_SignatureFile_V104.xml"), StandardCharsets.UTF_8)).build();

        List<String> ignoredTags = List.of("Shift", "DefaultShift", "FileFormatCollection");

        Diff d = DiffBuilder.compare(xml).withTest(sigFile)
                .normalizeWhitespace()
                .ignoreComments()
                .ignoreWhitespace()
                .checkForSimilar()
                .withNodeFilter(node -> !ignoredTags.contains(node.getNodeName()))
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAttributes("ID", "Position"), ElementSelectors.byNameAndText, ElementSelectors.byName))
                .build();

        System.out.println("----------------------------DIFF-------------------------------");
        System.out.println(d.fullDescription());
        System.out.println("---------------------------------------------------------------");
        assertFalse(d.hasDifferences());
    }

}
