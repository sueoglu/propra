package output;

public class Output {
    private String seqID;
    private String sequence;
    protected String predictedSS;

    public Output() {
        this.seqID = seqID;
        this.sequence = sequence;
        this.predictedSS = predictedSS;


    }

    public String getPredictedSS() {
        return predictedSS;
    }

    public void setPredictedSS(String predictedSS) {
        this.predictedSS = predictedSS;
    }

    public String getSeqID() {
        return seqID;
    }

    public void setSeqID(String seqID) {
        this.seqID = seqID;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

}
