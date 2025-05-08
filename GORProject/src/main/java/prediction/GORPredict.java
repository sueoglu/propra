package prediction;

import model.GORModel;
import output.Output;
import utils.Config;
import utils.PredSecondaryStructure;

import java.util.ArrayList;
import java.util.List;

public class GORPredict {
    public GORModel model;
    public Output output;
    protected Config config;

    public GORPredict(GORModel model){
        this.model = model;
        this.output = new Output();
        config = new Config();
    }

    public PredSecondaryStructure predict(GORModel model, String sequence){
        StringBuilder secondaryStructure = new StringBuilder();
        List<Double> PHList = new ArrayList<>();
        List<Double> PEList = new ArrayList<>();
        List<Double> PCList = new ArrayList<>();

        int seqLength = sequence.length();
        int windowSize = config.getWINDOW_SIZE();
        int half_window_size = windowSize / 2;

        for (int i = 0; i < seqLength; i++){
            if (half_window_size > i || seqLength - i <= half_window_size){
                secondaryStructure.append("-");
                PHList.add(Double.NaN);
                PEList.add(Double.NaN);
                PCList.add(Double.NaN);
                continue;
            }
            double helixScore = 0.0;
            double sheetScore = 0.0;
            double coilScore = 0.0;

            helixScore = computeScore(model, sequence, 'H', i);
            sheetScore = computeScore(model, sequence, 'E', i);
            coilScore = computeScore(model, sequence, 'C', i);

            char predictedSS = getPredictedState(helixScore, sheetScore, coilScore);
            secondaryStructure.append(predictedSS);

            PHList.add(helixScore);
            PEList.add(sheetScore);
            PCList.add(coilScore);
        }
        PredSecondaryStructure prediction = new PredSecondaryStructure(secondaryStructure.toString(), PHList, PEList, PCList);
        return prediction;
    }
    public double computeScore(GORModel model, String sequence, char predictedSS, int index) {
        //StringBuilder secondaryStructure = new StringBuilder();
        int windowSize = config.getWINDOW_SIZE();
        int half_window_size = windowSize / 2;
        double score = 0;

        for (int i = 0 ; i < windowSize; i++) {
            int seqIndex = index + (i - half_window_size);
            if (seqIndex >= 0 && seqIndex < sequence.length()) {
                char window_aa = sequence.charAt(seqIndex);
                int aaIndex = config.getAMINOACIDS().indexOf(window_aa);
                if (aaIndex != -1) {
                    score += model.getScore1(i, window_aa, predictedSS);
                }
            }
        }
        return Math.exp(score) / (Math.exp(score) + 1);
    }
    private char getPredictedState(double helix, double sheet, double coil) {
        if (helix > sheet && helix > coil) {
            return 'H';
        } else if (sheet > helix && sheet > coil) {
            return 'E';
        } else {
            return 'C';
        }
    }

    public void setWindowSize(int size) {
        config.setWINDOW_SIZE(size);
    }
    /*
    public static void main(String[] args) {
        GORModel model = new GORModel();
        GORPredict predictor = new GORPredict(model);

        PredSecondaryStructure prediction = predictor.predict(model, "TTCCPSIVARSNFNVCRLPGTPEAICATYTGCIIIPGATCPGDYAN");
        System.out.println(prediction.getPredictedSS());
    }

 */


}
