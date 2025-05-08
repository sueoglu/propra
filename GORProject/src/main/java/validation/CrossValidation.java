package validation;

import model.GORModel;
import prediction.*;
import training.*;
import utils.*;

import java.util.*;


public class CrossValidation {
    private int folds;
    private boolean shuffle; //  random shuffle y/n
    private List<List<ProteinSeq>> foldSets;
    private String gorType;
    private boolean postpr;

    public CrossValidation(int folds, boolean shuffle, String gorType) {
        this.folds = folds;
        this.shuffle = shuffle;
        this.gorType = gorType;
        this.foldSets = new ArrayList<>();

    }

    public List<List<ProteinSeq>> runCV(List<ProteinSeq> dataset) {
        List<List<ProteinSeq>> testFoldPredictions = new ArrayList<>();

        for (int i = 0; i < folds; i++) {
            splitData(dataset);

            for (int j = 0; j < folds; j++) {
                List<ProteinSeq> testSet = foldSets.get(j);
                List<ProteinSeq> trainSet = new ArrayList<>();

                // Create training set (all folds except the i-th fold)
                for (int k = 0; k < folds; k++) {
                    if (j != k) {
                        trainSet.addAll(foldSets.get(k));
                    }
                }

                GORTrain gorTrain = createGORTrain(trainSet);
                GORModel model = gorTrain.GORTrain();
                GORPredict predictor = createGORPredictor(model);


                for (ProteinSeq seq : testSet) {
                    PredSecondaryStructure prediction = predictor.predict(model, seq.getSequence());

                    if (postpr) {
                        prediction.postProcess();
                    }

                    seq.setPredictedSS(prediction);

                }
                testFoldPredictions.add(testSet);
            }
        }
        return testFoldPredictions;
    }

    // GOR 5
    public List<List<ProteinSeq>> runCV(Map<String, HashMap<String, List<String>>> mapOfAlignments, List<ProteinSeq> dataset) {
        List<List<ProteinSeq>> testFoldPredictions = new ArrayList<>();

        // set alignemts for proteins
        for (String id : mapOfAlignments.keySet()) {
            HashMap<String, List<String>> queryProtein = mapOfAlignments.get(id);
            List<String> alignments = queryProtein.get("Alignments");

            for (ProteinSeq prot : dataset) {
                if (id.equals(prot.getId())) {
                    prot.setAlignment(alignments);
                }
            }

        }

        for (int i = 0; i < folds; i++) {
            splitData(dataset);

            for (int j = 0; j < folds; j++) {
                List<ProteinSeq> testSet = foldSets.get(j);
                List<ProteinSeq> trainSet = new ArrayList<>();

                // Create training set (all folds except the i-th fold)
                for (int k = 0; k < folds; k++) {
                    if (k != j) {
                        trainSet.addAll(foldSets.get(k));
                    }
                }

                GORTrain gorTrain = createGORTrain(trainSet);
                GORModel model = gorTrain.GORTrain();
                GOR5Predict predictor = new GOR5Predict(model);

                for (ProteinSeq protein : testSet) {
                    List<String> sequences = new ArrayList<>();
                    sequences.add(protein.getSequence());
                    sequences.addAll(protein.getAlignment());

                    PredSecondaryStructure prediction = predictor.predict(model, gorType, sequences);

                    if (postpr) {
                        prediction.postProcess();
                    }

                    protein.setPredictedSS(prediction);
                }
                testFoldPredictions.add(testSet);
            }
        }

        return testFoldPredictions;
    }



    public void splitData(List<ProteinSeq> dataset) {
        if (shuffle) {
            Collections.shuffle(dataset);
        }

        int foldSize = dataset.size() / folds;
        for (int i = 0; i < folds; i++) {
            int start = i * foldSize;
            int end = (i == folds - 1) ? dataset.size() : (i + 1) * foldSize;
            foldSets.add(new ArrayList<>(dataset.subList(start, end)));
        }
    }


    public void setPostpr(boolean postpr) {
        this.postpr = postpr;
    }

    private GORTrain createGORTrain(List<ProteinSeq> trainSet) {
        return switch (gorType) {
            case "gor1" -> new GOR1(trainSet);
            case "gor3" -> new GOR3(trainSet);
            case "gor4" -> new GOR4(trainSet);
            default -> null;
        };
    }

    private GORPredict createGORPredictor(GORModel model) {
  return switch (gorType) {
            case "gor1" -> new GORPredict(model);
            case "gor3" -> new GOR3Predict(model);
            case "gor4" -> new GOR4Predict(model);
            default -> null;
        };
    }

}
