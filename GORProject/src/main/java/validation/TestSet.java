/*

package validation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import Algorithms.NeedlemanWunsch;
import Algorithms.SmithWaterman;
import Matrix.ScoreMatrix;
import Parsers.CommandParser.Mode;
import org.apache.commons.lang3.StringUtils;


public class TestSet {
    private Map<String, String> testSeq;
    private double threshold;


    public TestSet(double threshold) {
        this.testSeq = new HashMap<>();
        this.threshold = threshold;

    }
    public TestSet(){
    }

    public Map<String, String> getTestSeq() {
        return testSeq;
    }
    public void setTestSeq(Map<String, String> testSeq) {
        this.testSeq = testSeq;
    }
    public double getThreshold() {
        return threshold;
    }
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }


    public double computeAliSimilarity(String seq1, String seq2){

        String cleanSeq1 = seq1.replaceAll("[^ACDEFGHIKLMNPQRSTVWY]", "");
        String cleanSeq2 = seq2.replaceAll("[^ACDEFGHIKLMNPQRSTVWY]", "");

        ScoreMatrix matrix = new ScoreMatrix(new File("/Users/oykusuoglu/PythonProjects/ProPraBlock/pp24_5/Aufgaben/Alignment/Alignment/files_for_testing/Matrices/dayhoff.mat"));
        NeedlemanWunsch nw = new NeedlemanWunsch(cleanSeq1, cleanSeq2,-1, matrix, Mode.LOCAL, false);

        double score = nw.getScore();
        double maxLength = Math.max(cleanSeq1.length(), cleanSeq2.length());

        return score / maxLength;
    }

    public boolean isSimilar(String seq1, String seq1ID, Map<String, String> trainset) {

        int similarCount = 0;
        int totalComparisons = 0;

        for(Map.Entry<String, String> entry : trainset.entrySet()){
            String trainSeqId = entry.getKey();
            String trainingSeq = entry.getValue();
            double sim = computeAliSimilarity(seq1, trainingSeq);

            totalComparisons++;
            if(sim >= threshold){
                similarCount++;
            }
        }
        return similarCount > totalComparisons * 0.5;
    }

    public static TestSet generateTestSet(Map<String, String> data,Map<String, String> trainingSet,  double threshold) {
        TestSet testSet = new TestSet(threshold);
        for(Map.Entry<String, String> entry : data.entrySet()) {
            String seqID = entry.getKey();
            String seq = entry.getValue();

            if(!testSet.isSimilar(seq, seqID, trainingSet)){
                testSet.testSeq.put(seqID, seq);
            }

        }
        return testSet;
    }

    public int getTestSize(){
        return testSeq.size();
    }

    public Map<String, String> generateTrainingSet(Map<String, String> originalDataset) {
        Map<String, String> trainingSet = new HashMap<>(originalDataset);

        for (String testId : testSeq.keySet()) {
            trainingSet.remove(testId);
        }

        return trainingSet;
    }
}

*/
