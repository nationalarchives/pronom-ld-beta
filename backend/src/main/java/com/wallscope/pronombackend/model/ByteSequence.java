package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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

    public String getID() {
        String[] parts = uri.getURI().split("/");
        return parts[parts.length - 1];
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

    private static final String fragOffsetRegex = "\\{(?<offset>[\\d-]+)\\}";

    public List<SubSequence> getSubSequences() {
        // Split on * because they are wildcards, and we get fragments to each side
        // Before splitting we replace instances of {0-*} with * and -*} with }*
        // This normalises the wildcards in the sequence and makes it easier to handle
        // offsets and wildcards
        String normalised = sequence.replaceAll("\\{0-\\*\\}", "*").replaceAll("-\\*\\}", "}*");
        String[] parts = normalised.split("\\*");
        ArrayList<SubSequence> parsedSubSeqs = new ArrayList<>();
        for (int i = 0; i < parts.length; i++) {
            String ss = parts[i];
            // find {5} or {4-7} style offset groups at the start of the string
            // These indicate the overall subsequence offsets
            // if the sequence is relative to EOF we check at the end of the string instead
            String offsetPattern = isEOFOffset() ? fragOffsetRegex + "$" : "^" + fragOffsetRegex;
            String startMatch = getOffsetMatches(offsetPattern, ss).stream().findFirst().orElse(null);
            String clean = ss.replaceAll(offsetPattern, "");

            Integer subSeqMinOffset = i == 0 && this.offset != null ? this.offset : 0;
            Integer subSeqMaxOffset = i == 0 && this.maxOffset != null ? this.maxOffset + subSeqMinOffset : 0;
            if (startMatch != null) {
                Integer[] offsets = processOffsets(startMatch);
                subSeqMinOffset = offsets[0];
                subSeqMaxOffset = offsets[1];
                if (isBOFOffset() && i == 0) {
                    this.position = makeResource(PRONOM.ByteSequence.Variable);
                }
            }

            // Find ?? and replace them with {1} which is essentially the same
            clean = clean.replaceAll("(?:^\\?\\?)*(?:\\?\\?$)*", "").replaceAll("\\?\\?", "{1}");
            // Process the clean sub sequence
            SubSequence subSeq = processSubSequence(clean, subSeqMinOffset, subSeqMaxOffset, i + 1);
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

    public static final Pattern sequencePartsRegex = Pattern.compile(
            "(?<r>\\([^\\)]+\\))" + //    Match (...) groups
                    "|(?<c>\\{[^\\}]+\\})" + //    Match {...} groups
                    "|(?<s>\\[[^\\]]+\\])" + //    Match [...] groups
                    "|(?<a>[A-F0-9]+)");// Finally, match HEX Byte characters

    // The trick here is that whatever is NOT captured by first 3 groups (i.e: surrounded by brackets)
    // will get captured by the last one, which only allows unambiguous sequence characters.
    // This gives us a match for each unambiguous HEX sequence on each sub-sequence.
    // All we have to do is find the longest of them after that.

    // Inside the bracket groups, the way it works is by matching the opening bracket and then matching
    // 1 or more characters that are NOT a closing bracket: [^}]+
    // '.*' Can not be used as it will skip brackets in the middle in strings like:
    // {5}080287F12D{4-8}

    public SubSequence processSubSequence(String sequence, int subSeqMinOffset, Integer subSeqMaxOffset, int subSeqPos) {
        Matcher m = sequencePartsRegex.matcher(sequence);
        ArrayList<SubSeqMatch> matches = new ArrayList<>();
        while (m.find()) {
            SubSeqMatch match = processMatch(m);
            if (match != null) matches.add(match);
        }

        // If the offset is relative to EOF we reverse the order of the matches (this emulates processing from right to left)
        if (isEOFOffset()) {
            Collections.reverse(matches);
        }

        // Accumulate to find the longest unambiguous subseq and its index
        int longestIdx = 0;
        if (matches.size() < 1) return null;
        SubSeqMatch longest = null;
        for (int i = 0; i < matches.size(); i++) {
            SubSeqMatch lc = matches.get(i);
            if (lc.type.equals(SubSeqMatch.MatchType.ALPHA) && (longest == null || lc.sequence.length() >= longest.sequence.length())) {
                longestIdx = i;
                longest = matches.get(i);
            }
        }
        // Initialise sequence
        String seq = null;
        // Create fragments from the matches
        // Split into left and right to make it easier to flip the left side positions when we're done
        ArrayList<Fragment> rFrags = new ArrayList<>();
        ArrayList<Fragment> lFrags = new ArrayList<>();
        int positionCounter = 1;
        for (int i = 0; i < matches.size(); i++) {
            SubSeqMatch fc = matches.get(i);
            // if we're at the longest idx, store the sequence
            if (i == longestIdx) {
                seq = fc.sequence;
                // Also reset the position counter
                positionCounter = 1;
                continue;
            }
            FragmentType ft = isEOFOffset() ? FragmentType.RIGHT : FragmentType.LEFT;
            int offsetIdx = isEOFOffset() ? i - 1 : i + 1;
            ArrayList<Fragment> frags = lFrags;
            // check if we're on the right instead and adjust the variables accordingly
            if (i > longestIdx) {
                ft = isEOFOffset() ? FragmentType.LEFT : FragmentType.RIGHT;
                offsetIdx = isEOFOffset() ? i + 1 : i - 1;
                frags = rFrags;
            }

            // Act on the different types of
            switch (fc.type) {
                // round brackets, create a list of fragments with the same position for each of the or'ed byte sequences
                case ROUND -> {
                    processRounds(fc.sequence, ft, positionCounter, offsetIdx, frags, matches);
                    positionCounter++;
                }
                // Alphanumeric and Square brackets groups are treated the same way.
                // We take the sequence and append any alpha or square groups that might follow.
                // There's a few cases when one of these groups didn't get processed by the previous group:
                // - When this is the first element
                // - When it appears right after the longest unambiguous fragment
                // - When it wasn't processed by the previous (it follows a curly or round group)
                // If it falls under this category, we process it as an alpha group
                // Otherwise we skip
                case ALPHA, SQUARE -> {
                    List<SubSeqMatch.MatchType> ts = List.of(SubSeqMatch.MatchType.ALPHA, SubSeqMatch.MatchType.SQUARE);
                    boolean isFirst = i == 0;
                    boolean isAfterLongest = i == longestIdx + 1;
                    boolean processedByPrevious = !isFirst && ts.contains(matches.get(i - 1).type);
                    if (isFirst || isAfterLongest || !processedByPrevious) {
                        processAlpha(fc.sequence, ft, positionCounter, offsetIdx, i, frags, matches, longestIdx);
                        positionCounter++;
                    }
                }
                // Curlys are processed by the sequence before or after them so here we do nothing
                case CURLY -> {
                }
            }
        }

        // Reverse the positions in the left fragments because we are supposed to count from right to left
        Collections.reverse(lFrags);
        positionCounter = 0;
        Integer lastPosition = 1;
        for (Fragment f : lFrags) {
            Integer initialPosition = f.position;
            if (!initialPosition.equals(lastPosition) || positionCounter == 0) {
                positionCounter++;
            }
            f.setPosition(positionCounter);
            lastPosition = initialPosition;
        }
        // The relevant fragments for calculating minFragLength depends on if we're counting form the BOF or EOF
        // if we count from the beginning of file (BOF) we take the left fragments, otherwise the right ones.
        // but because we reverse the sequence at the start, here we can always pass the so-called "left" fragments
        // even if in reality they appear to the right of the sequence
        Integer fraglen = calcMinFragLength(lFrags);
        lFrags.addAll(rFrags); // from this point onwards lFrags has all fragments, not just left
        return new SubSequence(seq, null, null, lFrags, fraglen, subSeqPos, subSeqMinOffset, subSeqMaxOffset);
    }

    private Integer[] processOffsets(String seq) {
        String clean = seq.replaceAll("[{}]", "");
        Integer[] offsets = {0, 0};
        if (clean.contains("-")) {
            String[] sMParts = clean.split("-");
            offsets[0] = Integer.parseInt(sMParts[0]);
            offsets[1] = Integer.parseInt(sMParts[1]);
        } else {
            offsets[0] = Integer.parseInt(clean);
            offsets[1] = null;
        }
        return offsets;
    }

    private void processRounds(String sequence, FragmentType ft, int position, int offsetIdx, ArrayList<Fragment> frags, ArrayList<SubSeqMatch> matches) {
        String clean = sequence.replaceAll("[()]", "");
        String[] parts = clean.split("\\|");
        Integer[] offsets = {0, 0};
        if (offsetIdx >= 0 && offsetIdx < matches.size() && matches.get(offsetIdx).type.equals(SubSeqMatch.MatchType.CURLY)) {
            offsets = processOffsets(matches.get(offsetIdx).sequence);
            // in fragments when there is no maxOffset, maxOffset = minOffset
            if (offsets[1] == null) {
                offsets[1] = offsets[0];
            }
        }
        for (String p : parts) {
            frags.add(new Fragment(ft, p, offsets[0], offsets[1], position));
        }
    }

    private void processAlpha(String sequence, FragmentType ft, int position, int offsetIdx, int currentIdx, ArrayList<Fragment> frags, ArrayList<SubSeqMatch> matches, int longestIdx) {
        // concatenate all the squares or alpha groups after this one
        StringBuilder seq = new StringBuilder(sequence);
        int nextIdx = currentIdx + 1;
        // check if there's a next one and it is before the longest subsequence
        SubSeqMatch nextMatch = matches.size() > nextIdx && nextIdx < longestIdx ? matches.get(nextIdx) : null;
        List<SubSeqMatch.MatchType> ts = List.of(SubSeqMatch.MatchType.SQUARE, SubSeqMatch.MatchType.ALPHA);
        while (nextMatch != null && ts.contains(nextMatch.type)) {
            // for left fragments concatenating squares shifts the index of a possible offset
            if (ft == FragmentType.LEFT) {
                offsetIdx++;
            }
            seq.append(nextMatch.sequence);
            nextIdx++;
            nextMatch = matches.size() > nextIdx ? matches.get(nextIdx) : null;
        }

        Integer[] offsets = {0, 0};
        if (offsetIdx >= 0 && offsetIdx < matches.size() && matches.get(offsetIdx).type.equals(SubSeqMatch.MatchType.CURLY)) {
            offsets = processOffsets(matches.get(offsetIdx).sequence);
            // in fragments when there is no maxOffset, maxOffset = minOffset
            if (offsets[1] == null) {
                offsets[1] = offsets[0];
            }
        }
        frags.add(new Fragment(ft, seq.toString(), offsets[0], offsets[1], position));
    }

    private SubSeqMatch processMatch(Matcher m) {
        List<String> matchTypes = List.of("r", "c", "s", "a");
        for (String t : matchTypes) {
            String temp = m.group(t);
            if (temp != null && !temp.isBlank()) {
                SubSeqMatch.MatchType et = switch (t) {
                    case "r" -> SubSeqMatch.MatchType.ROUND;
                    case "c" -> SubSeqMatch.MatchType.CURLY;
                    case "s" -> SubSeqMatch.MatchType.SQUARE;
                    case "a" -> SubSeqMatch.MatchType.ALPHA;
                    default -> throw new IllegalStateException();
                };
                return new SubSeqMatch(temp, m.start(t), m.end(t), et);
            }
        }
        return null;
    }

    private static class SubSeqMatch {
        private enum MatchType {ROUND, CURLY, SQUARE, ALPHA}

        private final int start;
        private final int end;
        private final String sequence;
        private final MatchType type;

        private SubSeqMatch(String sequence, int start, int end, MatchType type) {
            this.start = start;
            this.end = end;
            this.sequence = sequence;
            this.type = type;
        }

        @Override
        public String toString() {
            return "SubSeqMatch{" +
                    "start=" + start +
                    ", end=" + end +
                    ", sequence='" + sequence + '\'' +
                    ", type=" + type +
                    '}';
        }
    }

    private static final Pattern squareBracketGroupsRegex = Pattern.compile("\\[\\d+:\\d+\\]");

    private Integer calcMinFragLength(List<Fragment> frags) {
        int fraglen = 0;
        for (int i = 0; i < frags.size(); i++) {
            Fragment current = frags.get(i);
            // check if the following fragments have the same position (happens with OR'ed bytes)
            // if they do, find the smallest
            int nextIdx = i + 1;
            while (nextIdx < frags.size() && frags.get(nextIdx).position.equals(current.position)) {
                if (frags.get(nextIdx).value.length() < current.value.length()) {
                    current = frags.get(nextIdx);
                }
                // update i in order to skip the already processed fragments when we get to the end of the outer loop
                i = nextIdx;
                nextIdx++;
            }
            // Add the size of the fragment and its minOffset to the frag length
            // We have to check the value string for bytes in [] since
            // inside the square brackets there will be pairs of bytes on each side of the :
            // we basically replace square bracket groups with the smallest of the sides (this does not assume both sides will be equal in length)
            String clean = squareBracketGroupsRegex.matcher(current.value).replaceAll(match -> {
                String noBrackets = match.group().replaceAll("[\\[\\]]", "");
                return Arrays.stream(noBrackets.split(":")).min(Comparator.comparingInt(String::length)).orElse("");
            });
            // the size in bytes is the string length / 2 because every byte is 2 characters
            int byteSize = clean.length() / 2;
            fraglen += byteSize + current.minOffset;
        }
        return fraglen;
    }

    public static class SubSequence {
        private final String sequence;
        private final Integer defaultShift;
        private final List<Shift> shifts;
        private final List<Fragment> fragments;
        private final Integer minFragLength;
        private final Integer position;
        private final Integer subSeqMaxOffset;
        private final Integer subSeqMinOffset;

        public SubSequence(String sequence, Integer defaultShift, List<Shift> shifts, List<Fragment> fragments, Integer minFragLength, Integer position, Integer subSeqMinOffset, Integer subSeqMaxOffset) {
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

    public static class Shift {
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

    public static class Fragment {
        private final FragmentType type;
        private final String value;
        private Integer maxOffset;
        private Integer minOffset;
        private Integer position;

        public Fragment(FragmentType type, String value, Integer minOffset, Integer maxOffset, Integer position) {

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

        public void setMaxOffset(Integer maxOffset) {
            this.maxOffset = maxOffset;
        }

        public Integer getMinOffset() {
            return minOffset;
        }

        public void setMinOffset(Integer minOffset) {
            this.minOffset = minOffset;
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
            // Required (at least one)
            Resource signature = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.InternalSignature)));
            if (signature == null) {
                signature = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.ContainerFile)));
            }
            // Optionals
            String sequence = safelyGetStringOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.ByteSequence)));
            Resource position = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.ByteSequencePosition)));
            String positionName = null;
            if (position != null) {
                positionName = safelyGetStringOrNull(mu.getOneObjectOrNull(position, makeProp(RDFS.label)));
            }
            Integer offset = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.Offset)));
            Resource byteOrder = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.ByteOrder)));
            Integer maxOffset = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.MaxOffset)));
            Integer indirectOffsetLocation = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.IndirectOffsetLocation)));
            Integer indirectOffsetLength = safelyGetIntegerOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.ByteSequence.IndirectOffsetLocation)));

            return new ByteSequence(uri, signature, position, positionName, offset, sequence, byteOrder, maxOffset, indirectOffsetLocation, indirectOffsetLength);
        }
    }
}
