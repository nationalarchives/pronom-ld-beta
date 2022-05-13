package com.wallscope.pronombackend;

import com.wallscope.pronombackend.model.ByteSequence;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    }

}
