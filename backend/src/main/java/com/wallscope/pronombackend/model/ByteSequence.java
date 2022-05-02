package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class ByteSequence implements RDFWritable {
    Logger logger = LoggerFactory.getLogger(ByteSequence.class);
    private final Resource uri;
    private final Resource signature;
    private Resource position;
    private final String positionName;
    private final Integer offset;
    private final String sequence;
    private final Resource byteOrder;
    private final Integer maxOffset;
    private final Integer indirectOffsetLocation;
    private final Integer indirectOffsetLength;

    public ByteSequence(Resource uri, Resource signature, Resource position, String positionName, Integer offset, String sequence, Resource byteOrder, Integer maxOffset, Integer indirectOffsetLocation, Integer indirectOffsetLength) {
        this.uri = uri;
        this.signature = signature;
        this.position = position;
        this.positionName = positionName;
        this.offset = offset;
        this.sequence = sequence;
        this.byteOrder = byteOrder;
        this.maxOffset = maxOffset;
        this.indirectOffsetLocation = indirectOffsetLocation;
        this.indirectOffsetLength = indirectOffsetLength;
    }

    public Integer getOffset() {
        return offset;
    }

    public Resource getPosition() {
        return position;
    }

    public String getPositionName() {
        return positionName;
    }

    public Resource getSignature() {
        return signature;
    }

    public Resource getByteOrder() {
        return byteOrder;
    }

    public String getSequence() {
        return sequence;
    }

    public Integer getIndirectOffsetLength() {
        return indirectOffsetLength;
    }

    public Integer getIndirectOffsetLocation() {
        return indirectOffsetLocation;
    }

    public Integer getMaxOffset() {
        return maxOffset;
    }

    public String getReference() {
        if (position == null) return null;
        return switch (position.getURI()) {
            case PRONOM.ByteSequence.AbsoluteFromBOF, PRONOM.ByteSequence.IndirectFromBOF -> "BOFoffset";
            case PRONOM.ByteSequence.AbsoluteFromEOF, PRONOM.ByteSequence.IndirectFromEOF -> "EOFoffset";
            case PRONOM.ByteSequence.Variable -> "Variable";
            default -> null;
        };
    }

    public boolean isBOFOffset() {
        return switch (position.getURI()) {
            case PRONOM.ByteSequence.AbsoluteFromBOF, PRONOM.ByteSequence.IndirectFromBOF -> true;
            default -> false;
        };
    }

    public boolean isEOFOffset() {
        return switch (position.getURI()) {
            case PRONOM.ByteSequence.AbsoluteFromEOF, PRONOM.ByteSequence.IndirectFromEOF -> true;
            default -> false;
        };
    }

    public String getByteOrderName() {
        if (byteOrder == null) return null;
        return switch (byteOrder.getURI()) {
            case PRONOM.ByteOrder.littleEndian -> "Little-endian";
            case PRONOM.ByteOrder.bigEndian -> "Big-endian";
            default -> null;
        };
    }

    @Override
    public Resource getURI() {
        return uri;
    }

    @Override
    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, makeProp(RDF.type), makeResource(PRONOM.ByteSequence.type));
        m.add(uri, makeProp(PRONOM.ByteSequence.ByteSequencePosition), position);
        m.add(uri, makeProp(PRONOM.ByteSequence.Offset), makeLiteral(offset));
        m.add(uri, makeProp(PRONOM.ByteSequence.ByteSequence), makeLiteral(sequence));
        m.add(uri, makeProp(PRONOM.ByteSequence.MaxOffset), makeLiteral(maxOffset));
        m.add(uri, makeProp(PRONOM.ByteSequence.InternalSignature), signature);
        m.add(uri, makeProp(PRONOM.ByteSequence.ByteOrder), byteOrder);
        m.add(uri, makeProp(PRONOM.ByteSequence.IndirectOffsetLocation), makeLiteral(indirectOffsetLocation));
        m.add(uri, makeProp(PRONOM.ByteSequence.IndirectOffsetLength), makeLiteral(indirectOffsetLength));
        return m;
    }

    @Override
    public String toString() {
        return "ByteSequence{" +
                "uri=" + uri +
                ", signature=" + signature +
                ", position=" + position +
                ", positionName='" + positionName + '\'' +
                ", offset=" + offset +
                ", sequence='" + sequence + '\'' +
                ", byteOrder=" + byteOrder +
                ", maxOffset=" + maxOffset +
                ", indirectOffsetLocation=" + indirectOffsetLocation +
                ", indirectOffsetLength=" + indirectOffsetLength +
                '}';
    }

    private static final String fragWildcardsRegex = "\\{(?<offset>[\\d-]+)\\}";

    public List<SubSequence> getSubSequences() {
        //System.out.println();
        //System.out.println();
        //System.out.println("Initial Sequence ===");
        //System.out.println(this.sequence);
        // Split on * because they are wildcards, and we get fragments to each side
        // Before splitting we replace instances of {0-*} with * and -*} with }*
        // This normalises the wildcards in the sequence and makes it easier to handle
        // offsets and wildcards
        String normalised = sequence.replaceAll("\\{0-\\*\\}", "*").replaceAll("-\\*\\}", "}*");
        String[] parts = normalised.split("\\*");
        ArrayList<SubSequence> parsedSubSeqs = new ArrayList<>();
        for (int i = 0; i < parts.length; i++) {
            String ss = parts[i];
            //System.out.println("Step 1 ===");
            //System.out.println(ss);
            //System.out.println("Step 2 ===");
            // find {5} or {4-7} style offset groups
            String startMatch = getOffsetMatches("^" + fragWildcardsRegex, ss).stream().findFirst().orElse(null);
            String clean = ss.replaceAll("^" + fragWildcardsRegex, "");
            //System.out.println(clean);
            //System.out.println("START OFFSET: " + startMatch);
            //System.out.println("Step 3 ===");
            Integer minOffset = i == 0 ? this.offset : null;
            Integer maxOffset = null;
            if (startMatch != null) {
                try {
                    if (startMatch.contains("-")) {
                        String[] sMParts = startMatch.split("-");
                        minOffset = Integer.parseInt(sMParts[0]);
                        maxOffset = Integer.parseInt(sMParts[1]);
                    } else {
                        minOffset = Integer.parseInt(startMatch);
                    }
                    if (isBOFOffset() && i == 0) {
                        this.position = makeResource(PRONOM.ByteSequence.Variable);
                    }
                } catch (Exception ignored) {
                    logger.debug("GOT EXCEPTION PARSING OFFSETS: " + ignored);
                }
            }
            //System.out.println("PARSED [MIN,MAX] OFFSETS: [" + minOffset + "," + maxOffset + "]");
            //System.out.println("Step 4 ===");
            // split the sub-sequence into unambiguous parts and find the longest
            List<LongestSubSeqMatches> longestCandidates = processLongestSubSeqMatches(clean);
            //System.out.println(longestCandidates);
            LongestSubSeqMatches longest = longestCandidates.stream().max(Comparator.comparingInt(m -> m.sequence.length())).orElse(null);
            if (longest == null) continue;
            //System.out.println("LONGEST: " + longest);
            List<Fragment> frags = processFragments(clean, longest);
            //System.out.println("GETTING FRAGMENTS: " + frags);
            SubSequence subSeq = new SubSequence(longest.sequence, null, null, frags, null, i + 1, maxOffset, minOffset);
            //System.out.println("ADDING SUBSEQUENCE: " + subSeq);
            parsedSubSeqs.add(subSeq);
        }

        return parsedSubSeqs;
    }

    private List<String> getOffsetMatches(String needle, String haystack) {
        Pattern p = Pattern.compile(needle);
        Matcher m = p.matcher(haystack);
        ArrayList<String> matches = new ArrayList<>();
        while (m.find()) {
            if (m.groupCount() > 0) {
                matches.add(m.group(1));
            }
        }
        return matches;
    }

    private static final Pattern longestUnambiguousRegex = Pattern.compile(
            "(\\?\\?)" + //            Match ?? groups
                    "|(\\([^\\)]+\\))" + //    Match (...) groups
                    "|(\\{[^\\}]+\\})" + //    Match {...} groups
                    "|(\\[[^\\]]+\\])" + //    Match [...] groups
                    "|(?<longest>[A-F0-9]+)"// Finally, match HEX Byte characters
    ); // The trick here is that whatever is NOT captured by first 4 groups (i.e: surrounded by brackets)
    // will get captured by the last one, which only allows unambiguous sequence characters.
    // This gives us the longest unambiguous HEX sequence on each sub-sequence.
    // Inside the bracket groups, the way it works is by matching the opening bracket and then matching
    // 1 or more characters that are NOT a closing bracket: [^}]+
    // '.*' Can not be used as it will skip brackets in the middle in strings like:
    // {5}080287F12D{4-8}

    private List<LongestSubSeqMatches> processLongestSubSeqMatches(String sequence) {
        Matcher m = longestUnambiguousRegex.matcher(sequence);
        ArrayList<LongestSubSeqMatches> matches = new ArrayList<>();
        while (m.find()) {
            String match = m.group("longest");
            if (match == null || match.isEmpty()) continue;
            Integer start = m.start("longest");
            Integer end = m.end("longest");
            matches.add(new LongestSubSeqMatches(match, start, end));
        }
        return matches;
    }

    private static final Pattern fragmentCaptureRegex = Pattern.compile("(?<group>[^\\{\\}]+(?:\\{[^\\}]+\\})*)");

    private List<Fragment> processFragments(String sequence, LongestSubSeqMatches longest) {
        // Get Left part of string taking the sequence string until the longest part.
        String left = sequence.substring(0, longest.start);
        //System.out.println("LEFT PART: " + left);
        Matcher lm = fragmentCaptureRegex.matcher(left);
        ArrayList<Fragment> frags = new ArrayList<>();
        Integer positionCounter = 0;
        while (lm.find()) {
            String match = lm.group("group");
            if (match == null || match.isEmpty()) continue;
            List<Fragment> fs = fragStringToFragments(match, positionCounter, FragmentType.LEFT);
            if (fs != null && !fs.isEmpty()) {
                positionCounter = fs.get(fs.size() - 1).position;
            }
            frags.addAll(fs);
        }
        Collections.reverse(frags);
        // Reverse the positions in the left fragments because we are supposed to count from right to left
        positionCounter = 0;
        Integer lastPosition = 1;
        for (Fragment f : frags) {
            Integer initialPosition = f.position;
            if (!initialPosition.equals(lastPosition)) {
                positionCounter++;
            }
            f.setPosition(positionCounter);
            lastPosition = initialPosition;
        }


        String right = sequence.substring(longest.end);
        //System.out.println("RIGHT PART: " + right);
        Matcher rm = fragmentCaptureRegex.matcher(right);
        positionCounter = 0;
        while (rm.find()) {
            String match = rm.group("group");
            if (match == null || match.isEmpty()) continue;
            List<Fragment> fs = fragStringToFragments(match, positionCounter, FragmentType.RIGHT);
            if (fs != null && !fs.isEmpty()) {
                positionCounter = fs.get(fs.size() - 1).position;
            }
            frags.addAll(fs);
        }
        return frags;
    }

    private static final Pattern offsetSequenceRegex = Pattern.compile("(?<sequence>[^\\{\\}]+)(?:\\{(?<offset>[^\\}]+)\\})*");
    private static final Pattern orSeparatorRegex = Pattern.compile("(?<rest>[^\\(]*)\\((?<or>[^\\)]+)\\)");

    private List<Fragment> fragStringToFragments(String sequence, Integer index, FragmentType type) {
        ArrayList<Fragment> frags = new ArrayList<>();
        Integer minOffset = null;
        Integer maxOffset = null;
        Matcher mOffset = offsetSequenceRegex.matcher(sequence);
        // This should always match once, with or without offset, but we early return if not anyway to avoid potential disasters
        if (!mOffset.find()) return null;
        String ofMatch = mOffset.group("offset");
        if (ofMatch != null && !ofMatch.isBlank()) {
            if (ofMatch.contains("-")) {
                String[] cParts = ofMatch.split("-");
                minOffset = Integer.parseInt(cParts[0]);
                maxOffset = Integer.parseInt(cParts[1]);
            } else {
                minOffset = Integer.parseInt(ofMatch);
                maxOffset = minOffset;
            }
        }
        String rest = mOffset.group("sequence");
        Matcher mOr = orSeparatorRegex.matcher(rest);
        Integer positionCounter = index;
        while (mOr.find()) {
            String orMatch = mOr.group("or");
            String newRestMatch = mOr.group("rest");
            if (!newRestMatch.isBlank()) {
                positionCounter++;
                frags.add(new Fragment(type, newRestMatch, maxOffset, minOffset, positionCounter));
            }
            String[] ors = orMatch.split("\\|");
            if (ors.length > 0) {
                positionCounter++;
                for (String or : ors) {
                    frags.add(new Fragment(type, or, maxOffset, minOffset, positionCounter));
                }
            }
        }
        // if frags is empty after this it's because there are no ors, so we just add the rest from before the orMatch
        if (frags.isEmpty() && !rest.isEmpty()) {
            frags.add(new Fragment(type, rest, maxOffset, minOffset, index));
        }
        //System.out.println("LAST FRAG: " + frags.get(frags.size() - 1));
        return frags;
    }

    private class LongestSubSeqMatches {
        public String sequence;
        public Integer start;
        public Integer end;

        public LongestSubSeqMatches(String sequence, Integer start, Integer end) {
            this.sequence = sequence;
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return "[" + start + "," + end + "]: " + sequence;
        }
    }

    public class SubSequence {
        private final String sequence;
        private final Integer defaultShift;
        private final List<Shift> shifts;
        private final List<Fragment> fragments;
        private final Integer minFragLength;
        private final Integer position;
        private final Integer subSeqMaxOffset;
        private final Integer subSeqMinOffset;

        public SubSequence(String sequence, Integer defaultShift, List<Shift> shifts, List<Fragment> fragments, Integer minFragLength, Integer position, Integer subSeqMaxOffset, Integer subSeqMinOffset) {
            this.sequence = sequence;
            this.defaultShift = defaultShift;
            this.shifts = shifts;
            this.fragments = fragments;
            this.minFragLength = minFragLength;
            this.position = position;
            this.subSeqMaxOffset = subSeqMaxOffset;
            this.subSeqMinOffset = subSeqMinOffset;
        }

        public String getSequence() {
            return sequence;
        }

        public Integer getDefaultShift() {
            return defaultShift;
        }

        public List<Shift> getShifts() {
            return shifts;
        }

        public List<Fragment> getFragments() {
            return fragments;
        }

        public Integer getMinFragLength() {
            return minFragLength;
        }

        public Integer getPosition() {
            return position;
        }

        public Integer getSubSeqMaxOffset() {
            return subSeqMaxOffset;
        }

        public Integer getSubSeqMinOffset() {
            return subSeqMinOffset;
        }

        @Override
        public String toString() {
            return "SubSequence{" +
                    "sequence='" + sequence + '\'' +
                    ", defaultShift=" + defaultShift +
                    ", shifts=" + shifts +
                    ", minFragLength=" + minFragLength +
                    ", position=" + position +
                    ", subSeqMaxOffset=" + subSeqMaxOffset +
                    ", subSeqMinOffset=" + subSeqMinOffset +
                    ", fragments=" + fragments +
                    '}';
        }
    }

    public class Shift {
        private final String byteVal;
        private final String value;

        public Shift(String byteVal, String value) {
            this.byteVal = byteVal;
            this.value = value;
        }

        public String getByteVal() {
            return byteVal;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Shift{" +
                    "byteVal='" + byteVal + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public enum FragmentType {LEFT, RIGHT}

    public class Fragment {
        private final FragmentType type;
        private final String value;
        private final Integer maxOffset;
        private final Integer minOffset;
        private Integer position;

        public Fragment(FragmentType type, String value, Integer maxOffset, Integer minOffset, Integer position) {

            this.type = type;
            this.value = value;
            this.maxOffset = maxOffset;
            this.minOffset = minOffset;
            this.position = position;
        }

        public FragmentType getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public Integer getMaxOffset() {
            return maxOffset;
        }

        public Integer getMinOffset() {
            return minOffset;
        }

        public Integer getPosition() {
            return position;
        }

        public void setPosition(Integer p) {
            position = p;
        }

        @Override
        public String toString() {
            return "Fragment{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    ", maxOffset=" + maxOffset +
                    ", minOffset=" + minOffset +
                    ", position=" + position +
                    '}';
        }
    }

    public static class Deserializer implements RDFDeserializer<ByteSequence> {
        @Override
        public Resource getRDFType() {
            return makeResource(PRONOM.ByteSequence.type);
        }

        @Override
        public ByteSequence fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            // Required
            Resource signature = mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.InternalSignature)).asResource();
            String sequence = mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.ByteSequence)).asLiteral().getString();
            Resource position = mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.ByteSequencePosition)).asResource();
            String positionName = mu.getOneObjectOrNull(position, makeProp(RDFS.label)).asLiteral().getString();
            // Optionals
            Integer offset = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.Offset)));
            Resource byteOrder = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.ByteOrder)));
            Integer maxOffset = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.MaxOffset)));
            Integer indirectOffsetLocation = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.IndirectOffsetLocation)));
            Integer indirectOffsetLength = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.IndirectOffsetLocation)));

            return new ByteSequence(uri, signature, position, positionName, offset, sequence, byteOrder, maxOffset, indirectOffsetLocation, indirectOffsetLength);
        }
    }
}
