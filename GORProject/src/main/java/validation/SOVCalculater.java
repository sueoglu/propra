package validation;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SOVCalculater {
    private String observedSeq;
    private String predictedSeq;
    private List<Segment> observedH;
    private List<Segment> observedE;
    private List<Segment> observedC;
    private List<Segment> predictedH;
    private List<Segment> predictedE;
    private List<Segment> predictedC;

    public SOVCalculater() {

    }

    public double calculateSOVTotal(String seqObserved, String seqPredicted) {
        this.observedSeq = seqObserved;
        this.predictedSeq = seqPredicted;

        observedH = findSegments(observedSeq, 'H');
        observedE = findSegments(observedSeq, 'E');
        observedC = findSegments(observedSeq, 'C');

        predictedH = findSegments(predictedSeq, 'H');
        predictedE = findSegments(predictedSeq, 'E');
        predictedC = findSegments(predictedSeq, 'C');

        char[] ssTypes = {'H', 'C', 'E'};
        double factor = 100.0;

        Map<Character, List<Segment>> observedSegments = new HashMap<>();
        Map<Character, List<Segment>> predictedSegments = new HashMap<>();

        for (char ss : ssTypes) {
            observedSegments.put(ss, getSegmentsForSS(ss, true));
            predictedSegments.put(ss, getSegmentsForSS(ss, false));
        }

        double totalN = 0.0;
        double totalSegmentSum = 0.0;

        for (char ss : ssTypes) {
            List<Pair<Segment, Segment>> overlappingPairs = findOverlappingPairs(observedSegments.get(ss), predictedSegments.get(ss));
            List<Segment> nonOverlappingObserved = findNonOverlappingObserved(observedSegments.get(ss), overlappingPairs);

            totalN += computeN(overlappingPairs, nonOverlappingObserved);
            totalSegmentSum += computeSOVSum(overlappingPairs);
        }

        return factor * (1.0/totalN) * totalSegmentSum;
    }

    public double calculateSOVSingleSS(String seqObserved, String seqPredicted, char ss) {
        this.observedSeq = seqObserved;
        this.predictedSeq = seqPredicted;

        observedH = findSegments(observedSeq, 'H');
        observedE = findSegments(observedSeq, 'E');
        observedC = findSegments(observedSeq, 'C');

        predictedH = findSegments(predictedSeq, 'H');
        predictedE = findSegments(predictedSeq, 'E');
        predictedC = findSegments(predictedSeq, 'C');

        int factor = 100;

        List<Segment> observedSegments = getSegmentsForSS(ss, true);
        List<Segment> predictedSegments = getSegmentsForSS(ss, false);


        List<Pair<Segment, Segment>> overlappingPairs = findOverlappingPairs(observedSegments, predictedSegments);
        List<Segment> nonOverlappingObserved = findNonOverlappingObserved(observedSegments, overlappingPairs);

        double N = computeN(overlappingPairs, nonOverlappingObserved);
        double sumOverSegments = computeSOVSum(overlappingPairs);

        return (double) factor * (1.0/N) * sumOverSegments;
    }

    private double computeN(List<Pair<Segment, Segment>> overlappingPairs, List<Segment> nonOverlappingObserved) {
        double N = 0.0;
        for (Pair<Segment,Segment> pair : overlappingPairs) {
            Segment observedSeg = pair.getKey(); // Key = observed SS Seq
            int observedSegLength = observedSeg.getLength();
            N += observedSegLength;
        }
        for (Segment segment : nonOverlappingObserved) {
            N += segment.getLength();
        }

        return N;
    }

    private List<Pair<Segment, Segment>> findOverlappingPairs(List<Segment> observedSegments, List<Segment> predictedSegments) {
        List<Pair<Segment, Segment>> overlappingPairs = new ArrayList<>();
        for (Segment observed : observedSegments) {

            for (Segment predicted : predictedSegments) {
                if (isOverlapping(observed, predicted)) {
                    overlappingPairs.add(new MutablePair<>(observed, predicted));

                }
            }

        }
        return overlappingPairs;
    }


    /** calculate sum component */
    private double computeSOVSum(List<Pair<Segment, Segment>> overlappingPairs) {
        int delta = 0;
        double sumOverSegments = 0.0;

        for (Pair<Segment,Segment> pair : overlappingPairs) {
            Segment observedSeg = pair.getKey(); // Key = observed SS Seq
            Segment predictedSeg = pair.getValue();

            int observedSegLength = observedSeg.getLength();
            int predictedSegLength = predictedSeg.getLength();

            // Overlap
            int minOverlap = getMinOverlap(observedSeg, predictedSeg);
            int maxOverlap = getMaxOverlap(observedSeg, predictedSeg);

            // delta
            delta = calcDelta(maxOverlap,minOverlap, observedSegLength, predictedSegLength);
            sumOverSegments += (((double) minOverlap + delta) / maxOverlap) * observedSegLength;

        }
        return sumOverSegments;
    }

    private List<Segment> findNonOverlappingObserved(List<Segment> observed, List<Pair<Segment, Segment>> overlappingPairs) {
        List<Segment> nonOverlapping = new ArrayList<>();

        for (Segment obs : observed) {
            boolean isOverlapped = overlappingPairs.stream().anyMatch(p -> p.getKey().equals(obs));
            if (!isOverlapped) {
                nonOverlapping.add(obs);
            }
        }
        return nonOverlapping;
    }

    private int calcDelta(int maxOverlap, int minOverlap, int lengthObserved, int lengthPredicted) {
        return Math.min(Math.min(maxOverlap - minOverlap, minOverlap), Math.min(lengthObserved/2, lengthPredicted/2));
    }

    private boolean isOverlapping(Segment observed, Segment predicted) {
        return !(observed.getEnd() < predicted.getStart() || observed.getStart() > predicted.getEnd());
    }

    private int getMaxOverlap(Segment observed, Segment predicted) {
        int minStart = Math.min(observed.getStart(), predicted.getStart());
        int maxEnd = Math.max(observed.getEnd(), predicted.getEnd());

        return maxEnd - minStart + 1;
    }


    private int getMinOverlap(Segment observed, Segment predicted) {
        int maxStart = Math.max(observed.getStart(), predicted.getStart());
        int minEnd = Math.min(observed.getEnd(), predicted.getEnd());

        return minEnd - maxStart + 1;
    }

    public List<Segment> findSegments(String sequence, char ss) {
        List<Segment> result = new ArrayList<>();

        String regex = ss + "+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sequence);

        while (matcher.find()) {
            String matchedSegment = matcher.group();
            int start = matcher.start();
            int end = matcher.end() - 1;
            result.add(new Segment(matchedSegment, start, end));
        }

        return result;
    }

    private List<Segment> getSegmentsForSS(char ss, boolean isObserved) {
        switch (ss) {
            case 'H':
                return isObserved ? observedH : predictedH;
            case 'E':
                return isObserved ? observedE : predictedE;
            case 'C':
                return isObserved ? observedC : predictedC;
            default:
                return new ArrayList<>();
        }
    }

    static class Segment {
        private String sequence;
        private int start;
        private int end;
        private int length;

        public Segment(String sequence, int start, int end) {
            this.sequence = sequence;
            this.start = start;
            this.end = end;
            this.length = end - start + 1;
        }

        public String getSequence() {
            return sequence;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public int getLength() {
            return length;
        }
    }


}
