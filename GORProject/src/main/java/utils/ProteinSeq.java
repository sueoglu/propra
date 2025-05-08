package utils;

import java.util.List;

public class ProteinSeq {
    private String id;
    private String sequence;
    private String secondaryStructure;
    private List<String> alignments;
    private PredSecondaryStructure predictedSS;

    public ProteinSeq(String id, String sequence, String secondaryStructure) {
        this.id = id;
        this.sequence = sequence;
        this.secondaryStructure = secondaryStructure;
        predictedSS = new PredSecondaryStructure();
    }

    public ProteinSeq(String id, String sequence, PredSecondaryStructure prediction) {
        this.id = id;
        this.sequence = sequence;
        predictedSS = prediction;
    }

    public ProteinSeq(String id, String sequence) {
        this.id = id;
        this.sequence = sequence;
    }

    public String getSequence() {
        return sequence;
    }

    public String getSecondaryStructure() {
        return secondaryStructure;
    }
    public String getId() {
        return id;
    }

    public PredSecondaryStructure getPredictedSS() {
        return predictedSS;
    }

    public void setPredictedSS(PredSecondaryStructure predictedSS) {
        this.predictedSS = predictedSS;
    }

    public List<String> getAlignment() {
        return alignments;
    }

    public void setAlignment(List<String> alignment) {
        this.alignments = alignment;
    }
}
