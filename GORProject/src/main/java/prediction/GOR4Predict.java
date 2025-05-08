package prediction;

import model.GORModel;
import utils.Config;
import utils.PredSecondaryStructure;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class GOR4Predict extends GORPredict{

    public GOR4Predict(GORModel model){
        super(model);
    }

    public PredSecondaryStructure predict(GORModel model, String sequence) {
        StringBuilder secondaryStructure = new StringBuilder();
        List<Double> PHList = new ArrayList<>();
        List<Double> PEList = new ArrayList<>();
        List<Double> PCList = new ArrayList<>();

        int seqLength = sequence.length();
        int windowSize = config.getWINDOW_SIZE();
        int half_window_size = windowSize / 2;

        for (int i = 0; i < seqLength; i++){
            if (half_window_size > i || seqLength - i <= half_window_size) {
                secondaryStructure.append("-");
                PHList.add(Double.NaN);
                PEList.add(Double.NaN);
                PCList.add(Double.NaN);
                continue;
            }

            double helixScore = computeScore(model, sequence, 'H', i);
            double sheetScore = computeScore(model, sequence, 'E', i);
            double coilScore = computeScore(model, sequence, 'C', i);

            char predictedSS = getPredictedState(helixScore, sheetScore, coilScore);
            secondaryStructure.append(predictedSS);

            PHList.add(helixScore);
            PEList.add(sheetScore);
            PCList.add(coilScore);
        }

        return new PredSecondaryStructure(secondaryStructure.toString(), PHList, PEList, PCList);
    }

    public double computeScore(GORModel model, String sequence, char predictedSS, int index){
        int windowSize = config.getWINDOW_SIZE();
        int m = windowSize / 2;
        char centralAA = sequence.charAt(index);
        double score = 0.0;
        double score1 = 0.0;
        double score2 = 0.0;

        double factor1 = 2.0 / (2.0 * m + 1);
        double factor2 = (2.0 * m - 1) / (2.0 * m + 1);


        int pairIndex = 0;
        for (int k = -m; k <= m; k++) {
            int pairPosInSeq = index + k;
            if (pairPosInSeq < 0 || pairPosInSeq >= sequence.length()) continue;
            char pairAA = sequence.charAt(pairPosInSeq);

            for (int l = k + 1; l <= m; l++) {
                int countingPosInSeq = index + l;
                if (countingPosInSeq < 0 || countingPosInSeq >= sequence.length()) continue;
                char countingAA = sequence.charAt(countingPosInSeq);

                if (config.getAMINOACIDS().indexOf(countingAA) != -1 &&
                        config.getAMINOACIDS().indexOf(centralAA) != -1 &&
                        config.getAMINOACIDS().indexOf(pairAA) != -1) {

                    score1 += model.getScore4(l + m, countingAA, predictedSS, centralAA, pairAA, pairIndex);
                }
            }
            score2 += model.getScore5(k + m, pairAA, predictedSS, centralAA);
            pairIndex++;
        }

        score = factor1 *  score1 - factor2 * score2;
        // from log odds back to probability
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

}