package prediction;
import model.GORModel;
import utils.Config;
import utils.PredSecondaryStructure;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GOR5Predict extends GORPredict {

    private GORModel model;

    public GOR5Predict(GORModel model) {
        super(model);
    }

    public PredSecondaryStructure predict(GORModel model, String modelType, List<String> sequences) {
        PredSecondaryStructure secondaryStructure = new PredSecondaryStructure();
        StringBuilder ss = new StringBuilder(); //final

        double helixScoreForPositionX = 0.0;
        double sheetScoreForPositionX = 0.0;
        double coilScoreForPositionX = 0.0;
        int length = 0;
        List<PredSecondaryStructure> predictions = new ArrayList<>(); //for each alignment/seq

        for (String sequence : sequences) {
            HashMap<Character, Integer> AAandPos = new HashMap<>(); //aas and positions
            List<Integer> positionsOfGaps = new ArrayList<>();
            PredSecondaryStructure tmpSS = new PredSecondaryStructure(); //ss for i-te seq
            length = sequence.length();

            if (sequence.contains("-")) { //handle gaps
                for (int i = 0; i < sequence.length(); i++) {
                    AAandPos.put(sequence.charAt(i), i);
                    if (sequence.charAt(i) == '-') {
                        positionsOfGaps.add(i);
                    }
                }
                sequence = sequence.replace("-", "").trim();
            }
            if (modelType.equals("gor1")) { //select gor model
                GORPredict gor1Predict = new GORPredict(model);
                tmpSS = gor1Predict.predict(model, sequence);
            }
            else if (modelType.equals("gor3")) {
                GOR3Predict gor3Predict = new GOR3Predict(model);
                tmpSS = gor3Predict.predict(model, sequence);
            }
            else if(modelType.equals("gor4")) {
                GOR4Predict gor4Predict = new GOR4Predict(model);
                tmpSS = gor4Predict.predict(model, sequence);
            }

            tmpSS.setAa(addGapsBack(sequence, positionsOfGaps)); //ss with gaps
            addGapsToLists(tmpSS.getHP(), positionsOfGaps); //add gaps to helix prob list
            addGapsToLists(tmpSS.getEP(), positionsOfGaps);
            addGapsToLists(tmpSS.getCP(), positionsOfGaps);

            predictions.add(tmpSS);
        }

        List<Double> finalHP = new ArrayList<>();
        List<Double> finalEP = new ArrayList<>();
        List<Double> finalCP = new ArrayList<>();

        int windowSize = config.getWINDOW_SIZE();
        int half_window_size = windowSize / 2;

        for (int i = 0; i < length; i++) { //
            if (half_window_size > i || length - i <= half_window_size){
                ss.append("-");
                continue;
            }

            int countValidScore = 0;
            for (PredSecondaryStructure pred : predictions) { //für jede tmpss
                if (i < pred.getHP().size()) {
                    double hp = pred.getHP().get(i);
                    double ep = pred.getEP().get(i);
                    double cp = pred.getCP().get(i);
                    if (!Double.isNaN(hp) && !Double.isNaN(ep) && !Double.isNaN(cp)) {
                        helixScoreForPositionX += hp;
                        sheetScoreForPositionX += ep;
                        coilScoreForPositionX += cp;
                        countValidScore++;
                    }
                }
            }
            if (countValidScore > 0) {
                helixScoreForPositionX /= countValidScore;
                sheetScoreForPositionX /= countValidScore;
                coilScoreForPositionX /= countValidScore;
            }

            char predictedSS = getPredictedState(helixScoreForPositionX, sheetScoreForPositionX, coilScoreForPositionX);
            ss.append(predictedSS);

            finalHP.add(helixScoreForPositionX); //final prob scores
            finalEP.add(sheetScoreForPositionX);
            finalCP.add(coilScoreForPositionX);
        }

        secondaryStructure.setPredictedSS(ss.toString());
        secondaryStructure.setHP(finalHP);
        secondaryStructure.setEP(finalEP);
        secondaryStructure.setCP(finalCP);

        return secondaryStructure;
    }
    public static String addGapsBack(String sequence, List<Integer> positionsOfGaps) {
        StringBuilder restoredAli = new StringBuilder(sequence);
        for (int pos : positionsOfGaps) {
            restoredAli.insert(pos, '-');
        }
        //System.out.println(restoredAli);
        return restoredAli.toString();
    }

    public static void addGapsToLists(List<Double> scoreList, List<Integer> positionsOfGaps) {
        for (int gapPos : positionsOfGaps) {
            scoreList.add(gapPos, Double.NaN); // NaN for a gap
        }
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





/*
    public String predictGOR5_3(GORModel model, String sequence, List<String> alignments) {
        StringBuilder secondaryStructure = new StringBuilder();
        int seqLength = sequence.length();
        int windowSize = config.getWINDOW_SIZE();
        int half_window_size = windowSize / 2;
        String aminoAcids = config.getAMINOACIDS();

        for (int i = 0; i < seqLength; i++) {
            int validAlignmentCount = 0;

            if (half_window_size > i || seqLength - i <= half_window_size) {
                secondaryStructure.append("-");
                continue;
            }
            double helixScore = 0.0;
            double sheetScore = 0.0;
            double coilScore = 0.0;

            for (int col = 0; col < windowSize; col++) {
                int seqIndex = i - half_window_size + col;
                char windowAA = sequence.charAt(seqIndex);
                char centralAA = sequence.charAt(i);

                if (aminoAcids.indexOf(windowAA) != -1) {
                    helixScore += model.getScore3(col, windowAA, 'H', centralAA);
                    sheetScore += model.getScore3(col, windowAA, 'E', centralAA);
                    coilScore += model.getScore3(col, windowAA, 'C', centralAA);
                }
            }
            for (String alignedSeq : alignments) {
                if(alignedSeq.length() != seqLength){
                    continue;
                }
                if (alignedSeq.charAt(i) == '-'){
                    continue;
                }
                validAlignmentCount++;

                for (int col = 0; col < windowSize; col++) {
                    int seqIndex = i - half_window_size + col;
                    char windowAA = alignedSeq.charAt(seqIndex);
                    char centralAA = alignedSeq.charAt(i);

                    if (aminoAcids.indexOf(windowAA) != -1 && windowAA != '-') {
                        helixScore += model.getScore3(col, windowAA, 'H', centralAA);
                        sheetScore += model.getScore3(col, windowAA, 'E', centralAA);
                        coilScore += model.getScore3(col, windowAA, 'C', centralAA);
                    }
                }
            }
            if (validAlignmentCount > 0) {
                helixScore /= validAlignmentCount;
                sheetScore /= validAlignmentCount;
                coilScore /= validAlignmentCount;
            }
            char predictedSS = getPredictedState(helixScore, sheetScore, coilScore);
            secondaryStructure.append(predictedSS);
        }
        return secondaryStructure.toString();
    }

    public String predictGOR5_1(GORModel model, String sequence, List<String> alignments) { //nur List<>
        StringBuilder secondaryStructure = new StringBuilder();
        StringBuilder probilitiesSS = new StringBuilder();

        int seqLength = sequence.length();
        int windowSize = config.getWINDOW_SIZE();
        int half_window_size = windowSize / 2;

        for (int i = 0; i < seqLength; i++) {
            if (half_window_size > i || seqLength - i <= half_window_size) {
                secondaryStructure.append("-");
                continue;
            }
            double helixScore = 0;
            double sheetScore = 0;
            double coilScore = 0;
            int validAlignmentCount = 0;

            for (int col = 0; col < windowSize; col++) {
                int seqIndex = i - half_window_size + col;
                char window_aa = sequence.charAt(seqIndex);

                int index_window = config.getAMINOACIDS().indexOf(window_aa);// index of aa in window range;
                if (index_window != -1) {
                    helixScore += model.getScore1(col, window_aa, 'H');
                    sheetScore += model.getScore1(col, window_aa, 'E');
                    coilScore += model.getScore1(col, window_aa, 'C');
                }
            }

            for (String alignedSeq : alignments){
                if(alignedSeq.length() != seqLength){
                    continue;
                }
                if (alignedSeq.charAt(i) == '-'){
                    continue;
                }
                validAlignmentCount++;

                for (int col = 0; col < windowSize; col++) {
                    int seqIndex = i - half_window_size + col;
                    char window_aa = alignedSeq.charAt(seqIndex);

                    int index_window = config.getAMINOACIDS().indexOf(window_aa);
                    if (index_window != -1 && window_aa != '-') {
                        helixScore += model.getScore1(col, window_aa, 'H');
                        sheetScore += model.getScore1(col, window_aa, 'E');
                        coilScore += model.getScore1(col, window_aa, 'C');
                    }
                }
            }
            if (validAlignmentCount > 0) {
                helixScore = helixScore / validAlignmentCount;
                sheetScore = sheetScore / validAlignmentCount;
                coilScore = coilScore / validAlignmentCount;
            }

            probilitiesSS.append(helixScore).append("\t")
                    .append(sheetScore).append("\t")
                    .append(coilScore).append("\n")
            ;
            char predictedSS = getPredictedState(helixScore, sheetScore, coilScore);
            secondaryStructure.append(predictedSS);
            //System.out.println("AA: " + aminoAcid + " | H: " + helixScore + " | E: " + sheetScore + " | C: " + coilScore);
        }
        //System.out.println(probilitiesSS);
        return secondaryStructure.toString();
    }

    private char getPredictedState(double helix, double sheet, double coil) {
        if (helix > sheet && helix > coil) {
            return 'H';
        } else if (sheet > helix && sheet > coil) {
            return 'E';
        } else {
            return 'C';
        }
    }  */


