package utils;

import java.util.ArrayList;
import java.util.List;

public class PredSecondaryStructure {
    private String aa;
    private String predictedSS;
    private String postprocessedPrediction;
    private List<Double> HP;
    private List<Double> EP;
    private List<Double> CP;
    private PostProcessor processor;


    public PredSecondaryStructure(String predictedSS, List<Double> HP, List<Double> EP, List<Double> CP) {
        this.predictedSS = predictedSS;
        this.HP = HP;
        this.CP = CP;
        this.EP = EP;
        processor = new PostProcessor();

        if (Config.postProcessing) {
            predictedSS = PostProcessor.processPrediction(predictedSS);
        }
    }

    public PredSecondaryStructure() {
        this.aa = ""; //
        this.predictedSS = "";
        this.HP = new ArrayList<>();
        this.EP = new ArrayList<>();
        this.CP = new ArrayList<>();

    }

    public void postProcess() {
        postprocessedPrediction = processor.processPrediction(predictedSS);
    }



    public List<Double> getHP() {
        return HP;
    }

    public List<Double> getEP() {
        return EP;
    }

    public List<Double> getCP() {
        return CP;
    }

    public String getPredictedSS() {
        return predictedSS;
    }

    public String getAa() {
        return aa;
    }

    public void setPredictedSS(String predictedSS) {
        this.predictedSS = predictedSS;
    }

    public void setAa(String aa) {
        this.aa = aa;
    }

    public void setEP(List<Double> EP) {
        this.EP = EP;
    }

    public void setHP(List<Double> HP) {
        this.HP = HP;
    }

    public void setCP(List<Double> CP) {
        this.CP = CP;
    }
    public String getPostprocessedPrediction() {
        return postprocessedPrediction;
    }

}
